package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.duzhaokun123.yaqianjiauto.classifier.BaseClassifier
import io.github.duzhaokun123.yaqianjiauto.classifier.EmptyClassifier
import io.github.duzhaokun123.yaqianjiauto.classifier.JsClassifier
import io.github.duzhaokun123.yaqianjiauto.classifier.YamlClassifier

@Entity
data class ClassifierData(
    // -1: empty
    // 0: js
    // 1: yaml
    @ColumnInfo("type")
    val type: Int,
    @ColumnInfo("code")
    val code: String,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("description")
    val description: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("index")
    val index: Long = 0
) {
    companion object {
        fun empty(): ClassifierData {
            return ClassifierData(Type.Empty, "", "", "")
        }

        fun typeToStr(type: Int): String {
            return when (type) {
                Type.Empty -> "empty"
                Type.JS -> "js"
                Type.Yaml -> "yaml"
                else -> throw Exception("unknown classifier type: $type")
            }
        }
    }

    object Type {
        const val Empty = -1
        const val JS = 0
        const val Yaml = 1
    }
}

fun ClassifierData.toClassifier(): BaseClassifier {
    return when (type) {
        ClassifierData.Type.Empty -> EmptyClassifier
        ClassifierData.Type.JS -> JsClassifier(this)
        ClassifierData.Type.Yaml -> YamlClassifier(this)
        else -> throw Exception("unknown classifier type: $type")
    }
}
