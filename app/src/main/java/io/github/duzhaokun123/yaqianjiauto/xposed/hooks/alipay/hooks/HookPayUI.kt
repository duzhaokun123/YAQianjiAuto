package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks


import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import com.github.kyuubiran.ezxhelper.utils.loadClass
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.SubHook
import io.github.duzhaokun123.yaqianjiauto.xposed.paramIs

object HookPayUI : SubHook {
    override fun init() {
        hookUI()
        hookData()
    }

    private fun hookData() {
        val class_CreateToAccountReq =
            loadClass("com.alipay.mobileprod.biz.transfer.dto.CreateToAccountReq")
        class_CreateToAccountReq
            .findMethod { name == "toString" }
            .hookAfter {
                val str = it.result as String
                DataSender.sendLog("CreateToAccountReq", str)
            }
    }

    private fun hookUI() {
        val class_UIAction = loadClass("com.alipay.android.msp.drivers.actions.UIAction")
        val class_MspUIClient = loadClass("com.alipay.android.msp.core.clients.MspUIClient")
        class_MspUIClient
            .findMethod { name == "handleUiShow" && paramIs(class_UIAction) }
            .hookAfter {
                val str = it.result
                DataSender.sendLog("UIAction", str.invokeMethod("toJSONString") as String)
            }
    }
}