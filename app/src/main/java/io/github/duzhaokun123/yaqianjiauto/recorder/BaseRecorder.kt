package io.github.duzhaokun123.yaqianjiauto.recorder

import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData

interface BaseRecorder {
    fun record(mappedClassifiedParsedData: MappedClassifiedParsedData)

    val name: String
}