package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.duzhaokun123.yaqianjiauto.model.Data
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Query("SELECT * FROM data")
    fun getAllFlow(): Flow<List<Data>>

    @Insert
    fun insert(data: Data)

    @Query("DELETE FROM data")
    fun deleteAll()

    @Delete
    fun delete(data: Data)

    @Query("SELECT * FROM data WHERE `index` = :index")
    fun getByIndexFlow(index: Long): Flow<Data?>
}