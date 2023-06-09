package io.github.duzhaokun123.yaqianjiauto.model

import com.google.gson.annotations.SerializedName
import io.github.duzhaokun123.yaqianjiauto.utils.gsonPretty

data class ParsedData(
    // 1: 支出
    // 2: 收入
    // 3: 转账
    @SerializedName("type")
    val type: Int,
    @SerializedName("account")
    val account: String,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("target")
    val target: String,
    @SerializedName("remark")
    val remark: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("extras")
    val extras: Map<String, String>
) {
    companion object {
        fun typeToStr(type: Int): String {
            return when (type) {
                Type.Expense -> "expense"
                Type.Income -> "income"
                Type.Transfer -> "transfer"
                else -> throw Exception("unknown parsed data type: $type")
            }
        }
    }

    object Type {
        const val Expense = 1
        const val Income = 2
        const val Transfer = 3
    }

     override fun toString(): String {
        return gsonPretty.toJson(this)
    }
}
