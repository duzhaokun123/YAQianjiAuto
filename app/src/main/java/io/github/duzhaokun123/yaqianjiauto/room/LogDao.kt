package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.duzhaokun123.yaqianjiauto.model.Log
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM log")
    fun getAllFlow(): Flow<List<Log>>

    @Insert
    fun insert(log: Log)

    @Query("DELETE FROM log")
    fun deleteAll()
}