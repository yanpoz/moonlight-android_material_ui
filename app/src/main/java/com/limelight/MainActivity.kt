package com.limelight

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.ui.theme.MoonlightandroidTheme
import com.limelight.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Data class for host information
data class Host(val name: String, val ip: String, val covers: List<String>)

// Sample host list
val hostList = listOf(
    Host("PHONKSSD", "192.168.1.1", listOf("cover_1", "cover_2", "cover_3", "cover_4")),
    Host("XENIA", "192.168.1.2", listOf("cover_5", "cover_6", "cover_7", "cover_8")),
    Host("HUAWEI", "192.168.1.3", listOf("cover_9", "cover_10", "cover_11", "cover_12"))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = viewModel()
            MainScreen(viewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    MoonlightandroidTheme {
        val context = LocalContext.current
        val sheetState = rememberModalBottomSheetState()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("Moonlight") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.showBottomSheet = true }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/".toUri()
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info")
                        }
                        IconButton(onClick = { viewModel.showSettings = true }) {
                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)) {
                LazyColumn {
                    items(hostList) { host ->
                        Text(text = host.name, modifier = Modifier.padding(8.dp))
                        Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                            LazyRow {
                                items(host.covers) { cover ->
                                    Image(
                                        painter = painterResource(R.drawable.cover_4),
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

        if (viewModel.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(id = com.limelight.R.string.title_add_pc))
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        value = viewModel.inputIp,
                        onValueChange = { viewModel.inputIp = it },
                        label = { Text(stringResource(id = com.limelight.R.string.ip_hint)) }
                    )
                }
            }
        }

        if (viewModel.showSettings) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showSettings = false },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(id = com.limelight.R.string.ip_hint))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(MainViewModel())
}