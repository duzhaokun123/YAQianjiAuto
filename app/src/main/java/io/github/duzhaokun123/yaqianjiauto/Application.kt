package io.github.duzhaokun123.yaqianjiauto

import androidx.room.Room
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import io.github.duzhaokun123.yaqianjiauto.room.AppDatabase

lateinit var application: Application

class Application : android.app.Application() {
    val db by lazy { Room.databaseBuilder(this, AppDatabase::class.java, "db").build() }

    init {
        application = this
        EzXHelperInit.initAppContext(this)
    }
}