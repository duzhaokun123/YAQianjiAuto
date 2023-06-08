package io.github.duzhaokun123.yaqianjiauto.classifier

import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.model.toClassifier
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain

object Classifierer {
    private val classifierDataDao by lazy { application.db.classifierDataDao() }
    fun classify(parsedData: ParsedData, onClassified: (ClassifiedParsedData, String) -> Unit, onFailed: (String) -> Unit) {
        runIO {
            val classifierDatas = classifierDataDao.getAll()
            if (classifierDatas.isEmpty()) {
                onFailed("no classifier found")
                return@runIO
            }
            runMain {
                var i = 0
                var classifier: BaseClassifier
                fun doClassify() {
                    if (i >= classifierDatas.size) {
                        onFailed("no classifier resulted")
                        return
                    }
                    val classifierData = classifierDatas[i]
                    classifier = classifierData.toClassifier()
                    classifier.classify(parsedData,
                        onClassified = { classifiedParsedData ->
                            if (classifiedParsedData == null) {
                                i++
                                doClassify()
                            } else {
                                onClassified(classifiedParsedData, classifierData.name)
                            }
                        }, onError = {
                            onFailed(it)
                            i++
                            doClassify()
                        })
                }
                doClassify()
            }
        }
    }
}