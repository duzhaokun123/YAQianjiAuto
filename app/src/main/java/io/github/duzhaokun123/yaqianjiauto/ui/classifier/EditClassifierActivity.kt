package io.github.duzhaokun123.yaqianjiauto.ui.classifier

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibleForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.model.toClassifier
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import io.github.duzhaokun123.yaqianjiauto.utils.times
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EditClassifierActivity : ComponentActivity() {
    companion object {
        const val EXTRA_INDEX = "index"
    }

    private val classifierDataDao by lazy { (application as Application).db.classifierDataDao() }

    private val toImportCode = Channel<String?>()
    private var toExportCode: String? = null

    private val fileImportLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) return@registerForActivityResult
            runIO {
                contentResolver.openInputStream(it)?.reader()?.apply {
                    toImportCode.send(readText())
                    close()
                }
            }
        }
    private val fileExportLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
            if (it == null) return@registerForActivityResult
            runIO {
                contentResolver.openOutputStream(it)?.writer()?.apply {
                    write(toExportCode ?: return@runIO)
                    close()
                    toExportCode = null
                    TipUtil.showToast("exported")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val index = intent.getLongExtra(EXTRA_INDEX, -1)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val classifierData by classifierDataDao.getByIndexFlow(index)
                        .collectAsState(null)
                    EditClassifier(if (index == -1L) ClassifierData.empty() else classifierData)
                }
            }
        }
    }

    @Composable
    fun EditClassifier(classifierData: ClassifierData?) {
        classifierData ?: return
        var type by remember { mutableStateOf(classifierData.type) }
        val typeStr = ClassifierData.typeToStr(type)
        var name by remember { mutableStateOf(classifierData.name) }
        var description by remember { mutableStateOf(classifierData.description) }
        var code by remember { mutableStateOf(classifierData.code) }

        val loadedData by toImportCode.receiveAsFlow().collectAsState(initial = null)
        if (loadedData != null) {
            code = loadedData as String
            runMain {
                toImportCode.send(null)
            }
        }

        var isEdit by remember { mutableStateOf(false) }
        var showTestDialog by remember { mutableStateOf(false) }
        var parserResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Row {
                        var typeDropDown by remember { mutableStateOf(false) }
                        TextButton(onClick = { typeDropDown = true }) {
                            Text("type: $typeStr")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "type")
                        }
                        Text("view", modifier = Modifier.align(Alignment.CenterVertically))
                        Switch(checked = isEdit, onCheckedChange = { isEdit = it })
                        Text("edit", modifier = Modifier.align(Alignment.CenterVertically))
                        DropdownMenu(
                            expanded = typeDropDown,
                            onDismissRequest = { typeDropDown = false }) {
                            DropdownMenuItem(
                                text = { Text("empty") },
                                onClick = { type = ClassifierData.Type.Empty; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("js") },
                                onClick = { type = ClassifierData.Type.JS; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("yaml") },
                                onClick = { type = ClassifierData.Type.Yaml; typeDropDown = false })
                        }
                    }
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("name") },
                        singleLine = true
                    )
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("description") })
                }
                Column(Modifier.align(Alignment.TopEnd)) {
                    Row {
                        IconButton(onClick = {
                            val newClassifierData = classifierData.copy(
                                type = type,
                                name = name,
                                description = description,
                                code = code
                            )
                            runIO {
                                classifierDataDao.upsert(newClassifierData)
                                TipUtil.showToast("saved")
                            }
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "save")
                        }
                        var lastClickDelete by remember { mutableStateOf(0L) }
                        IconButton(onClick = {
                            if (System.currentTimeMillis() - lastClickDelete > 2000) {
                                lastClickDelete = System.currentTimeMillis()
                                TipUtil.showToast("再次点击删除")
                            } else {
                                runIO { classifierDataDao.delete(classifierData) }
                                finish()
                            }
                        }) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "delete")
                        }
                    }
                    TextButton(onClick = { fileImportLauncher.launch(arrayOf("*/*")) }) {
                        Icon(Icons.Default.FileOpen, contentDescription = "open")
                        Text("import")
                    }
                    TextButton(onClick = {
                        if (type == ClassifierData.Type.Empty) return@TextButton
                        toExportCode = code
                        Log.d("TAG", "a: $toExportCode")
                        fileExportLauncher.launch(
                            "${packageName}_${name}.${
                                when (type) {
                                    ClassifierData.Type.JS -> "js"
                                    ClassifierData.Type.Yaml -> "yaml"
                                    else -> "txt"
                                }
                            }"
                        )
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "download")
                        Text("export")
                    }
                    TextButton(onClick = { showTestDialog = true }) {
                        Icon(
                            Icons.Default.AccessibleForward,
                            contentDescription = stringResource(R.string.test)
                        )
                        Text(stringResource(R.string.test))
                    }
                }

            }

            if (type != ClassifierData.Type.Empty) {
                val parser = remember { PrettifyParser() }
                val themeState by remember { mutableStateOf(CodeThemeType.Monokai) }
                val theme = remember(themeState) { themeState.theme }
                if (isEdit) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("code") },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                } else {
                    val parsedCode = TextFieldValue(
                        parseCodeAsAnnotatedString(parser, theme, typeStr, code)
                    )
                    OutlinedTextField(
                        value = parsedCode,
                        onValueChange = {},
                        label = { Text("code") },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }

            if (showTestDialog) {
                TestDialog(ClassifierData(type, code, name, description),
                    { showTestDialog = false }, { parserResult = it })
            }

            if (parserResult != null) {
                ResultDialog(result = parserResult!!) {
                    parserResult = null
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewEditClassifier() {
        YA自动记账Theme {
            EditClassifier(ClassifierData(-1, "abcd" * 25, "name", "description"))
        }
    }

    @Composable
    fun TestDialog(
        classifierData: ClassifierData,
        onDismiss: () -> Unit,
        onResult: (Pair<Boolean, String>) -> Unit
    ) {
        var type by remember { mutableStateOf(1) }
        val typeStr = ParsedData.typeToStr(type)
        var account by remember { mutableStateOf("") }
        var balance by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }
        var remark by remember { mutableStateOf("") }
        var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
        var extras by remember { mutableStateOf(mapOf<String, String>()) }
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    classifierData.toClassifier().classify(
                        ParsedData(
                            type,
                            account,
                            balance.toDoubleOrNull() ?: 0.0,
                            target,
                            remark,
                            timestamp,
                            extras
                        ),
                        onClassified = { onResult(true to "$it") },
                        onError = { onResult(false to it) }
                    )
                }) {
                    Text("run")
                }
            },
            title = {
                Row {
                    Text(stringResource(R.string.test), modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        val clipboardManager =
                            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val parsedDataJson =
                            clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
                                ?: return@IconButton
                        runCatching {
                            gson.fromJson<ParsedData>(parsedDataJson)
                        }.onFailure {
                            TipUtil.showToast("not a parsed data")
                        }.onSuccess { parsedData ->
                            type = parsedData.type
                            account = parsedData.account
                            balance = parsedData.balance.toString()
                            target = parsedData.target
                            remark = parsedData.remark
                            timestamp = parsedData.timestamp
                            extras = parsedData.extras
                        }
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "paste")
                    }
                }
            },
            text = {
                Column {
                    Box {
                        var typeDropDown by remember { mutableStateOf(false) }
                        TextButton(onClick = { typeDropDown = true }) {
                            Text("type: $typeStr")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "type")
                        }
                        DropdownMenu(
                            expanded = typeDropDown,
                            onDismissRequest = { typeDropDown = false }) {
                            DropdownMenuItem(
                                text = { Text("expense") },
                                onClick = { type = ParsedData.Type.Expense; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("income") },
                                onClick = { type = ParsedData.Type.Income; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("transfer") },
                                onClick = { type = ParsedData.Type.Transfer; typeDropDown = false })
                        }
                    }
                    TextField(
                        value = account,
                        onValueChange = { account = it },
                        label = { Text("account") },
                        singleLine = true
                    )
                    TextField(
                        value = balance,
                        onValueChange = { balance = it },
                        label = { Text("balance") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("target") },
                        singleLine = true
                    )
                    TextField(
                        value = remark,
                        onValueChange = { remark = it },
                        label = { Text("remark") },
                        singleLine = true
                    )
                    TextField( // TODO: use date picker
                        value = timestamp.toString(),
                        onValueChange = { timestamp = it.toLongOrNull() ?: 0 },
                        label = { Text("timestamp") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    var extraKey by remember { mutableStateOf("") }
                    var extraValue by remember { mutableStateOf("") }
                    Row {
                        TextField(
                            value = extraKey,
                            onValueChange = { extraKey = it },
                            label = { Text("extra K") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = extraValue,
                            onValueChange = { extraValue = it },
                            label = { Text("V") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            extras = extras + (extraKey to extraValue)
                            extraKey = ""
                            extraValue = ""
                        }, modifier = Modifier.align(Alignment.CenterVertically)) {
                            Icon(Icons.Default.Add, contentDescription = "add")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(50.dp, 150.dp)
                            .fillMaxWidth()
                    ) {
                        items(extras.toList()) { (key, vale) ->
                            Row {
                                IconButton(onClick = { extras = extras - key }) {
                                    Icon(Icons.Default.Delete, contentDescription = "delete")
                                }
                                Text(
                                    "$key: $vale",
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            })
    }

    @Preview
    @Composable
    fun PreviewTestDialog() {
        YA自动记账Theme {
            TestDialog(ClassifierData.empty(), {}, {})
        }
    }

    @Composable
    fun ResultDialog(result: Pair<Boolean, String>, onDismiss: () -> Unit) {
        val (noError, message) = result
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("ok")
                }
            },
            title = {
                Text(if (noError) "noError" else "error")
            },
            text = {
                Text(
                    message, style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            })
    }
}