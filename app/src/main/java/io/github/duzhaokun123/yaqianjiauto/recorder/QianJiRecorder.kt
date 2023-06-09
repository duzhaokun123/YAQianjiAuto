package io.github.duzhaokun123.yaqianjiauto.recorder

import android.content.Intent
import android.net.Uri
import io.github.duzhaokun123.yaqianjiauto.application
import io.github.duzhaokun123.yaqianjiauto.model.MappedClassifiedParsedData
import io.github.duzhaokun123.yaqianjiauto.model.ParsedData
import io.github.duzhaokun123.yaqianjiauto.utils.toDataTime

object QianJiRecorder: BaseRecorder {
    const val TAG = "QianJiRecorder"
    override val name: String = "钱迹"
    override fun record(mappedClassifiedParsedData: MappedClassifiedParsedData) {
        when(mappedClassifiedParsedData.classifiedParsedData.parsedData!!.type) {
            ParsedData.Type.Expense -> expenseIncom(mappedClassifiedParsedData)
            ParsedData.Type.Income -> expenseIncom(mappedClassifiedParsedData)
            ParsedData.Type.Transfer -> transfer(mappedClassifiedParsedData)
            else -> throw Exception("unknown parsed data type: ${mappedClassifiedParsedData.classifiedParsedData.parsedData.type}")
        }
    }

    private fun expenseIncom(mappedClassifiedParsedData: MappedClassifiedParsedData) {
        val uri = Uri.Builder()
            .scheme("qianji")
            .authority("publicapi")
            .path("addbill")
            .appendQueryParameter("money", mappedClassifiedParsedData.classifiedParsedData.parsedData!!.balance.toString())
            .appendQueryParameter("time", mappedClassifiedParsedData.classifiedParsedData.parsedData.timestamp.toDataTime())
            .appendQueryParameter("remark", "${mappedClassifiedParsedData.classifiedParsedData.parsedData.target} - ${mappedClassifiedParsedData.classifiedParsedData.parsedData.remark}")
            .appendQueryParameter("catename", mappedClassifiedParsedData.classifiedParsedData.subcategory ?: mappedClassifiedParsedData.classifiedParsedData.category)
//            .appendQueryParameter("accountname", mappedClassifiedParsedData.overrideAccount)
            .appendQueryParameter("showresutl", "1")

        mappedClassifiedParsedData.overrideAccount?.let {
            uri.appendQueryParameter("accountname", it)
        }

        when(mappedClassifiedParsedData.classifiedParsedData.parsedData.type) {
            ParsedData.Type.Expense -> uri.appendQueryParameter("type", "0")
            ParsedData.Type.Income -> uri.appendQueryParameter("type", "1")
            else -> throw Exception("unknown parsed data type: ${mappedClassifiedParsedData.classifiedParsedData.parsedData.type}")
        }

        application.startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri.build()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun transfer(mappedClassifiedParsedData: MappedClassifiedParsedData) {

    }
}