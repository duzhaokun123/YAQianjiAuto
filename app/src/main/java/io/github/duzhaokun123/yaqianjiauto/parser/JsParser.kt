package io.github.duzhaokun123.yaqianjiauto.parser

import com.evgenii.jsevaluator.interfaces.JsCallback
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParserData
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.jsEvaluator
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import kotlinx.coroutines.delay

class JsParser(private val parserData: ParserData) : BaseParser {
    override fun parse(data: Data, onParsed: (ParsedData?) -> Unit, onError: (String) -> Unit) {
        val timeoutKiller = runMain {
            delay(2000)
            jsEvaluator.jsCallFinished("evgeniiJsEvaluatorException timeout")
        }
        jsEvaluator.callFunction(parserData.code, object : JsCallback {
            override fun onResult(value: String) {
                timeoutKiller.cancel()
                if (value == "undefined") onParsed(null)
                else {
                    runCatching {
                        gson.fromJson<ParsedData>(value)
                    }.onFailure {
                        onError(it.message ?: "unknown error")
                    }.onSuccess {
                        onParsed(it)
                    }
                }
            }

            override fun onError(errorMessage: String) {
                timeoutKiller.cancel()
                onError(errorMessage)
            }
        }, "parse", data.data, data.format, data.timestamp)
    }
}