package com.limelight

import android.content.Intent
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.activity.compose.setContent
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import com.limelight.ui.theme.MoonlightandroidTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}


data class Host(val name: String, val ip: String, val covers: List<String>)
val hostList = listOf(
    Host("PHONKSSD", "192.168.1.1", listOf("cover_1", "cover_2", "cover_3", "cover_4")),
    Host("XENIA", "192.168.1.2", listOf("cover_5", "cover_6", "cover_7", "cover_8")),
    Host("HUAWEI", "192.168.1.3", listOf("cover_9", "cover_10", "cover_11", "cover_12"))
)



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainScreen() {
    MoonlightandroidTheme {
        var showDialog by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text("Moonlight")
                    },
                    navigationIcon = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        val context = LocalContext.current
                        IconButton(onClick = {
                           val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/".toUri()
                            }
                           context.startActivity(intent)
                        }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
        )
        {
            Column(modifier = Modifier.padding(it).padding(horizontal = 20.dp)){
                LazyColumn{
                    items(hostList.size) { host ->
                        Text(text = hostList[host].name, modifier = Modifier.padding(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            LazyRow {
                                items(hostList[host].covers.size){coverIndex ->
                                    Image(
                                        painter = painterResource(R.drawable.cover_1),
                                        contentDescription = "Game cover",
                                        modifier = Modifier.height(200.dp).padding(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
        if (showDialog) {
            AddHostDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    showDialog = false
                    // Handle host addition here
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHostDialog(onDismissRequest: () -> Unit, onConfirmation: () -> Unit) {
    var hostIP by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = com.limelight.R.string.title_add_pc)) },
        text = {
            OutlinedTextField(
                value = hostIP,
                onValueChange = { hostIP = it },
                label = { Text(stringResource(id = com.limelight.R.string.ip_hint)) }
            )
                 },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = com.limelight.R.string.applist_menu_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Add")
            }
        },
    )
}