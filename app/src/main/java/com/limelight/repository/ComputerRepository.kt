package com.limelight.repository

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.limelight.binding.PlatformBinding
import com.limelight.computers.ComposeComputerManagerListener
import com.limelight.computers.Computer
import com.limelight.computers.ComputerManagerService
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.NvHTTP
import com.limelight.nvstream.http.PairingManager
import com.limelight.nvstream.http.PairingManager.PairState
import com.limelight.nvstream.wol.WakeOnLanSender
import com.limelight.utils.ServerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.StringReader

class ComputerRepository {
    // Scopes
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val uiScope = MainScope() // Scope for main thread operations
    // Computers with Apps Lists
    private val _computers = MutableStateFlow<List<Computer>>(emptyList())
    val computers = _computers.asStateFlow()
    // Other
    private var connectionJob: Job? = null
    private var runningPolling: Boolean = false
    private var context: Context? = null
    // Constants
    private val connectionPollDelayMs = 500L
    val desktopAppId = 881448767

    // ComputerManagerService connection
    private var computerManagerBinder: ComputerManagerService.ComputerManagerBinder? = null
    private var computerManagerListener: ComposeComputerManagerListener? = null
    private val computerManagerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            repositoryScope.launch {
                computerManagerBinder = binder as? ComputerManagerService.ComputerManagerBinder
                computerManagerBinder?.waitForReady()

                // Initialize the listener if it has not been, or if service reconnected
                if (computerManagerListener == null) {
                    computerManagerListener = ComposeComputerManagerListener(
                        onComputerUpdated = ::processComputerDetails
                    )
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
            _computers.update { computers ->
                computers.map { computer ->
                    computer.applistPoller?.stop()
                    computer.copy(applistPoller = null, apps = emptyList())
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
            Service.BIND_AUTO_CREATE
        )
    }
    fun unbindService(context: Context) {
        try {
            pauseComputerUpdates() // Ensure polling is stopped
            _computers.value.forEach { it.applistPoller?.stop() }
            context.unbindService(computerManagerServiceConnection)
        } catch (e: IllegalArgumentException) {
            Log.e("ComputerRepository",
                "Service might not have been bound or already unbound", e
            )
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
        _computers.update { computers ->
            computers.map {
                if (it.details.uuid == computerUUID) updateAction(it) else it
            }
        }
    }
    private fun processComputerDetails(details: ComputerDetails) {
        _computers.update { computers ->
            val index = computers.indexOfFirst { it.details.uuid == details.uuid }
            val oldComputer = if (index != -1) computers[index] else null

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
                    computers.toMutableList().apply { this[index] = newComputer }
                } else {
                    computers + newComputer
                }
            } else {
                computers
            }
        }
    }
    fun addComputer(ipAddress: String) {
        // TODO: Implement the logic to add a computer,
        // similar to how it would have been in the ViewModel.
        // This might involve using computerManagerBinder.
    }
    fun initiateConnection(context: Context, computerUuid: String, onAppLaunched: () -> Unit) {
        connectionJob?.cancel()
        connectionJob = repositoryScope.launch {
            while (true) {
                val computer = computers.value.find { it.details.uuid == computerUuid } ?: break
                if (computer.details.activeAddress != null &&
                    computer.details.state != ComputerDetails.State.OFFLINE &&
                    computerManagerBinder != null
                ) {
                    if (computer.details.pairState != PairState.PAIRED) {
                        pairComputer(computer)
                        break
                    } else {
                        val desktopApp = computer.apps.find { it.appId == desktopAppId }
                        if (desktopApp != null) {
                            launchApp(context, desktopApp, computer, onAppLaunched)
                            break
                        }
                    }
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
    fun launchApp(context: Context, app: NvApp, computer: Computer, onAppLaunched: () -> Unit) {
        ServerHelper.doStart(context as Activity, app, computer.details, computerManagerBinder)
        onAppLaunched()
    }
    fun quitApp(context: Context, app: NvApp, computer: Computer) {
        if (computerManagerBinder == null) {
            Log.e(
                "ComputerRepository", 
                "ComputerManagerBinder not available, cannot quit app")
            return
        }
        ServerHelper.doQuit(
            context as Activity, computer.details, app, computerManagerBinder, null)
    }
    fun sendWakeOnLan(context: Context, computer: Computer) {
        if (computer.details.state == ComputerDetails.State.ONLINE) {
            // TODO: Implement Toasts
            Log.e("ComputerRepository", "Computer is already online")
        }
        if (computer.details.macAddress == null) {
            Log.e("ComputerRepository", "Computer has no MAC address")
        }
        repositoryScope.launch {
            try {
                WakeOnLanSender.sendWolPacket(computer.details)
            } catch (e: Exception) {
                Log.e("ComputerRepository", "Error sending Wake-On-LAN packet", e)
            }
        }
    }
    fun pollAppsForActiveComputers() {
        repositoryScope.launch {
            // Access _computers on the UI thread as it's a mutableStateListOf,
            // but run the actual pollNow() call in the repositoryScope (IO thread).
            val computersToPoll = synchronized(lock = _computers) { _computers.value.toList() }
            computersToPoll.forEach { computer -> computer.applistPoller?.pollNow() }
        }
    }
}
