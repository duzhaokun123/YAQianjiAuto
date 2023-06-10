package io.github.duzhaokun123.yaqianjiauto.ui.record

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.recorder.QianJiRecorder
import io.github.duzhaokun123.yaqianjiauto.ui.base.BaseOverlayWindow
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime
import kotlinx.coroutines.flow.receiveAsFlow

class RecordOverlayWindow(
    private val mappedClassifiedParsedData: MappedClassifiedParsedData,
    private val appName: String,
    private val parserName: String,
    private val classifierName: String,
    private val accountMaps: List<AccountMap>
) : BaseOverlayWindow() {
    override fun onLayoutParams(params: WindowManager.LayoutParams) {
        params.gravity = Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL
        params.width = WindowManager.LayoutParams.MATCH_PARENT
    }

    @Composable
    override fun Content() {
        YA自动记账Theme {
            Box(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier
                    .fillMaxSize()
                    .clickable { finish() })
                val showing by showing.receiveAsFlow().collectAsState(initial = false)
                AnimatedVisibility(
                    visible = showing,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically { it * 2 },
                    exit = slideOutVertically { it * 2 }
                ) {
                    RecordCard(
                        mappedClassifiedParsedData,
                        appName,
                        parserName,
                        classifierName,
                        accountMaps,
                        onDismiss = ::finish
                    )
                }
            }
        }
    }

    private fun finish() = finish(250)
}

@Composable
fun RecordCard(
    mappedClassifiedParsedData: MappedClassifiedParsedData,
    appName: String, parserName: String, classifierName: String, accountMaps: List<AccountMap>,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(appName)
            Text("-> $parserName")
            Text("-> $classifierName")
            Text("-> { ${accountMaps.joinToString("; ") { "${it.from} -> ${it.to}" }} }")
            Text("->")
            Row {
                Text("type", modifier = Modifier.weight(1f))
                Text(ParsedData.typeToStr(mappedClassifiedParsedData.classifiedParsedData.parsedData.type))
            }
            Row {
                Text("balance", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.balance.toString())
            }
            Row {
                Text("category", modifier = Modifier.weight(1f))
                Text(
                    mappedClassifiedParsedData.classifiedParsedData.subcategory
                        ?: mappedClassifiedParsedData.classifiedParsedData.category
                )
            }
            Row {
                Text("account", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.overrideAccount ?: "<none>")
            }
            Row {
                Text("time", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.timestamp.toDataTime())
            }
            Row {
                Text("target", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.target)
            }
            Row {
                Text("remark", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.remark)
            }
            Row {
                TextButton(onClick = { onDismiss() }, modifier = Modifier.weight(1f)) {
                    Text("取消")
                }
                Button(onClick = {
                    record(mappedClassifiedParsedData)
                    onDismiss()
                }, modifier = Modifier.weight(1f)) {
                    Row {
                        Text("记账")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRecordCard() {
    YA自动记账Theme {
        RecordCard(
            MappedClassifiedParsedData(
                ClassifiedParsedData(
                    ParsedData(
                        ParsedData.Type.Expense,
                        "account1",
                        10.0,
                        "target",
                        "remark",
                        System.currentTimeMillis(),
                        emptyMap()
                    ),
                    "category", "subcategory"
                ), "account2", "target"
            ),
            BuildConfig.APPLICATION_ID,
            "parser",
            "classifier",
            listOf(AccountMap("account1", "account2")), onDismiss = {}
        )
    }
}

fun record(mappedClassifiedParsedData: MappedClassifiedParsedData) {
    QianJiRecorder.record(mappedClassifiedParsedData)
}