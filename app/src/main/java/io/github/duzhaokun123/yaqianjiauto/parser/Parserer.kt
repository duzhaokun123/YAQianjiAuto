package io.github.duzhaokun123.yaqianjiauto.parser

import android.util.Log
import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.toParser
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain

object Parserer {
    const val TAG = "Parserer"
    val parserDataDao by lazy { application.db.parserDataDao() }

    fun parse(data: Data) {
        runIO {
            val parserDatas = parserDataDao.getByPackageName(data.packageName)
            if (parserDatas.isEmpty()) {
                TipUtil.showToast("no parser for ${data.packageName}")
                return@runIO
            }
            runMain {
                var i = 0
                var parserData: BaseParser
                fun doParse() {
                    if (i >= parserDatas.size) {
                        TipUtil.showToast("no parser resulted for ${data.packageName}")
                        return
                    }
                    parserData = parserDatas[i].toParser()
                    parserData.parse(data,
                        onParsed = { parsedData ->
                            if (parsedData == null) {
                                i++
                                doParse()
                            } else {
                                Log.d(TAG, "doParse: $parsedData")
                                TipUtil.showToast("parsed ${data.packageName}")
                            }
                        }, onError = {
                            TipUtil.showToast(it)
                        })
                }
                doParse()
            }
        }
    }
}