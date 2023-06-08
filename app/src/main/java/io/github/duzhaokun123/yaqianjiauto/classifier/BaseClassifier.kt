package io.github.duzhaokun123.yaqianjiauto.classifier

import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData

interface BaseClassifier {
    fun classify(parsedData: ParsedData, onClassified: (ClassifiedParsedData?) -> Unit, onError: (String) -> Unit)
}