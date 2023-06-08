package io.github.duzhaokun123.yaqianjiauto.ui.record

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime

class RecordActivity : ComponentActivity() {
    companion object {
        const val EXTRA_DATA = "data"
        const val EXTRA_APP_NAME = "app_name"
        const val EXTRA_PARSER_NAME = "parser_name"
        const val EXTRA_CLASSIFIER_NAME = "classifier_name"
        const val EXTRA_ACCOUNT_MAPS = "account_maps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mappedClassifiedParsedData =
            gson.fromJson<MappedClassifiedParsedData>(intent.getStringExtra(EXTRA_DATA)!!)
        val appName = intent.getStringExtra(EXTRA_APP_NAME)!!
        val parserName = intent.getStringExtra(EXTRA_PARSER_NAME)!!
        val classifierName = intent.getStringExtra(EXTRA_CLASSIFIER_NAME)!!
        val accountMaps = intent.getStringArrayExtra(EXTRA_ACCOUNT_MAPS)!!.map {
            gson.fromJson<AccountMap>(it)
        }
        setContent {
            YA自动记账Theme {
                Box(modifier = Modifier.fillMaxSize()) {
                    RecordCard(
                        mappedClassifiedParsedData, appName, parserName, classifierName, accountMaps,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }

    @Composable
    fun RecordCard(
        mappedClassifiedParsedData: MappedClassifiedParsedData,
        appName: String, parserName: String, classifierName: String, accountMaps: List<AccountMap>,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(appName)
                Text("-> $parserName")
                Text("-> $classifierName")
                Text("-> { ${accountMaps.joinToString("; ") { "${it.from} -> ${it.to}" }} }")
                Text("->")
                Row {
                    Text("balance", modifier = Modifier.weight(1f))
                    Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.balance.toString())
                }
                Row {
                    Text("type", modifier = Modifier.weight(1f))
                    Text(ParsedData.typeToStr(mappedClassifiedParsedData.classifiedParsedData.parsedData.type))
                }
                Row {
                    Text("class", modifier = Modifier.weight(1f))
                    Text(mappedClassifiedParsedData.classifiedParsedData.subclass ?: mappedClassifiedParsedData.classifiedParsedData.`class`)
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
                    Text("remark", modifier = Modifier.weight(1f))
                    Text("${mappedClassifiedParsedData.classifiedParsedData.parsedData.target} - ${mappedClassifiedParsedData.classifiedParsedData.parsedData.remark}")
                }
                Row {
                    TextButton(onClick = { finish() }, modifier = Modifier.weight(1f)) {
                        Text("取消")
                    }
                    Button(onClick = { finish() }, modifier = Modifier.weight(1f)) {
                        Text("记账")
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
                            "accoun1",
                            10.0,
                            "target",
                            "remark",
                            System.currentTimeMillis(),
                            emptyMap()
                        ),
                        "class", "subclass"
                    ), "account2", "target"
                ), BuildConfig.APPLICATION_ID, "parser", "classifier", listOf(AccountMap("account1", "account2"))
            )
        }
    }
}