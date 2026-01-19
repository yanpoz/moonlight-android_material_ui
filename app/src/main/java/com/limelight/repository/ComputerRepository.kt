package com.limelight.repository

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
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
import java.io.StringReader

data class Computer(
    val details: ComputerDetails,
    val apps: List<NvApp> = emptyList(),
    val pairResult: PairState? = null,
    val pairPin: String? = null,
    val applistPoller: ComputerManagerService.ApplistPoller? = null
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
    private val connectionPollDelayMs = 500L

    private val computerManagerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            repositoryScope.launch {
                computerManagerBinder = binder as? ComputerManagerService.ComputerManagerBinder
                computerManagerBinder?.waitForReady()

                // Initialize the listener if it has not been, or if service reconnected
                if (computerManagerListener == null) {
                    computerManagerListener = ComposeComputerManagerListener(
                        ::processComputerDetails)
                }
                if (computerManagerBinder != null &&
                    computerManagerListener != null &&
                    !runningPolling
                    ) {
                    computerManagerBinder!!.startPolling(computerManagerListener!!)
                    runningPolling = true
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            computerManagerBinder = null
            runningPolling = false
            uiScope.launch {
                _computers.forEachIndexed { index, computer ->
                    if (computer.applistPoller != null) {
                        computer.applistPoller.stop()
                        _computers[index] = computer.copy(applistPoller = null, apps = emptyList())
                    }
                }
            }
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
            _computers.forEach { it.applistPoller?.stop() }
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

    private fun modifyComputer(computerUUID: String, updateAction: (Computer) -> Computer) {
        uiScope.launch {
            val index = _computers.indexOfFirst { it.details.uuid == computerUUID }
            if (index != -1) {
                _computers[index] = updateAction(_computers[index])
            }
        }
    }

    private fun processComputerDetails(details: ComputerDetails) {
        uiScope.launch {
            val index = _computers.indexOfFirst { it.details.uuid == details.uuid }
            val oldComputer = if (index != -1) _computers[index] else null

            // Always try to parse the app list from details if available.
            val apps = details.rawAppList?.let { NvHTTP.getAppListByReader(StringReader(it)) }
                ?: oldComputer?.apps // Preserve the existing list if no new raw data is present
                ?: emptyList()

            // App list polling is only done when paired.
            val applistPoller = if (details.pairState == PairState.PAIRED && computerManagerBinder != null) {
                // If paired and we have a binder, ensure we have an active poller.
                oldComputer?.applistPoller
                    ?: computerManagerBinder!!.createAppListPoller(details).also { it.start() }
            } else {
                // If not paired or binder is gone, stop any existing poller.
                oldComputer?.applistPoller?.stop()
                null
            }

            // Preserve fields not included in ComputerDetails (pairResult, pairPin)
            val newComputer = Computer(
                details = details,
                apps = apps,
                pairResult = oldComputer?.pairResult,
                pairPin = oldComputer?.pairPin,
                applistPoller = applistPoller
            )

            if (oldComputer != newComputer) {
                if (index != -1) {
                    _computers[index] = newComputer
                } else {
                    _computers.add(newComputer)
                }
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
                delay(connectionPollDelayMs)
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
            if (httpConn.pairState == PairState.PAIRED) {
                modifyComputer(computer.details.uuid) {
                    it.copy(pairResult = PairState.PAIRED)
                }
                return
            }
            val pairPin = computer.pairPin ?: PairingManager.generatePinString()
            modifyComputer(computer.details.uuid) { it.copy(pairPin = pairPin) }
            val pairingManager = httpConn.pairingManager

            val pairResult = pairingManager.pair(
                httpConn.getServerInfo(true),
                pairPin
            )

            modifyComputer(computer.details.uuid) { it.copy(pairResult = pairResult) }

            if (pairResult == PairState.PAIRED) {
                computerManagerBinder?.getComputer(computer.details.uuid)?.serverCert =
                    pairingManager.pairedCert
            }
        } catch (e: Exception) {
            Log.e("ComputerRepository", "Error pairing computer", e)
        } finally {
            resumeComputerUpdates()
        }
    }

    fun launchApp(context: Context, app: NvApp, computer: Computer) {
        ServerHelper.doStart(context as Activity?, app, computer.details, computerManagerBinder)
    }

    fun pollAppsForActiveComputers() {
        repositoryScope.launch {
            // Access _computers on the UI thread as it's a mutableStateListOf,
            // but run the actual pollNow() call in the repositoryScope (IO thread).
            val computersToPoll = synchronized(_computers) {
                _computers.toList()
            }
            computersToPoll.forEach { computer ->
                computer.applistPoller?.pollNow()
            }
        }
    }

    // Optional: A method to clean up resources like the CoroutineScope if needed.
    // fun clear() {
    // repositoryScope.cancel()
    // uiScope.cancel()
    // }
}