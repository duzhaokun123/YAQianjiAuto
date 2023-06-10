package io.github.duzhaokun123.yaqianjiauto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.duzhaokun123.yaqianjiauto.accountmapper.AccountMapper
import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.classifier.Classifierer
import io.github.duzhaokun123.yaqianjiauto.model.Data
import io.github.duzhaokun123.yaqianjiauto.model.Log
import io.github.duzhaokun123.yaqianjiauto.parser.Parserer
import io.github.duzhaokun123.yaqianjiauto.ui.record.RecordOverlayWindow
import io.github.duzhaokun123.yaqianjiauto.utils.TipUtil
import io.github.duzhaokun123.yaqianjiauto.utils.gson
import io.github.duzhaokun123.yaqianjiauto.utils.record
import io.github.duzhaokun123.yaqianjiauto.utils.runIO
import io.github.duzhaokun123.yaqianjiauto.utils.runMain
import io.github.duzhaokun123.yaqianjiauto.xposed.DataSender

class DataReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "DataReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DataSender.ACTION_SEND_DATA -> {
                val data = intent.getStringExtra(DataSender.EXTRA_DATA) ?: return
                val format = intent.getStringExtra(DataSender.EXTRA_FORMAT) ?: return
                val packageName = intent.getStringExtra(DataSender.EXTRA_PACKAGE_NAME) ?: return
                val dataDao = application.db.dataDao()
                runIO {
                    val data = Data(System.currentTimeMillis(), data, format, packageName)
                    dataDao.insert(data)
                    data.record(5, mini = true)
                }
            }

            DataSender.ACTION_SEND_LOG -> {
                val tag = intent.getStringExtra(DataSender.EXTRA_LOG_TAG) ?: return
                val log = intent.getStringExtra(DataSender.EXTRA_LOG) ?: return
                val packageName = intent.getStringExtra(DataSender.EXTRA_PACKAGE_NAME) ?: return
                val logDao = application.db.logDao()
                runIO {
                    logDao.insert(
                        Log(
                            System.currentTimeMillis(),
                            tag,
                            log,
                            packageName
                        )
                    )
                }
            }
        }
    }
}