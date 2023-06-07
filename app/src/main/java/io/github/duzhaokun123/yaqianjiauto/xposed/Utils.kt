package io.github.duzhaokun123.yaqianjiauto.xposed

import android.content.Intent
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.Log
import com.google.gson.Gson
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import java.lang.reflect.Method


object SelfHookChecker {
    fun check(): Boolean {
        return System.getProperty("xposed.hooked") == "true"
    }
}

fun Log.debugToast(msg: String, vararg formats: String, duration: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG)
        toast(msg, *formats, duration = duration)
}

fun Log.debugToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG)
        toast(msg, duration = duration)
}

object DataSender {
    const val ACTION_SEND_DATA = "io.github.duzhaokun123.yaqianjiauto.action.SEND_DATA"
    const val ACTION_SEND_LOG = "io.github.duzhaokun123.yaqianjiauto.action.SEND_LOG"
    const val EXTRA_DATA = "io.github.duzhaokun123.yaqianjiauto.extra.DATA"
    const val EXTRA_FORMAT = "io.github.duzhaokun123.yaqianjiauto.extra.FORMAT"
    const val EXTRA_PACKAGE_NAME = "io.github.duzhaokun123.yaqianjiauto.extra.PACKAGE_NAME"
    const val EXTRA_LOG = "io.github.duzhaokun123.yaqianjiauto.extra.LOG"
    const val EXTRA_LOG_TAG = "io.github.duzhaokun123.yaqianjiauto.extra.LOG_TAG"

    fun sendData(data: String,format: String, packageName: String = InitFields.appContext.packageName) {
        val intent = Intent(ACTION_SEND_DATA)
        intent.putExtra(EXTRA_DATA, data)
        intent.putExtra(EXTRA_FORMAT, format)
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
        intent.`package` = BuildConfig.APPLICATION_ID
        InitFields.appContext.sendBroadcast(intent)
    }

    fun sendLog(tag: String, log: String, packageName: String = InitFields.appContext.packageName) {
        val intent = Intent(ACTION_SEND_LOG)
        intent.putExtra(EXTRA_LOG_TAG, tag)
        intent.putExtra(EXTRA_LOG, log)
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
        intent.`package` = BuildConfig.APPLICATION_ID
        InitFields.appContext.sendBroadcast(intent)
    }
}

fun Method.paramIs(vararg classes: Class<*>): Boolean {
    if (classes.size != parameterTypes.size) return false
    for (i in classes.indices) {
        if (classes[i] != parameterTypes[i]) return false
    }
    return true
}