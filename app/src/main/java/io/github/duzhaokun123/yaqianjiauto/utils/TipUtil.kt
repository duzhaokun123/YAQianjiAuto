package io.github.duzhaokun123.yaqianjiauto.utils

import android.widget.Toast
import io.github.duzhaokun123.yaqianjiauto.application

object TipUtil {
    fun showToast(text: String) {
        runMain {
            Toast.makeText(application, text, Toast.LENGTH_SHORT).show()
        }
    }
}