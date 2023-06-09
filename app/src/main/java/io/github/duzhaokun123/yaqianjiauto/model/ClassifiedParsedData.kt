package io.github.duzhaokun123.yaqianjiauto.model

import com.google.gson.annotations.SerializedName
import io.github.duzhaokun123.yaqianjiauto.utils.gsonPretty

data class ClassifiedParsedData(
    @SerializedName("parsed_data")
    val parsedData: ParsedData,
    @SerializedName("category")
    val category: String,
    @SerializedName("subcategory")
    val subcategory: String?
) {
    override fun toString(): String {
        return gsonPretty.toJson(this)
    }
}
