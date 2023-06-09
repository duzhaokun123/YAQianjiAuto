package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @ColumnInfo("timestamp")
    val timestamp: Long,
    @ColumnInfo("data")
    val data: String,
    @ColumnInfo("format")
    val format: String,
    @ColumnInfo("packageName")
    val packageName: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("index")
    val index: Long = 0
)
