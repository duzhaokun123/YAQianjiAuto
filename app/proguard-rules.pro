-keep class io.github.duzhaokun123.yaqianjiauto.model.** { *; }

-keep class io.github.duzhaokun123.yaqianjiauto.xposed.XposedInit

# js-evaluator-for-android
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}