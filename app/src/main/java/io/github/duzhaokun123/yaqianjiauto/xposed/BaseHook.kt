package io.github.duzhaokun123.yaqianjiauto.xposed

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

interface BaseHook : IXposedHookLoadPackage {
    val packageName: String
    val appName: String

    @CallSuper
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        Application::class.java
            .findMethod { name == "onCreate" }
            .hookAfter {
                EzXHelperInit.initAppContext(it.thisObject as Context)
            }
    }
}