package io.github.duzhaokun123.yaqianjiauto.ui.record

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.ui.base.BaseOverlayWindow
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow

class MiniRecordOverlayWindow(
    private val mappedClassifiedParsedData: MappedClassifiedParsedData,
    private val appName: String,
    private val parserName: String,
    private val classifierName: String,
    private val accountMaps: List<AccountMap>
) : BaseOverlayWindow() {
    private val timeoutTimer = Channel<Int>()
    private lateinit var timeoutJob: Job

    fun show(timeout: Int) {
        show()
        timeoutJob = runMain {
            (timeout downTo 0).forEach {
                timeoutTimer.send(it)
                delay(1000)
            }
            record(mappedClassifiedParsedData)
            finish()
        }
    }

    override fun onLayoutParams(params: WindowManager.LayoutParams) {
        params.gravity = Gravity.END + Gravity.CENTER_VERTICAL
    }

    @Composable
    override fun Content() {
        val timeout by timeoutTimer.receiveAsFlow().collectAsState(initial = 0)
        val showing by showing.receiveAsFlow().collectAsState(initial = false)
        YA自动记账Theme {
            AnimatedVisibility(visible = showing,
                enter = slideInHorizontally { it * 2 },
                exit = slideOutHorizontally { it * 2 }) {
                MiniRecordCard(
                    mappedClassifiedParsedData,
                    onClick = {
                        finish()
                        RecordOverlayWindow(
                            mappedClassifiedParsedData,
                            appName,
                            parserName,
                            classifierName,
                            accountMaps
                        ).show()
                    },
                    timeout = timeout
                )
            }
        }
    }

    private fun finish() {
        finish(250)
        timeoutJob.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniRecordCard(
    mappedClassifiedParsedData: MappedClassifiedParsedData,
    onClick: () -> Unit,
    timeout: Int
) {
    Card(
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column {
                Text(
                    mappedClassifiedParsedData.overrideAccount ?: "<none>",
                    style = MaterialTheme.typography.labelMedium
                )
                val type = mappedClassifiedParsedData.classifiedParsedData.parsedData.type
                val balance = when (type) {
                    ParsedData.Type.Expense -> "-"
                    ParsedData.Type.Income -> "+"
                    ParsedData.Type.Transfer -> ""
                    else -> throw Exception("Unknown $type")
                } + "%.2f".format(mappedClassifiedParsedData.classifiedParsedData.parsedData.balance)
                Text(
                    balance, style = MaterialTheme.typography.titleLarge, color = when (type) {
                        ParsedData.Type.Expense -> MaterialTheme.colorScheme.error
                        ParsedData.Type.Income -> MaterialTheme.colorScheme.primary
                        ParsedData.Type.Transfer -> MaterialTheme.colorScheme.secondary
                        else -> throw Exception("Unknown $type")
                    }
                )
                Text(
                    mappedClassifiedParsedData.overrideTarget ?: "<none>",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    mappedClassifiedParsedData.classifiedParsedData.subcategory
                        ?: mappedClassifiedParsedData.classifiedParsedData.category,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                "($timeout)",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Preview
@Composable
fun PreviewMiniRecordCard() {
    YA自动记账Theme {
        MiniRecordCard(
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
            onClick = {},
            timeout = 5
        )
    }
}