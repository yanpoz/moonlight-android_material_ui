package com.limelight.repository

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.limelight.R
import com.limelight.binding.PlatformBinding
import com.limelight.computers.ComposeComputerManagerListener
import com.limelight.computers.Computer
import com.limelight.computers.ComputerManagerService
import com.limelight.grid.assets.DiskAssetLoader
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.NvHTTP
import com.limelight.nvstream.http.PairingManager
import com.limelight.nvstream.http.PairingManager.PairState
import com.limelight.nvstream.jni.MoonBridge
import com.limelight.nvstream.wol.WakeOnLanSender
import com.limelight.utils.ServerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    // Computers with Apps Lists
    private val _computers = MutableStateFlow<List<Computer>>(emptyList())
    val computers = _computers.asStateFlow()
    // Unique ID
    private val _uniqueId = MutableStateFlow<String?>(null)
    val uniqueId = _uniqueId.asStateFlow()
    // Connection Status
    enum class ConnectionStatus { IDLE, CONNECTING, SUCCESS, FAILED, CANCELED }
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.IDLE)
    val connectionStatus = _connectionStatus.asStateFlow()

    // Network Test Status
    sealed class NetworkTestStatus {
        data object Idle : NetworkTestStatus()
        data object Running : NetworkTestStatus()
        data class Finished(val result: String) : NetworkTestStatus()
    }
    private val _networkTestStatus = MutableStateFlow<NetworkTestStatus>(NetworkTestStatus.Idle)
    val networkTestStatus = _networkTestStatus.asStateFlow()

    // Other
    private var connectionJob: Job? = null
    private var runningPolling: Boolean = false
    private var context: Context? = null
    private val prefs by lazy { context?.getSharedPreferences("computer_order", Context.MODE_PRIVATE) }
    private val appPrefs by lazy { context?.getSharedPreferences("app_order", Context.MODE_PRIVATE) }
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
                _uniqueId.value = computerManagerBinder?.uniqueId

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
            _uniqueId.value = null
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
    private fun getComputer(computerUuid: String) = computers.value.find { it.details.uuid == computerUuid }
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
            val apps = details.rawAppList?.let { raw ->
                NvHTTP.getAppListByReader(StringReader(raw)).map { app ->
                    app.apply { position = getSavedAppPosition(details.uuid, app.appId) }
                }.sortedWith(compareBy<NvApp> { it.position }.thenBy { it.appName })
            } ?: oldComputer?.apps // Preserve the existing list if no new raw data is present
                ?: emptyList()

            // App list polling is only done when paired.
            val applistPoller = if (details.pairState == PairState.PAIRED && computerManagerBinder != null) {
                // If paired, and we have a binder, ensure we have an active poller.
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
                position = getSavedComputerPosition(details.uuid),
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

    private fun getSavedComputerPosition(uuid: String): Int {
        // Returns the saved position, or a very high number (Int.MAX_VALUE)
        // so new computers appear at the end by default
        return prefs?.getInt(uuid, Int.MAX_VALUE) ?: Int.MAX_VALUE
    }
    fun updateComputerPosition(uuid: String, newPosition: Int) {
        prefs?.edit()?.putInt(uuid, newPosition)?.apply()

        // Refresh the list to apply sorting
        _computers.update { list ->
            list.map {
                if (it.details.uuid == uuid) it.copy(position = newPosition) else it
            }
        }
    }

    fun moveComputerUp(uuid: String) {
        val currentList = _computers.value.sortedWith(
            compareBy<Computer> { it.position }.thenBy { it.details.name }
        )
        val index = currentList.indexOfFirst { it.details.uuid == uuid }
        if (index > 0) {
            val computerToMove = currentList[index]
            val computerAbove = currentList[index - 1]

            val newPosAbove = index
            val newPosToMove = index - 1

            updateComputerPosition(computerToMove.details.uuid, newPosToMove)
            updateComputerPosition(computerAbove.details.uuid, newPosAbove)
        }
    }

    fun moveComputerDown(uuid: String) {
        val currentList = _computers.value.sortedWith(
            compareBy<Computer> { it.position }.thenBy { it.details.name }
        )
        val index = currentList.indexOfFirst { it.details.uuid == uuid }
        if (index != -1 && index < currentList.size - 1) {
            val computerToMove = currentList[index]
            val computerBelow = currentList[index + 1]

            val newPosBelow = index
            val newPosToMove = index + 1

            updateComputerPosition(computerToMove.details.uuid, newPosToMove)
            updateComputerPosition(computerBelow.details.uuid, newPosBelow)
        }
    }

    private fun getSavedAppPosition(computerUuid: String, appId: Int): Int {
        return appPrefs?.getInt("${computerUuid}_$appId", Int.MAX_VALUE) ?: Int.MAX_VALUE
    }

    fun updateAppPosition(computerUuid: String, appId: Int, newPosition: Int) {
        appPrefs?.edit()?.putInt("${computerUuid}_$appId", newPosition)?.apply()

        // Refresh the list to apply sorting
        _computers.update { list ->
            list.map { computer ->
                if (computer.details.uuid == computerUuid) {
                    val updatedApps = computer.apps.map { app ->
                        if (app.appId == appId) {
                            app.apply { position = newPosition }
                        } else app
                    }.sortedWith(compareBy<NvApp> { it.position }.thenBy { it.appName })
                    computer.copy(apps = updatedApps)
                } else computer
            }
        }
    }

    fun moveAppUp(computerUuid: String, appId: Int) {
        val computer = getComputer(computerUuid) ?: return
        val currentApps = computer.apps
        val index = currentApps.indexOfFirst { it.appId == appId }
        if (index > 0) {
            val appToMove = currentApps[index]
            val appAbove = currentApps[index - 1]

            val newPosAbove = index
            val newPosToMove = index - 1

            updateAppPosition(computerUuid, appToMove.appId, newPosToMove)
            updateAppPosition(computerUuid, appAbove.appId, newPosAbove)
        }
    }

    fun moveAppDown(computerUuid: String, appId: Int) {
        val computer = getComputer(computerUuid) ?: return
        val currentApps = computer.apps
        val index = currentApps.indexOfFirst { it.appId == appId }
        if (index != -1 && index < currentApps.size - 1) {
            val appToMove = currentApps[index]
            val appBelow = currentApps[index + 1]

            val newPosBelow = index
            val newPosToMove = index + 1

            updateAppPosition(computerUuid, appToMove.appId, newPosToMove)
            updateAppPosition(computerUuid, appBelow.appId, newPosBelow)
        }
    }

    fun initiateConnection(context: Context, computerUuid: String) {
        connectionJob?.cancel()
        _connectionStatus.value = ConnectionStatus.CONNECTING
        connectionJob = repositoryScope.launch {
            while (true) {
                val computer = getComputer(computerUuid) ?: break
                if (computer.details.activeAddress != null &&
                    computer.details.state != ComputerDetails.State.OFFLINE &&
                    computerManagerBinder != null
                ) {
                    if (computer.details.pairState != PairState.PAIRED) {
                        pairComputer(computer.details.uuid)
                        break
                    } else {
                        val desktopApp = computer.apps.find { it.appId == desktopAppId }
                        if (desktopApp != null) {
                            launchApp(context, desktopApp, computer.details.uuid)
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
        _connectionStatus.value = ConnectionStatus.CANCELED
    }

    fun resetConnectionStatus() {
        _connectionStatus.value = ConnectionStatus.IDLE
    }

    fun pairComputer(computerUuid: String) {
        val computer = getComputer(computerUuid) ?: return
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
                return
            }
            val pairPin = computer.pairPin ?: PairingManager.generatePinString()
            modifyComputer(computer.details.uuid) { it.copy(pairPin = pairPin) }
            val pairingManager = httpConn.pairingManager

            val pairResult = pairingManager.pair(
                httpConn.getServerInfo(true),
                pairPin
            )

            if (pairResult == PairState.PAIRED) {
                computerManagerBinder?.getComputer(computer.details.uuid)?.serverCert =
                    pairingManager.pairedCert
            } else {
                _connectionStatus.value = ConnectionStatus.FAILED
            }
        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.FAILED
            Log.e("ComputerRepository", "Error pairing computer", e)
        } finally {
            resumeComputerUpdates()
        }
    }
    fun launchApp(context: Context, app: NvApp, computerUuid: String) {
        val computer = getComputer(computerUuid)
        if (computer != null) {
            ServerHelper.doStart(context as Activity, app, computer.details, computerManagerBinder)
            _connectionStatus.value = ConnectionStatus.SUCCESS
        } else {
            _connectionStatus.value = ConnectionStatus.FAILED
        }
    }
    fun quitApp(context: Context, app: NvApp, computerUuid: String) {
        if (computerManagerBinder == null) {
            Log.e(
                "ComputerRepository",
                "ComputerManagerBinder not available, cannot quit app")
            return
        }
        val computer = getComputer(computerUuid) ?: return
        ServerHelper.doQuit(
            context as Activity, computer.details, app, computerManagerBinder, null)
    }
    fun sendWakeOnLan(context: Context, computerUuid: String) {
        val computer = getComputer(computerUuid) ?: return

        if (computer.details.state == ComputerDetails.State.ONLINE) {
            // TODO: Implement Toasts
            Log.e("ComputerRepository", "Computer is already online")
            return
        }
        if (computer.details.macAddress == null) {
            Log.e("ComputerRepository", "Computer has no MAC address")
            return
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

    fun deleteComputer(computerUuid: String) {
        val computer = getComputer(computerUuid) ?: return
        val currentContext = context ?: return

        // 1. Remove from service/DB
        computerManagerBinder?.removeComputer(computer.details)

        // 2. Delete assets
        DiskAssetLoader(currentContext).deleteAssetsForComputer(computer.details.uuid)

        // 3. Delete hidden apps pref
        currentContext.getSharedPreferences("HiddenApps", Context.MODE_PRIVATE)
            .edit()
            .remove(computer.details.uuid)
            .apply()

        // 4. Disable shortcuts TODO


        // 5. Update local state
        _computers.update { list ->
            list.filter { it.details.uuid != computerUuid }
        }
    }

    fun testNetwork(context: Context) {
        repositoryScope.launch {
            _networkTestStatus.value = NetworkTestStatus.Running
            val ret = MoonBridge.testClientConnectivity(
                ServerHelper.CONNECTION_TEST_SERVER, 443, MoonBridge.ML_PORT_FLAG_ALL
            )
            val resultMessage = when (ret) {
                MoonBridge.ML_TEST_RESULT_INCONCLUSIVE ->
                    context.getString(R.string.nettest_text_inconclusive)
                0 -> context.getString(R.string.nettest_text_success)
                else -> context.getString(R.string.nettest_text_failure) +
                        MoonBridge.stringifyPortFlags(ret, "\n")
            }
            _networkTestStatus.value = NetworkTestStatus.Finished(resultMessage)
        }
    }

    fun dismissNetworkTest() {
        _networkTestStatus.value = NetworkTestStatus.Idle
    }
}
