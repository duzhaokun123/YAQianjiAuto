package io.github.duzhaokun123.yaqianjiauto.parser

import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.model.toParser
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain

object Parserer {
    private val parserDataDao by lazy { application.db.parserDataDao() }

    fun parse(data: Data, onParsed: (ParsedData, String) -> Unit, onFailed: (String) -> Unit) {
        runIO {
            val parserDatas = parserDataDao.getByPackageName(data.packageName)
            if (parserDatas.isEmpty()) {
                onFailed("no parser found")
                return@runIO
            }
            runMain {
                var i = 0
                var parser: BaseParser
                fun doParse() {
                    if (i >= parserDatas.size) {
                        onFailed("no parser resulted")
                        return
                    }
                    val parserData = parserDatas[i]
                    parser = parserData.toParser()
                    parser.parse(data,
                        onParsed = { parsedData ->
                            if (parsedData == null) {
                                i++
                                doParse()
                            } else {
                                onParsed(parsedData, parserData.name)
                            }
                        }, onError = {
                            onFailed(it)
                            i++
                            doParse()
                        })
                }
                doParse()
            }
        }
    }
}