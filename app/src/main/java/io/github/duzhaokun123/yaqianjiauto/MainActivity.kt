package io.github.duzhaokun123.yaqianjiauto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.evgenii.jsevaluator.interfaces.JsCallback
import io.github.duzhaokun123.yaqianjiauto.ui.classifier.ClassifierListActivity
import io.github.duzhaokun123.yaqianjiauto.ui.data.DataListActivity
import io.github.duzhaokun123.yaqianjiauto.ui.parser.ParserListActivity
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.jsEvaluator
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.SelfHookChecker

class MainActivity : ComponentActivity() {
    val logDao by lazy { (application as Application).db.logDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YA自动记账Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()
                }
            }
        }
    }

    @Composable
    fun Main() {
        Column {
            Text("hooked: ${SelfHookChecker.check()}")
            Row {
                Button(onClick = {
                    DataSender.sendData("YA自动记账", "text")
                }) {
                    Text(text = "send data")
                }
                Button(onClick = {
                    DataSender.sendLog("YA自动记账", "test log")
                }) {
                    Text(text = "send log")
                }
                Button(onClick = {
                    jsEvaluator.callFunction("function myFunction(a, b, c, d) { return JSON.stringify({\"name\":\"oec2003\",\"age\":\"25\"}); }", object : JsCallback {
                        override fun onResult(value: String?) {
                            TipUtil.showToast("$value")
                        }

                        override fun onError(errorMessage: String?) {
                            TipUtil.showToast("$errorMessage")
                        }
                    }, "myFunction", "parameter 1", "parameter 2", 912, 101.3)
                }) {
                    Text("test")
                }
            }
            Row {
                Button(onClick = {
                    startActivity(Intent(this@MainActivity, DataListActivity::class.java))
                }) {
                    Text("data")
                }
                Button(onClick = {
                    runIO {
                        logDao.deleteAll()
                    }
                }) {
                    Text("clear")
                }
                Button(onClick = {
                    startActivity(Intent(this@MainActivity, ParserListActivity::class.java))
                }) {
                    Text("parsers")
                }
                Button(onClick = {
                    startActivity(Intent(this@MainActivity, ClassifierListActivity::class.java))
                }) {
                    Text("classifiers")
                }
            }
            var packageName by remember { mutableStateOf("") }
            Row {
                Text("package name")
                TextField(value = packageName, onValueChange = { packageName = it })
            }
            var tag by remember { mutableStateOf("") }
            Row {
                Text("tag")
                TextField(value = tag, onValueChange = { tag = it })
            }
            LogList(packageName, tag)
        }
    }

    @Composable
    fun LogList(packageName: String, tag: String) {
        val log by logDao.getAllFlow().collectAsState(initial = emptyList())
        LazyColumn() {
            items(log.reversed().filter {
                it.packageName.contains(packageName, true) && it.tag.contains(tag, true)
            }) {
                SelectionContainer {
                    Text("""
                        |${it.timestamp.toDataTime()}
                        |${it.packageName}
                        |${it.tag}
                        |${it.log}
                        |
                    """.trimMargin())
                }
            }
        }
    }
}