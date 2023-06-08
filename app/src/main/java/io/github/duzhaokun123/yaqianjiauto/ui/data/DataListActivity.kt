package io.github.duzhaokun123.yaqianjiauto.ui.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibleForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.accountmapper.AccountMapper
import io.github.duzhaokun123.yaqianjiauto.classifier.Classifierer
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.parser.Parserer
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.times
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime

class DataListActivity : ComponentActivity() {
    companion object {
        const val TAG = "DataListActivity"
    }
    private val dataDao by lazy { (application as Application).db.dataDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var packageName by remember { mutableStateOf("") }
                    var selectedData by remember { mutableStateOf<Data?>(null) }
                    Column {
                        TextField(
                            value = packageName,
                            onValueChange = { packageName = it },
                            label = { Text("package name") })
                        val datas by dataDao.getAllFlow().collectAsState(initial = emptyList())
                        LazyColumn {
                            items(
                                datas.reversed()
                                    .filter { it.packageName.contains(packageName, true) }) {
                                DataCard(it) {
                                    selectedData = it
                                }
                            }
                        }
                    }
                    if (selectedData != null) {
                        SelectDataDialog(selectedData!!) {
                            selectedData = null
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DataCard(data: Data, onCLick: () -> Unit = {}) {
        Card(
            modifier = Modifier.padding(8.dp),
            onClick = onCLick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(data.packageName, style = MaterialTheme.typography.titleMedium)
                Text("${data.timestamp.toDataTime()} ${data.format}", style = MaterialTheme.typography.labelSmall)
                Text(
                    data.data,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    @Preview(widthDp = 320)
    @Composable
    fun PreviewDataCard() {
        YA自动记账Theme {
            DataCard(
                Data(
                    System.currentTimeMillis(),
                    "abcd" * 250,
                    "text",
                    BuildConfig.APPLICATION_ID
                )
            )
        }
    }

    @Composable
    fun SelectDataDialog(data: Data, onDismiss: () -> Unit = {}) {
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = { },
            title = { Text(stringResource(R.string.action)) },
            text = {
                Column {
                    TextButton(onClick = {
                        runIO {
                            dataDao.delete(data)
                        }
                        onDismiss()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                        Text(
                            stringResource(R.string.delete), modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                    }
                    TextButton(onClick = {
                        val clipboardManager =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("code", data.data))
                        onDismiss()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Default.CopyAll,
                            contentDescription = stringResource(R.string.copy)
                        )
                        Text(
                            stringResource(R.string.copy), modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                    }
                    TextButton(onClick = {
                        Parserer.parse(data, onParsed = { parsedData, parserName ->
                            Classifierer.classify(parsedData, onClassified = { classifierParsedData, classifierName ->
                                AccountMapper.map(classifierParsedData, onMapped = { mappedClassifiedParsedData, accountMaps ->
                                    Log.d(TAG, "mappedClassifiedParsedData: $mappedClassifiedParsedData")
                                    Log.d(TAG, "accountMaps: $accountMaps")
                                })
                            }, onFailed = {
                                TipUtil.showToast(it)
                            })
                        }, onFailed = {
                            TipUtil.showToast(it)
                        })
                        onDismiss()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Default.AccessibleForward,
                            contentDescription = stringResource(R.string.test)
                        )
                        Text(
                            stringResource(R.string.test), modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                    }
                    TextButton(onClick = {
                        startActivity(Intent(this@DataListActivity, DataViewActivity::class.java).apply {
                            putExtra(DataViewActivity.EXTRA_INDEX, data.index)
                        })
                        onDismiss()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Default.Code,
                            contentDescription = stringResource(R.string.view)
                        )
                        Text(
                            stringResource(R.string.view), modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                    }
                }
            })
    }

    @Preview
    @Composable
    fun PreviewSelectDataDialog() {
        YA自动记账Theme {
            SelectDataDialog(
                Data(
                    System.currentTimeMillis(),
                    "abcd" * 250,
                    "text",
                    BuildConfig.APPLICATION_ID
                )
            )
        }
    }
}
