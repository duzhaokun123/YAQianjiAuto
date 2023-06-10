package io.github.duzhaokun123.yaqianjiauto.utils

import android.icu.text.SimpleDateFormat
import com.evgenii.jsevaluator.JsEvaluator
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.duzhaokun123.yaqianjiauto.accountmapper.AccountMapper
import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.classifier.Classifierer
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.parser.Parserer
import io.github.duzhaokun123.yaqianjiauto.ui.record.MiniRecordOverlayWindow
import io.github.duzhaokun123.yaqianjiauto.ui.record.RecordOverlayWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.util.Locale

fun runIO(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.IO, block = block)

fun runMain(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Main, block = block)

val simpleDateTimeFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

fun Long.toDataTime(): String {
    val date = java.util.Date(this)
    return simpleDateTimeFormat.format(date)
}

val gson by lazy { Gson() }

val gsonPretty by lazy { Gson().newBuilder().setPrettyPrinting().create() }

inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)

fun JsonObject.getStringAsJsonObject(memberName: String): JsonObject =
    gson.fromJson(get(memberName).asString)

operator fun String.times(i: Int): String {
    val sb = StringBuilder()
    for (j in 0 until i) {
        sb.append(this)
    }
    return sb.toString()
}

val jsEvaluator by lazy { JsEvaluator(application) }

val yaml by lazy { Load(LoadSettings.builder().build()) }

fun Data.record(timeout: Int = -1, onFailed: (String) -> Unit = {}, mini: Boolean = false) {
    Parserer.parse(this, onParsed = { parsedData, parserName ->
        Classifierer.classify(parsedData, onClassified = { classifierParsedData, classifierName ->
            AccountMapper.map(
                classifierParsedData,
                onMapped = { mappedClassifiedParsedData, accountMaps ->
                    runMain {
                        if (mini) {
                            MiniRecordOverlayWindow(
                                mappedClassifiedParsedData, packageName, parserName, classifierName, accountMaps
                            ).show(timeout)
                        } else {
                            RecordOverlayWindow(
                                mappedClassifiedParsedData, packageName, parserName, classifierName, accountMaps
                            ).show()
                        }
                    }
                })
        }, onFailed = onFailed)
    }, onFailed = onFailed)
}