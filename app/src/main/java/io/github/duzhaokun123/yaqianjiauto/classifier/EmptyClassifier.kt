package io.github.duzhaokun123.yaqianjiauto.classifier

import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData

object EmptyClassifier: BaseClassifier {
    override fun classify(
        parsedData: ParsedData,
        onClassified: (ClassifiedParsedData?) -> Unit,
        onError: (String) -> Unit
    ) {
        onClassified(null)
    }
}