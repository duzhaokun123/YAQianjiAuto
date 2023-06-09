package io.github.duzhaokun123.yaqianjiauto.ui.record

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.recorder.QianJiRecorder
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow

class RecordOverlayWindow(
    private val mappedClassifiedParsedData: MappedClassifiedParsedData,
    private val appName: String,
    private val parserName: String,
    private val classifierName: String,
    private val accountMaps: List<AccountMap>
) {
    val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    lateinit var composeView: ComposeView
    lateinit var lifecycleOwner: LifecycleOwner
    val timeoutTimer = Channel<Int>()
    var timeoutTimerJob: Job? = null
    val showing = Channel<Boolean>()
    fun show(timeout: Int = 0) {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        composeView = ComposeView(application)

        val viewModelStore = ViewModelStore()
        lifecycleOwner = LifecycleOwner()
        lifecycleOwner.performRestore(null)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        })
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setContent {
            val timeout by timeoutTimer.receiveAsFlow().collectAsState(initial = null)
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
                            onDismiss = ::finish,
                            timeout = timeout
                        )
                    }
                }
            }
        }
        if (timeout >= 0) {
            timeoutTimerJob = runMain {
                for (i in timeout downTo 0) {
                    timeoutTimer.send(i)
                    delay(500)
                }
                record(mappedClassifiedParsedData)
                finish()
            }
        }
        windowManager.addView(composeView, params)
        runMain {
            showing.send(true)
        }
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    private fun finish() {
        runMain {
            showing.send(false)
            delay(1000)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            windowManager.removeView(composeView)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        timeoutTimerJob?.cancel()
        timeoutTimer.cancel()
    }

    class LifecycleOwner : SavedStateRegistryOwner {
        private var lifecycleRegistry = LifecycleRegistry(this)
        private var savedStateRegistryController = SavedStateRegistryController.create(this)

        val isInitialized: Boolean
            get() = true

        override val lifecycle: Lifecycle
            get() = lifecycleRegistry

        fun setCurrentState(state: Lifecycle.State) {
            lifecycleRegistry.currentState = state
        }

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }

        override val savedStateRegistry: SavedStateRegistry
            get() = savedStateRegistryController.savedStateRegistry

        fun performRestore(savedState: Bundle?) {
            savedStateRegistryController.performRestore(savedState)
        }

        fun performSave(outBundle: Bundle) {
            savedStateRegistryController.performSave(outBundle)
        }
    }
}

@Composable
fun RecordCard(
    mappedClassifiedParsedData: MappedClassifiedParsedData,
    appName: String, parserName: String, classifierName: String, accountMaps: List<AccountMap>,
    onDismiss: () -> Unit, timeout: Int?
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
                Text("balance", modifier = Modifier.weight(1f))
                Text(mappedClassifiedParsedData.classifiedParsedData.parsedData.balance.toString())
            }
            Row {
                Text("type", modifier = Modifier.weight(1f))
                Text(ParsedData.typeToStr(mappedClassifiedParsedData.classifiedParsedData.parsedData.type))
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
                Text("remark", modifier = Modifier.weight(1f))
                Text("${mappedClassifiedParsedData.classifiedParsedData.parsedData.target} - ${mappedClassifiedParsedData.classifiedParsedData.parsedData.remark}")
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
                        if (timeout != null) {
                            Text("($timeout)")
                        }
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
                        "accoun1",
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
            listOf(AccountMap("account1", "account2")), onDismiss =  {}, timeout =  5
        )
    }
}

fun record(mappedClassifiedParsedData: MappedClassifiedParsedData) {
    QianJiRecorder.record(mappedClassifiedParsedData)
}