package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.google.gson.JsonObject
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.getStringAsJsonObject
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.SubHook
import io.github.duzhaokun123.yaqianjiauto.xposed.paramIs

object HookRed: SubHook {
    override fun init() {
        val class_proguard = loadClass("com.alipay.mobile.redenvelope.proguard.c.b")
        val class_SyncMessage = loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage")
        class_proguard
            .findMethod { name == "onReceiveMessage" && paramIs(class_SyncMessage) }
            .hookBefore {
                var data = it.args[0].toString()
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length, data.indexOf("], pushData=,"))

                val json = gson.fromJson<JsonObject>(data)
                if (json.has("pl").not()) return@hookBefore
                val pl = json.getStringAsJsonObject("pl")
                if (pl.has("templateJson").not()) return@hookBefore
                val templateJson = pl.getStringAsJsonObject("templateJson")
                if (templateJson.has("statusLine1Text").not()) return@hookBefore
                if (templateJson.has("title").not()) return@hookBefore
                if (templateJson.has("subtitle").not()) return@hookBefore
                DataSender.sendLog("HookRed", "AlipayData: $data")
                templateJson.addProperty("auto", "支付宝收红包")
                DataSender.sendData(templateJson.toString(), "json")
            }
    }
}