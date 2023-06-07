package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.wechat.hook

import android.content.ContentValues
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.SubHook
import io.github.duzhaokun123.yaqianjiauto.xposed.paramIs

object HookMsg : SubHook {
    override fun init() {
        val class_SQLiteDatabase = loadClass("com.tencent.wcdb.database.SQLiteDatabase")
        class_SQLiteDatabase
            .findMethod {
                name == "insert" && paramIs(
                    String::class.java,
                    String::class.java,
                    ContentValues::class.java
                )
            }
            .hookAfter {
                runCatching {
                    val contentValues = it.args[2] as ContentValues
                    val tableName = it.args[0] as String
                    val arg = it.args[1] as String
                    if (BuildConfig.DEBUG)
                        DataSender.sendLog("HookMsg", "insert: $tableName, $arg, $contentValues")
                    if (tableName != "message") return@hookAfter
                    val type = contentValues.getAsInteger("type") ?: return@hookAfter
                    val json = JsonObject()
                    json.addProperty("isSend", contentValues.getAsInteger("isSend"))
                    json.addProperty("status", contentValues.getAsLong("status"))
                    json.addProperty("talker", contentValues.getAsLong("talker"))

                    val xml = gson.fromJson<JsonElement>(XmlToJson.Builder(contentValues.getAsString("content")).build().toString())
                    json.add("content", xml)

                    when (type) {
                        419430449 -> {
                            json.addProperty("title", "转账消息")
                            DataSender.sendData(json.toString(), "json")
                        }

                        436207665 -> {
                            json.addProperty("title", "红包消息")
                            DataSender.sendData(json.toString(), "json")
                        }

                        318767153 -> {
                            json.addProperty("title", "卡片消息")
                            DataSender.sendData(json.toString(), "json")
                        }

                        else -> {
                            if (BuildConfig.DEBUG)
                                DataSender.sendLog("HookMsg", "unknown type: $type, $contentValues")
                        }
                    }
                }.onFailure {
                    DataSender.sendLog("HookMsg", "${it.message}")
                }
            }
    }
}