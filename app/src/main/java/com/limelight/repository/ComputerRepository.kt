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
import com.limelight.nvstream.http.NvApp
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

data class Computer(
    val details: ComputerDetails,
    val apps: List<NvApp> = emptyList(),
    val pairResult: PairState? = null,
    val pairPin: String? = null
)

class ComputerRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val uiScope = MainScope() // Scope for main thread operations

    private var computerManagerBinder: ComputerManagerService.ComputerManagerBinder? = null
    private var computerManagerListener: ComposeComputerManagerListener? = null
    private val _computers = mutableStateListOf<Computer>()
    val computers: List<Computer> = _computers

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
                    computerManagerListener = ComposeComputerManagerListener { details ->
                        val existingIndex = _computers.indexOfFirst {
                            it.details.uuid == details.uuid
                        }
                        if (existingIndex >= 0) {
                            val currentComputer = _computers[existingIndex]
                            if (currentComputer.details != details) { // Avoid unnecessary updates
                                updateComputer(details.uuid) { it.copy(details = details) }
                            }
                        } else {
                            // Add a new computer with a default ComputerState
                            uiScope.launch {
                                _computers.add(Computer(details = details))
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
        context.bindService(
            intent,
            computerManagerServiceConnection,
            Service.BIND_AUTO_CREATE)
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

    private fun updateComputer(computerUUID: String, updateAction: (Computer) -> Computer) {
        uiScope.launch {
            val index = _computers.indexOfFirst { it.details.uuid == computerUUID }
            if (index != -1) {
                _computers[index] = updateAction(_computers[index])
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
                val computer = computers.find { it.details.uuid == computerUUID } ?: break
                if (computer.details.activeAddress != null &&
                    computer.details.state != ComputerDetails.State.OFFLINE &&
                    computer.details.pairState != PairState.PAIRED &&
                    computerManagerBinder != null
                ) {
                    pairComputer(computer)
                    break
                }
                delay(500L)
            }
        }
    }

    fun cancelConnection() {
        connectionJob?.cancel()
    }

    fun pairComputer(computer: Computer) {
        try {
            pauseComputerUpdates()

            val httpConn = NvHTTP(
                ServerHelper.getCurrentAddressFromComputer(computer.details),
                computer.details.httpsPort,
                computerManagerBinder?.uniqueId,
                computer.details.serverCert,
                PlatformBinding.getCryptoProvider(context)
            )
            if (httpConn.pairState == PairState.PAIRED) return
            val pairPin = computer.pairPin ?: PairingManager.generatePinString()
            updateComputer(computer.details.uuid) { it.copy(pairPin = pairPin) }
            val pairingManager = httpConn.pairingManager

            val pairResult = pairingManager.pair(
                httpConn.getServerInfo(true),
                pairPin
            )

            updateComputer(computer.details.uuid) { it.copy(pairResult = pairResult) }
            computerManagerBinder?.getComputer(computer.details.uuid)?.serverCert =
                pairingManager.pairedCert
            computerManagerBinder?.invalidateStateForComputer(computer.details.uuid)

        } catch (e: IndexOutOfBoundsException) {
            // Computer not found in list, so we can't pair.
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
