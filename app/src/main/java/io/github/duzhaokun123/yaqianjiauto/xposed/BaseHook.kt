package io.github.duzhaokun123.yaqianjiauto.xposed

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAllConstructorAfter
import com.github.kyuubiran.ezxhelper.utils.hookAllConstructorBefore
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

interface BaseHook : IXposedHookLoadPackage {
    val packageName: String
    val appName: String
    val applicationClass: Class<*>
        get() = Application::class.java

    @CallSuper
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        applicationClass
            .hookAllConstructorBefore {
                Log.dx("$appName(${lpparam.processName}) application constructor")
                EzXHelperInit.initAppContext(it.thisObject as Context)
            }
    }
}