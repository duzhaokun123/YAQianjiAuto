package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.paramCount
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.SubHook

object HookSafe: SubHook {
    override fun init() {
        loadClass("com.alipay.apmobilesecuritysdk.scanattack.common.ScanAttack")
            .findMethod(true) { paramCount == 7 }
            .hookAfter {
                DataSender.sendLog("HookSafe", "ScanAttack: ${it.result}")
                it.result = null
            }
    }
}