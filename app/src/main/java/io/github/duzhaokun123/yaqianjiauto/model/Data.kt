package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @ColumnInfo
    val timestamp: Long,
    @ColumnInfo
    val data: String,
    @ColumnInfo
    val format: String,
    @ColumnInfo
    val packageName: String,
    @PrimaryKey(autoGenerate = true)
    val index: Long = 0
)
