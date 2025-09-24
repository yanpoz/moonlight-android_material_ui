package com.limelight.repository

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import com.limelight.computers.ComposeComputerManagerListener
import com.limelight.computers.ComputerManagerService
import com.limelight.nvstream.http.ComputerDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ComputerRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var computerManagerBinder: ComputerManagerService.ComputerManagerBinder? = null
    private var computerManagerListener: ComposeComputerManagerListener? = null
    private val _computers = mutableStateListOf<ComputerDetails>()
    val computers: List<ComputerDetails> = _computers

    private val computerManagerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            repositoryScope.launch {
                computerManagerBinder = binder as ComputerManagerService.ComputerManagerBinder
                computerManagerBinder?.waitForReady()
                computerManagerListener = ComposeComputerManagerListener { computer ->
                    // Update the list on the Main thread as it's observed by Compose
                    repositoryScope.launch(Dispatchers.Main) {
                        val existingIndex = _computers.indexOfFirst { it.uuid == computer.uuid }
                        if (existingIndex >= 0) {
                            _computers[existingIndex] = computer
                        } else {
                            _computers.add(computer)
                        }
                    }
                }
                computerManagerBinder?.startPolling(computerManagerListener)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            computerManagerBinder = null
            // Consider clearing the listener or other cleanup if necessary
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, ComputerManagerService::class.java)
        context.bindService(intent, computerManagerServiceConnection, Service.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        try {
            computerManagerBinder?.stopPolling()
            context.unbindService(computerManagerServiceConnection)
        } catch (e: IllegalArgumentException) {
            // Service might not have been bound or already unbound
        }
    }

    fun addComputer(context: Context, ipAddress: String) {
        // TODO: Implement the logic to add a computer,
        // similar to how it would have been in the ViewModel.
        // This might involve using computerManagerBinder.
    }

    // Optional: A method to clean up resources like the CoroutineScope if needed,
    // though unbindService handles polling and service connection.
    // fun clear() {
    //     repositoryScope.cancel()
    // }
}
