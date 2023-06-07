package io.github.duzhaokun123.yaqianjiauto.parser

import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParserData

class JsParser(private val parserData: ParserData) : BaseParser {
    override fun parse(data: Data, onParsed: (ParsedData?) -> Unit, onError: (String) -> Unit) {
        TODO("Not yet implemented")
    }
}