package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import io.github.duzhaokun123.yaqianjiauto.model.ParserData
import kotlinx.coroutines.flow.Flow

@Dao
interface ParserDataDao {
    @Query("SELECT * FROM parserdata")
    fun getAllFlow(): Flow<List<ParserData>>

    @Insert
    fun insert(parserData: ParserData)

    @Delete
    fun delete(parserData: ParserData)

    @Upsert
    fun upsert(parserData: ParserData)

    @Upsert
    fun upsertAll(parserDataList: List<ParserData>)

    @Query("SELECT * FROM parserdata WHERE packageName = :packageName")
    fun getByPackageName(packageName: String): List<ParserData>

    @Query("SELECT * FROM parserdata WHERE `index` = :index")
    fun getByIndexFlow(index: Long): Flow<ParserData?>
}