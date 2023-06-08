package io.github.duzhaokun123.yaqianjiauto.model

import com.google.gson.annotations.SerializedName
import io.github.duzhaokun123.yaqianjiauto.utils.gsonPretty

data class MappedClassifiedParsedData(
    @SerializedName("classified_parsed_data")
    val classifiedParsedData: ClassifiedParsedData,
    @SerializedName("override_account")
    val overrideAccount: String?,
    @SerializedName("override_target")
    val overrideTarget: String?
) {
    override fun toString(): String {
        return gsonPretty.toJson(this)
    }
}
