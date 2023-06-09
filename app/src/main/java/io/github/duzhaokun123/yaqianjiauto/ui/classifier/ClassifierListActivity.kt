package io.github.duzhaokun123.yaqianjiauto.ui.classifier

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.model.ClassifierData
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.runIO

class ClassifierListActivity : ComponentActivity() {
    private val classifierDataDao by lazy { (application as Application).db.classifierDataDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Box(Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = {
                                    startActivity(
                                        Intent(this@ClassifierListActivity, EditClassifierActivity::class.java)
                                    )
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        }
                        val classifierDatas by
                        classifierDataDao.getAllFlow().collectAsState(initial = emptyList())
                        LazyColumn {
                            itemsIndexed(classifierDatas) { index, classifierData ->
                                ClassifierDataCard(classifierData, index, index == classifierDatas.size - 1) {
                                    runIO {
                                        val to = classifierDatas[index + if (it) -1 else 1]
                                        classifierDataDao.updateAll(
                                            listOf(
                                                classifierData.copy(index = to.index),
                                                to.copy(index = classifierData.index)
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
    fun ClassifierDataCard(classifierData: ClassifierData, index: Int, last: Boolean, onMove: (Boolean) -> Unit) {
        Card(modifier = Modifier.padding(8.dp),
            onClick = {
                startActivity(Intent(this, EditClassifierActivity::class.java).apply {
                    putExtra(EditClassifierActivity.EXTRA_INDEX, classifierData.index)
                })
            }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    Text(classifierData.name, style = MaterialTheme.typography.titleMedium)
                    Text(ClassifierData.typeToStr(classifierData.type), style = MaterialTheme.typography.labelSmall)
                    Text(classifierData.description, maxLines = 4, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                }
                Row(modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
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
}