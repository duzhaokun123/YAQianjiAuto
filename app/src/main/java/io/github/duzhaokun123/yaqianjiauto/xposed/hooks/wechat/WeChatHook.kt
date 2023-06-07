package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.wechat

import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaqianjiauto.xposed.BaseHook
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.wechat.hook.HookMsg

object WeChatHook: BaseHook {
    override val packageName: String
        get() = "com.tencent.mm"
    override val appName: String
        get() = "微信"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        super.handleLoadPackage(lpparam)
        runCatching {
            HookMsg.init()
        }.logexIfThrow("HookMsg")
    }
}