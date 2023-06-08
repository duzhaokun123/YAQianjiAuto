package io.github.duzhaokun123.yaqianjiauto.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountMapDao {
    @Query("SELECT * FROM accountmap")
    fun getAllFlow(): Flow<List<AccountMap>>

    @Delete
    fun delete(accountMap: AccountMap)

    @Upsert
    fun upsert(accountMap: AccountMap)

    @Query("SELECT * FROM accountmap WHERE `from` = :from")
    fun getByFrom(from: String): AccountMap?
}