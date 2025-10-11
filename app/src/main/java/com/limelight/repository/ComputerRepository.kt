package com.limelight.repository

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import com.limelight.binding.PlatformBinding
import com.limelight.computers.ComposeComputerManagerListener
import com.limelight.computers.ComputerManagerService
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvHTTP
import com.limelight.nvstream.http.PairingManager
import com.limelight.nvstream.http.PairingManager.PairState
import com.limelight.utils.ServerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComputerRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val uiScope = MainScope() // Scope for main thread operations

    private var computerManagerBinder: ComputerManagerService.ComputerManagerBinder? = null
    private var computerManagerListener: ComposeComputerManagerListener? = null
    private val _computers = mutableStateListOf<ComputerDetails>()
    val computers: List<ComputerDetails> = _computers

    private var connectionJob: Job? = null

    private var runningPolling = false
    private var context: Context? = null

    private val computerManagerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            repositoryScope.launch {
                computerManagerBinder = binder as? ComputerManagerService.ComputerManagerBinder
                computerManagerBinder?.waitForReady()

                // Initialize the listener if it has not been, or if service reconnected
                if (computerManagerListener == null) {
                    computerManagerListener = ComposeComputerManagerListener { computer ->
                        // Update the list on the Main thread as it's observed by Compose
                        uiScope.launch {
                            val existingIndex = _computers.indexOfFirst { it.uuid == computer.uuid }
                            if (existingIndex >= 0) {
                                if (_computers[existingIndex] != computer) { // Avoid unnecessary updates
                                    _computers[existingIndex] = computer
                                }
                            } else {
                                _computers.add(computer)
                            }
                        }
                    }
                }
                if (computerManagerBinder != null && !runningPolling && computerManagerListener != null) {
                    computerManagerBinder?.startPolling(computerManagerListener!!)
                    runningPolling = true
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            computerManagerBinder = null
            runningPolling = false // Reset polling state
            // Optionally clear computerManagerListener = null if it must be recreated
        }
    }

    fun bindService(context: Context) {
        this@ComputerRepository.context = context.applicationContext
        val intent = Intent(context, ComputerManagerService::class.java)
        context.bindService(intent, computerManagerServiceConnection, Service.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        try {
            pauseComputerUpdates() // Ensure polling is stopped
            context.unbindService(computerManagerServiceConnection)
        } catch (e: IllegalArgumentException) {
            // Service might not have been bound or already unbound
        }
    }

    fun resumeComputerUpdates() {
        repositoryScope.launch { // Ensure binder calls are off the main thread if they block
            if (computerManagerBinder != null && !runningPolling && computerManagerListener != null) {
                computerManagerBinder?.startPolling(computerManagerListener!!)
                runningPolling = true
            }
        }
    }

    fun pauseComputerUpdates() {
        repositoryScope.launch { // Ensure binder calls are off the main thread if they block
            if (computerManagerBinder != null && runningPolling) {
                computerManagerBinder?.stopPolling()
                // Consider computerManagerBinder?.waitForPollingStopped() if available and non-blocking
                runningPolling = false
            }
        }
    }

    fun addComputer(ipAddress: String) {
        // TODO: Implement the logic to add a computer,
        // similar to how it would have been in the ViewModel.
        // This might involve using computerManagerBinder.
    }

    fun initiateConnection(computerUUID: String) {
        connectionJob?.cancel()
        connectionJob = repositoryScope.launch {
            while (true) {
                val computer = computers.find { it.uuid == computerUUID } ?: break
                if (computer.activeAddress != null &&
                    computer.state != ComputerDetails.State.OFFLINE &&
                    computerManagerBinder != null
                ) {
                    if (computer.pairState != PairState.PAIRED) {
                        pairComputer(computer)
                        break
                    }
                }
                delay(1000L)
            }
        }
    }

    fun cancelConnection() {
        connectionJob?.cancel()
    }

    fun pairComputer(computer: ComputerDetails) {
        try {
            pauseComputerUpdates()

            val httpConn = NvHTTP(
                ServerHelper.getCurrentAddressFromComputer(computer),
                computer.httpsPort,
                computerManagerBinder?.uniqueId,
                computer.serverCert,
                PlatformBinding.getCryptoProvider(context)
            )
            if (httpConn.pairState == PairState.PAIRED) return
            val pin = PairingManager.generatePinString()
        } catch (e: Exception) {
            // Handle exceptions if necessary. Revert to original state.
        } finally {
            resumeComputerUpdates()
        }
    }

    // Optional: A method to clean up resources like the CoroutineScope if needed.
    // fun clear() {
    // repositoryScope.cancel()
    // uiScope.cancel()
    // }
}