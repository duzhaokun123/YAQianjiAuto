package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.duzhaokun123.yaqianjiauto.parser.BaseParser
import io.github.duzhaokun123.yaqianjiauto.parser.EmptyParser
import io.github.duzhaokun123.yaqianjiauto.parser.JsParser
import io.github.duzhaokun123.yaqianjiauto.utils.gsonPretty

@Entity
data class ParserData(
    // -1: empty
    // 0: js
    // 1: python
    @ColumnInfo
    val type: Int,
    @ColumnInfo
    val packageName: String,
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
        fun empty(): ParserData {
            return ParserData(-1, "", "", "", "")
        }

        fun typeToStr(type: Int): String {
            return when (type) {
                Type.Empty -> "empty"
                Type.JS -> "js"
                Type.Python -> "python"
                else -> throw Exception("unknown parser type: $type")
            }
        }
    }

    object Type {
        const val Empty = -1
        const val JS = 0
        const val Python = 1
    }
}

fun ParserData.toParser(): BaseParser {
    return when (type) {
        ParserData.Type.Empty -> EmptyParser
        ParserData.Type.JS -> JsParser(this)
        else -> throw Exception("unknown parser type: $type")
    }
}