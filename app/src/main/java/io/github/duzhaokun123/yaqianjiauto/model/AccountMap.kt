package io.github.duzhaokun123.yaqianjiauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AccountMap(
    @PrimaryKey
    @ColumnInfo("from")
    @SerializedName("from")
    val from: String,
    @ColumnInfo("to")
    @SerializedName("to")
    val to: String?
)
