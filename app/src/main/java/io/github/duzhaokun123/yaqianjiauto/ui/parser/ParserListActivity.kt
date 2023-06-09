package io.github.duzhaokun123.yaqianjiauto.ui.parser

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.model.ParserData
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.runIO

class ParserListActivity : ComponentActivity() {
    private val parserDataDao by lazy { (application as Application).db.parserDataDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var packageName by remember { mutableStateOf("") }
                    Column {
                        Row {
                            TextField(
                                value = packageName,
                                onValueChange = { packageName = it },
                                label = { Text("package name") })
                            IconButton(onClick = {
                                startActivity(Intent(this@ParserListActivity, EditParserActivity::class.java).apply {
                                    putExtra(EditParserActivity.EXTRA_INDEX, -1L)
                                })
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        }
                        val parserDatas by
                            parserDataDao.getAllFlow().collectAsState(initial = emptyList())
                        LazyColumn {
                            itemsIndexed(parserDatas.filter {
                                it.packageName.contains(packageName, true)
                            }) { index, parsedData ->
                                ParserDataCard(parsedData, index, index == parserDatas.size - 1) {
                                    runIO {
                                        val to = parserDatas[index + if (it) -1 else 1]
                                        parserDataDao.updateAll(
                                            listOf(
                                                parsedData.copy(index = to.index),
                                                to.copy(index = parsedData.index)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ParserDataCard(parserData: ParserData, index: Int, last: Boolean, onMove: (Boolean) -> Unit) {
        Card(modifier = Modifier
            .padding(8.dp),
            onClick = {
                startActivity(Intent(this@ParserListActivity, EditParserActivity::class.java).apply {
                    putExtra(EditParserActivity.EXTRA_INDEX, parserData.index)
                })
            }) {
            Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                Column{
                    Text(parserData.name, style = MaterialTheme.typography.titleMedium)
                    Text(parserData.packageName, style = MaterialTheme.typography.labelSmall)
                    Text(parserData.description, maxLines = 4, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                }

                Row(modifier = Modifier
                    .align(Alignment.CenterEnd)
                ) {
                    IconButton(onClick = { onMove(true) },
                        enabled = index != 0
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "up")
                    }
                    IconButton(onClick = { onMove(false) },
                        enabled = last.not()
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "down")
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreViewParserDataCard() {
        YA自动记账Theme {
            ParserDataCard(ParserData(0, "packageName", "code", "name", "description"), 0, false, {})
        }
    }
}