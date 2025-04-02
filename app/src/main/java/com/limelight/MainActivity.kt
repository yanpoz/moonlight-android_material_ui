package com.limelight

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.ContentView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.ui.theme.MoonlightandroidTheme
import com.limelight.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvHTTP
import java.io.StringReader

// Data class for host information
data class Host(val name: String, val ip: String, val covers: List<String>)

// Sample host list
val hostList = listOf(
    Host("PHONKSSD", "192.168.1.1", listOf("cover_1", "cover_2", "cover_3", "cover_4")),
    Host("XENIA", "192.168.1.2", listOf("cover_5", "cover_6", "cover_7", "cover_8")),
    Host("HUAWEI", "192.168.1.3", listOf("cover_9", "cover_10", "cover_11", "cover_12"))
)

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            viewModel = viewModel()
            val navController = rememberNavController()

            // Bind to the ComputerManagerService
            viewModel.bindService(this@MainActivity)

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        onSettingsClick = { navController.navigate("settings") },
                        viewModel = viewModel
                    )
                }
                composable("settings") {
                    SettingsScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unbindService(this)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    MoonlightandroidTheme {
        val context = LocalContext.current
        val sheetState = rememberModalBottomSheetState()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        // Observe computers from viewModel
        val computers = viewModel.computers

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
                        IconButton(onClick = onSettingsClick ) {
                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)) {
                if (computers.isEmpty()) {
                    // Show empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.scut_pc_not_found),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn {
                        items(computers) { computer ->
                            ComputerItem(computer)
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
                    Text(stringResource(id = R.string.title_add_pc))
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        value = viewModel.inputIp,
                        onValueChange = { viewModel.inputIp = it },
                        label = { Text(stringResource(id = R.string.ip_hint)) }
                    )

                    Button(
                        onClick = { viewModel.addComputer(context, viewModel.inputIp) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.title_add_pc))
                    }
                }
            }
        }

        if (viewModel.showSettings) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showSettings = false },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(id = R.string.category_ui_settings))
                    // Settings content
                }
            }
        }
    }
}

@Composable
fun ComputerItem(computer: ComputerDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = computer.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Status indicator
                val statusColor = when (computer.state) {
                    ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.primary
                    ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show IP address
            val address = computer.activeAddress?.address ?: computer.localAddress?.address
            ?: computer.remoteAddress?.address ?: computer.manualAddress?.address ?: "Unknown"

            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // If there are apps, show them in a row
            if (!computer.rawAppList.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                val apps = try {
                    NvHTTP.getAppListByReader(StringReader(computer.rawAppList))
                } catch (e: Exception) {
                    emptyList()
                }

                if (apps.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.category_ui_settings),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(apps) { app ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(120.dp)
                            ) {
                                // App icon or placeholder
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "app.name",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "app.name",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    MainScreen(MainViewModel())
//}

@Composable
fun SettingsScreen() {
    SampleNavigableListDetailPaneScaffoldFull()
}