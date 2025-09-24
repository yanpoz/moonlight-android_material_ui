package com.limelight.viewmodel

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.limelight.R
import com.limelight.computers.ComposeComputerManagerListener
import com.limelight.computers.ComputerManagerService
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvHTTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    // UI properties
    var showBottomSheet by mutableStateOf(false)
    var inputIp by mutableStateOf("")

    // Properties for computer management
    private var computerManagerBinder: ComputerManagerService.ComputerManagerBinder? = null
    private var computerManagerListener: ComposeComputerManagerListener? = null
    private val _computers = mutableStateListOf<ComputerDetails>()
    val computers: List<ComputerDetails> = _computers

    // Connection state for binding to the service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            viewModelScope.launch(Dispatchers.IO){
                computerManagerBinder = binder as ComputerManagerService.ComputerManagerBinder
                computerManagerBinder?.waitForReady()
                computerManagerListener = ComposeComputerManagerListener { computer ->
                    // Update on the main thread
                    viewModelScope.launch(Dispatchers.Main) {
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
        }
    }

    // Functions to manage service binding
    fun bindService(context: Context) {
        val intent = Intent(context, ComputerManagerService::class.java)
        context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        try {
            computerManagerBinder?.stopPolling()
            context.unbindService(serviceConnection)
        } catch (e: IllegalArgumentException) {
            // Service might not have been bound
        }
    }

    // Function to add a computer by IP address
    fun addComputer(context: Context, ipAddress: String) {
        // TODO
    }

    override fun onCleared() {
        super.onCleared()
        // No need to unbind here as it's done in the Activity/Fragment
    }
}
