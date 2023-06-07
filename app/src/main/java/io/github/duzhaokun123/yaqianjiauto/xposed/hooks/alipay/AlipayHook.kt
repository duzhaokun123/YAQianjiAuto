package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay

import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.xposed.BaseHook
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks.HookPayUI
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks.HookReceive
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks.HookRed
import io.github.duzhaokun123.yaqianjiauto.xposed.hooks.alipay.hooks.HookSafe

object AlipayHook : BaseHook {
    override val packageName: String
        get() = "com.eg.android.AlipayGphone"
    override val appName: String
        get() = "支付宝"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        super.handleLoadPackage(lpparam)
        runCatching {
            if (BuildConfig.DEBUG)
                HookPayUI.init()
        }.logexIfThrow("HookPayUI")
        runCatching {
            HookReceive.init()
        }.logexIfThrow("HookReceive")
        runCatching {
            HookRed.init()
        }.logexIfThrow("HookRed")
//        runCatching {
//            HookSafe.init()
//        }.logexIfThrow("HookSafe")
    }
}