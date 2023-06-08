package io.github.duzhaokun123.yaqianjiauto.accountmapper

import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.utils.runIO

object AccountMapper {
    private val accountMapDao by lazy { application.db.accountMapDao() }

    fun map(
        classifiedParsedData: ClassifiedParsedData,
        onMapped: (MappedClassifiedParsedData, Set<AccountMap>) -> Unit
    ) {
        runIO {
            var overrideAccount: String? = null
            val accountMap = accountMapDao.getByFrom(classifiedParsedData.parsedData.account)
            overrideAccount = if (accountMap == null)
                classifiedParsedData.parsedData.account
            else
                accountMap.to

            var overrideTarget: String? = null
            var targetMap: AccountMap? = null
            if (classifiedParsedData.parsedData.type != ParsedData.Type.Transfer)
                overrideTarget = classifiedParsedData.parsedData.target
            else {
                targetMap = accountMapDao.getByFrom(classifiedParsedData.parsedData.target)
                overrideTarget = if (targetMap == null)
                    classifiedParsedData.parsedData.target
                else
                    targetMap.to
            }
            onMapped(
                MappedClassifiedParsedData(
                    classifiedParsedData, overrideAccount, overrideTarget
                ), setOfNotNull(accountMap, targetMap)
            )
        }
    }
}