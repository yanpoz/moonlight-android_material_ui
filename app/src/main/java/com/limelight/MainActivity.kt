package com.limelight

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainScreen() {
    MoonlightandroidTheme {
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = {
                        Text("Moonlight")
                    },
                    actions = {
                        IconButton(onClick = {
                                    val urlIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/")
                                    )

                        }) {
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
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                    text = { Text(text = "Add host") },
                )
            },
//            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
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