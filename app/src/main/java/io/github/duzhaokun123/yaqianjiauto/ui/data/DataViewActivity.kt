package io.github.duzhaokun123.yaqianjiauto.ui.data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.gsonPretty
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime

class DataViewActivity : ComponentActivity() {
    companion object {
        const val EXTRA_INDEX = "index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataIndex = intent.getLongExtra(EXTRA_INDEX, -1)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val data by
                    (application as Application).db.dataDao().getByIndexFlow(dataIndex).collectAsState(initial = null)
                    if (data == null)
                        Text("no code index $dataIndex")
                    else
                        DataView(data!!)
                }
            }
        }
    }

    @Composable
    fun DataView(data: Data) {
        var code = data.data
        when (data.format) {
            "json" -> code = gsonPretty.toJson(gson.fromJson(code))
        }
        val parser = remember { PrettifyParser() }
        val themeState by remember { mutableStateOf(CodeThemeType.Monokai) }
        val theme = remember(themeState) { themeState.theme() }
        val parsedCode = remember {
            parseCodeAsAnnotatedString(
                parser = parser,
                theme = theme,
                lang = data.format,
                code = code
            )
        }
        Column {
            Text(data.packageName, style = MaterialTheme.typography.titleMedium)
            Text("${data.timestamp.toDataTime()} ${data.format}", style = MaterialTheme.typography.labelSmall)
            SelectionContainer {
                Text(
                    parsedCode, modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }

    @Preview
    @Composable
    fun PreviewDataView() {
        YA自动记账Theme {
            DataView(
                Data(
                    System.currentTimeMillis(),
                    "{\"text\":\"text\",\"number\":123}",
                    "text",
                    BuildConfig.APPLICATION_ID
                )
            )
        }
    }
}