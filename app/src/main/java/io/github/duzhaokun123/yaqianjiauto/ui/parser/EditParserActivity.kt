package io.github.duzhaokun123.yaqianjiauto.ui.parser

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibleForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentPaste
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.ParserData
import io.github.duzhaokun123.yaqianjiauto.model.toParser
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import io.github.duzhaokun123.yaqianjiauto.utils.times
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EditParserActivity : ComponentActivity() {
    companion object {
        const val EXTRA_INDEX = "index"
    }

    private val parserDataDao by lazy { (application as Application).db.parserDataDao() }

    private val toImportData = Channel<String?>()
    private var toExportData: String? = null

    private val fileImportLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) return@registerForActivityResult
            runIO {
                contentResolver.openInputStream(it)?.reader()?.apply {
                    toImportData.send(readText())
                    close()
                }
            }
        }
    private val fileExportLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
            if (it == null) return@registerForActivityResult
            runIO {
                Log.d("TAG", "a: $toExportData")
                contentResolver.openOutputStream(it)?.writer()?.apply {
                    write(toExportData ?: return@runIO)
                    close()
                    toExportData = null
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
                    val parserData by parserDataDao.getByIndexFlow(index)
                        .collectAsState(initial = null)
                    EditParser(if (index == -1L) ParserData.empty() else parserData)
                }
            }
        }
    }

    @Composable
    fun EditParser(parserData: ParserData?) {
        parserData ?: return
        var type by remember { mutableStateOf(parserData.type) }
        val typeStr = ParserData.typeToStr(type)
        var packageName by remember { mutableStateOf(parserData.packageName) }
        var name by remember { mutableStateOf(parserData.name) }
        var description by remember { mutableStateOf(parserData.description) }
        var data by remember { mutableStateOf(parserData.data) }

        val loadedData by toImportData.receiveAsFlow().collectAsState(initial = null)
        if (loadedData != null) {
            data = loadedData as String
            runMain {
                toImportData.send(null)
            }
        }

        var isEdit by remember { mutableStateOf(false) }
        var showTestDialog by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
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
                                onClick = { type = ParserData.Type.Empty; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("js") },
                                onClick = { type = ParserData.Type.JS; typeDropDown = false })
                            DropdownMenuItem(
                                text = { Text("python") },
                                onClick = { type = ParserData.Type.Python; typeDropDown = false })
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
                    TextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("packageName") },
                        singleLine = true
                    )
                }
                Column(Modifier.align(Alignment.TopEnd)) {
                    Row {
                        IconButton(onClick = {
                            val newParserData =
                                parserData.copy(
                                    type = type,
                                    packageName = packageName,
                                    data = data,
                                    name = name,
                                    description = description
                                )
                            runIO {
                                parserDataDao.upsert(newParserData)
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
                                runIO { parserDataDao.delete(parserData) }
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
                        if (type == ParserData.Type.Empty) return@TextButton
                        toExportData = data
                        Log.d("TAG", "a: $toExportData")
                        fileExportLauncher.launch(
                            "${packageName}_${name}.${
                                when (type) {
                                    ParserData.Type.JS -> "js"
                                    ParserData.Type.Python -> "py"
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

            val parser = remember { PrettifyParser() }
            val themeState by remember { mutableStateOf(CodeThemeType.Monokai) }
            val theme = remember(themeState) { themeState.theme() }
            if (isEdit) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("code") },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )
            } else {
                val parsedCode = TextFieldValue(
                    parseCodeAsAnnotatedString(parser, theme, typeStr, data)
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
            TestDialog(ParserData(type, packageName, data, name, description)) {
                showTestDialog = false
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewEditParser() {
        YA自动记账Theme {
            EditParser(
                ParserData(
                    -1,
                    BuildConfig.APPLICATION_ID,
                    "abce" * 25,
                    "name",
                    "description"
                )
            )
        }
    }

    @Composable
    fun TestDialog(parserData: ParserData, onDismiss: () -> Unit) {
        var data by remember { mutableStateOf("") }
        var format by remember { mutableStateOf("json") }
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    parserData.toParser().parse(
                        Data(System.currentTimeMillis(), data, "", parserData.packageName),
                        onParsed = {},
                        onError = {})
                }) {
                    Text("run")
                }
            },
            title = {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.test),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    OutlinedTextField(
                        value = format, onValueChange = { format = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 4.dp)
                            .align(Alignment.CenterVertically),
                        label = { Text("format") },
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    IconButton(
                        onClick = {
                            val clipboardManager =
                                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            data =
                                clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                        }, modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            Icons.Default.ContentPaste, contentDescription = "paste",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            },
            text = {
                OutlinedTextField(
                    value = data, onValueChange = { data = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = { Text("data") },
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

            })
    }

    @Preview
    @Composable
    fun PreviewTestDialog() {
        YA自动记账Theme {
            TestDialog(ParserData.empty()) {}
        }
    }
}