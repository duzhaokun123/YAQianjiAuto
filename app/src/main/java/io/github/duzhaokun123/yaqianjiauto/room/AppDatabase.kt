package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.Log
import io.github.duzhaokun123.yaqianjiauto.model.ParserData

@Database(entities = [Log::class, Data::class, ParserData::class, ClassifierData::class, AccountMap::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun dataDao(): DataDao
    abstract fun parserDataDao(): ParserDataDao
    abstract fun classifierDataDao(): ClassifierDataDao
    abstract fun accountMapDao(): AccountMapDao
}