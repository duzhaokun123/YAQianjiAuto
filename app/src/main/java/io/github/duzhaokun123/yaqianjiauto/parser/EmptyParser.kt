package io.github.duzhaokun123.yaqianjiauto.parser

import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData

object EmptyParser: BaseParser {
    override fun parse(data: Data, onParsed: (ParsedData?) -> Unit, onError: (String) -> Unit) {
        onParsed(null)
    }
}