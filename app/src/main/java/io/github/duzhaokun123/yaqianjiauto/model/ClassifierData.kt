package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.duzhaokun123.yaqianjiauto.classifier.BaseClassifier
import io.github.duzhaokun123.yaqianjiauto.classifier.EmptyClassifier
import io.github.duzhaokun123.yaqianjiauto.classifier.JsClassifier

@Entity
data class ClassifierData(
    // -1: empty
    // 0: js
    // 1: python
    @ColumnInfo
    val type: Int,
    @ColumnInfo
    val code: String,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val description: String,
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0
) {
    companion object {
        fun empty(): ClassifierData {
            return ClassifierData(-1, "", "", "")
        }

        fun typeToStr(type: Int): String {
            return when (type) {
                ParserData.Type.Empty -> "empty"
                ParserData.Type.JS -> "js"
                ParserData.Type.Python -> "python"
                else -> throw Exception("unknown classifier type: $type")
            }
        }
    }

    object Type {
        const val Empty = -1
        const val JS = 0
        const val Python = 1
    }
}

fun ClassifierData.toClassifier(): BaseClassifier {
    return when (type) {
        ParserData.Type.Empty -> EmptyClassifier
        ParserData.Type.JS -> JsClassifier(this)
        else -> throw Exception("unknown classifier type: $type")
    }
}
