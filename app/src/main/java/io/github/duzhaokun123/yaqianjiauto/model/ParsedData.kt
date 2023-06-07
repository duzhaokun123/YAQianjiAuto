package io.github.duzhaokun123.yaqianjiauto.model

data class ParsedData(
    val type: Int,
    val account: String,
    val balance: Double,
    val name: String,
    val time: Long,
    val extras: Map<String, String>
)
