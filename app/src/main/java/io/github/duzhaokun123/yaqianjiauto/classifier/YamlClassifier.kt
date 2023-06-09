package io.github.duzhaokun123.yaqianjiauto.classifier

import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.utils.yaml
import java.util.regex.Pattern

class YamlClassifier(private val classifierData: ClassifierData) : BaseClassifier {
    override fun classify(
        parsedData: ParsedData,
        onClassified: (ClassifiedParsedData?) -> Unit,
        onError: (String) -> Unit
    ) {
        runCatching {
            val map = yaml.loadFromString(classifierData.code) as Map<String, Any>
            if (parsedData.type in map["type"] as List<Int>
                && Pattern.matches(map["target"] as String, parsedData.target)
                && Pattern.matches(map["remark"] as String, parsedData.remark)
            )
                ClassifiedParsedData(parsedData, map["category"] as String, map["subcategory"] as? String)
            else
                null
        }.onFailure { t ->
            onError(t.message ?: "unknown error")
        }.onSuccess {
            onClassified(it)
        }
    }
}