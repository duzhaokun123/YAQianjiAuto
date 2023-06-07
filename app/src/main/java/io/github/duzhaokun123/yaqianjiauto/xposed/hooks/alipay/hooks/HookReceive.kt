package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.getStringAsJsonObject
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.SubHook
import io.github.duzhaokun123.yaqianjiauto.xposed.paramIs

object HookReceive : SubHook {
    override fun init() {
        val class_MsgboxInfoServiceImpl =
            loadClass("com.alipay.android.phone.messageboxstatic.biz.sync.d")
        val class_SyncMessage =
            loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage")
        class_MsgboxInfoServiceImpl
            .findMethod { name == "onReceiveMessage" && paramIs(class_SyncMessage) }
            .hookBefore {
                var data = it.args[0].toString()
                data = data.substring(
                    data.indexOf("msgData=[") + "msgData=[".length,
                    data.indexOf("], pushData=,")
                )
                runCatching {
                    data = "[$data]"
                    val jsonArray = gson.fromJson<JsonArray>(data)
                    jsonArray.forEach { item ->
                        analyze(item.toString())
                    }
                }.onFailure { e ->
                    DataSender.sendLog(
                        "HookReceive",
                        "AlipayErr: ${e.message}\n${e.stackTrace.joinToString()}"
                    )
                    runCatching {
                        analyze(data)
                    }.onFailure { e ->
                        DataSender.sendLog(
                            "HookReceive",
                            "AlipayErr: ${e.message}\n${e.stackTrace.joinToString()}"
                        )
                    }
                }
            }
    }

    private fun analyze(data: String) {
        DataSender.sendLog("HookReceive", "AlipayData: $data")

        val json = gson.fromJson<JsonObject>(data)
        if (json.has("pl").not()) return
        val pl = json.getStringAsJsonObject("pl")

        if (pl.has("templateType").not()) return
        if (pl.has("templateName").not()) return
        if (pl.has("title").not()) return
        if (pl.has("content").not()) return

        var title = pl["title"].asString
        val templateName = pl["templateName"].asString
        if (title == "其他") title = templateName

        val r = JsonObject()
        r.addProperty("title", title)
        r.addProperty("templateName", templateName)
        r.addProperty("templateType", pl["templateType"].asString)

        if (pl["templateType"].asString == "BN") {
            r.add("content", pl.getStringAsJsonObject("content"))
            if (pl.has("extraInfo"))
                r.add("extraInfo", pl.getStringAsJsonObject("extraInfo"))
            DataSender.sendData(r.toString(), "json")
        } else if (pl.get("templateType").asString == "S") {
            r.addProperty("content", pl["content"].asString)
            r.add("extraInfo", pl.getStringAsJsonObject("extraInfo"))
            DataSender.sendData(r.toString(), "json")
        }
    }
}