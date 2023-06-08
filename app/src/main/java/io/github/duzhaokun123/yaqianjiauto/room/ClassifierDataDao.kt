package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassifierDataDao {
    @Query("SELECT * FROM classifierdata")
    fun getAllFlow(): Flow<List<ClassifierData>>

    @Query("SELECT * FROM classifierdata")
    fun getAll(): List<ClassifierData>

    @Insert
    fun insert(classifierData: ClassifierData)

    @Delete
    fun delete(classifierData: ClassifierData)

    @Upsert
    fun upsert(classifierData: ClassifierData)

    @Upsert
    fun upsertAll(classifierDataList: List<ClassifierData>)

    @Query("SELECT * FROM classifierdata WHERE `index` = :index")
    fun getByIndexFlow(index: Long): Flow<ClassifierData>
}