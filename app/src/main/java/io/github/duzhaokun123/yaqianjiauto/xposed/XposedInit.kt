package io.github.duzhaokun123.yaqianjiauto.xposed

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.AlipayHook
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.self.SelfHook
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.wechat.WeChatHook

class XposedInit : IXposedHookLoadPackage {
    companion object {
        const val TAG = "YA自动记账"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelperInit.initHandleLoadPackage(lpparam)
        EzXHelperInit.setLogTag(TAG)
        EzXHelperInit.setToastTag(TAG)
        val hooks = listOf(SelfHook, WeChatHook, AlipayHook)
        hooks.forEach { hook ->
            if (InitFields.hostPackageName == hook.packageName) {
                Log.dx("Hooking ${hook.appName}(${lpparam.processName})...")
                runCatching {
                    hook.handleLoadPackage(lpparam)
                }.onSuccess {
                    Log.dx("${hook.appName} hooked.")
                }.logexIfThrow(hook.appName)
            }
        }
    }
}