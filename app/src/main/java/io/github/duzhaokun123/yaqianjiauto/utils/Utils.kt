package io.github.duzhaokun123.yaqianjiauto.utils

import android.icu.text.SimpleDateFormat
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

fun JsonObject.getStringAsJsonObject(memberName: String): JsonObject = gson.fromJson(get(memberName).asString)

operator fun String.times(i: Int): String {
    val sb = StringBuilder()
    for (j in 0 until i) {
        sb.append(this)
    }
    return sb.toString()
}