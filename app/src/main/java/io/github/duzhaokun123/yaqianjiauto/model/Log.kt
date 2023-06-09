package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Log (
    @ColumnInfo("timestamp")
    val timestamp: Long,
    @ColumnInfo("tag")
    val tag: String,
    @ColumnInfo("log")
    val log: String,
    @ColumnInfo("packageName")
    val packageName: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("index")
    val index: Long = 0
)