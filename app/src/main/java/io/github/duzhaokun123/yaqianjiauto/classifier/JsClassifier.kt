package io.github.duzhaokun123.yaqianjiauto.classifier

import com.evgenii.jsevaluator.interfaces.JsCallback
import com.google.gson.JsonObject
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.utils.fromJson
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.jsEvaluator
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import kotlinx.coroutines.delay

class JsClassifier(private val classifierData: ClassifierData): BaseClassifier {
    override fun classify(
        parsedData: ParsedData,
        onClassified: (ClassifiedParsedData?) -> Unit,
        onError: (String) -> Unit
    ) {
        val timeoutKiller = runMain {
            delay(2000)
            jsEvaluator.jsCallFinished("evgeniiJsEvaluatorException timeout")
        }
        jsEvaluator.callFunction(classifierData.code, object : JsCallback {
            override fun onResult(value: String) {
                if (value == "undefined") onClassified(null)
                else {
                    runCatching {
                        val classifyInfo = gson.fromJson<JsonObject>(value)
                        ClassifiedParsedData(
                            parsedData,
                            classifyInfo["category"].asString,
                            classifyInfo["subcategory"]?.asString
                        )
                    }.onFailure {
                        onError(it.message ?: "unknown error")
                    }.onSuccess {
                        onClassified(it)
                    }
                }
            }

            override fun onError(errorMessage: String) {
                timeoutKiller.cancel()
                onError(errorMessage)
            }
        }, "classify", parsedData.toString())
    }
}