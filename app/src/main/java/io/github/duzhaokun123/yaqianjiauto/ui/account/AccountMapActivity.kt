package io.github.duzhaokun123.yaqianjiauto.ui.account

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.yaqianjiauto.Application
import io.github.duzhaokun123.yaqianjiauto.R
import io.github.duzhaokun123.yaqianjiauto.model.AccountMap
import io.github.duzhaokun123.yaqianjiauto.ui.theme.YA自动记账Theme
import io.github.duzhaokun123.yaqianjiauto.utils.runIO

class AccountMapActivity : ComponentActivity() {
    val accountMapDao by lazy { (application as Application).db.accountMapDao() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YA自动记账Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showDialog by remember { mutableStateOf(false) }
                    Column {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        }
                        val accountMaps by accountMapDao.getAllFlow()
                            .collectAsState(initial = emptyList())
                        LazyColumn {
                            items(accountMaps) {
                                AccountMapCard(it)
                            }
                        }
                    }

                    if (showDialog) {
                        AddDialog(onDismiss = { showDialog = false })
                    }
                }
            }
        }
    }

    @Composable
    fun AccountMapCard(accountMap: AccountMap) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = accountMap.from, modifier = Modifier.align(Alignment.CenterStart))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "to",
                    modifier = Modifier.align(Alignment.Center)
                )
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        text = accountMap.to ?: "<none>",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(onClick = {
                        runIO {
                            accountMapDao.delete(accountMap)
                        }
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun AccountMapCardPreview() {
        YA自动记账Theme {
            AccountMapCard(AccountMap("from", "to"))
        }
    }

    @Composable
    fun AddDialog(onDismiss: () -> Unit) {
        var from by remember { mutableStateOf("") }
        var to by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    runIO {
                        accountMapDao.upsert(AccountMap(from, to.takeIf { it.isNotBlank() }))
                    }
                    onDismiss()
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }, title = {
                Text(stringResource(R.string.add))
            }, text = {
                Column {
                    OutlinedTextField(value = from, onValueChange = { from = it },
                        label = { Text("from") })
                    Icon(Icons.Default.ArrowDownward, contentDescription = "to", modifier = Modifier.align(Alignment.CenterHorizontally))
                    OutlinedTextField(value = to, onValueChange = { to = it },
                        label = { Text("to") })
                }
            })
    }

    @Preview
    @Composable
    fun AddDialogPreview() {
        YA自动记账Theme {
            AddDialog {}
        }
    }
}