package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountMap(
    @PrimaryKey
    val from: String,
    @ColumnInfo
    val to: String?
)
