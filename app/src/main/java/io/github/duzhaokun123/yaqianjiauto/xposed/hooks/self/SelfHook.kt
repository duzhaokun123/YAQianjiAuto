package io.github.duzhaokun123.yaqianjiauto.xposed.hooks.self

import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaqianjiauto.BuildConfig
import io.github.duzhaokun123.yaqianjiauto.xposed.BaseHook

object SelfHook: BaseHook {
    override val packageName: String
        get() = BuildConfig.APPLICATION_ID
    override val appName: String
        get() = "YA自动记账"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        super.handleLoadPackage(lpparam)
        System.setProperty("xposed.hooked", "true")
    }
}