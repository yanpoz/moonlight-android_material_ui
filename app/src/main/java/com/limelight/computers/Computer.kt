package com.limelight.computers

import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager

data class Computer(
    val details: ComputerDetails,
    val apps: List<NvApp> = emptyList(),
    val position: Int = 0,
    val pairPin: String? = null,
    val applistPoller: ComputerManagerService.ApplistPoller? = null
) {
    enum class State {
        IN_GAME,
        READY_TO_CONNECT,
        READY_TO_PAIR,
        OFFLINE,
        CONNECTING,
        ERROR
    }

    val state: State get() = when {
        details.pairState == PairingManager.PairState.FAILED -> State.ERROR
        details.pairState == PairingManager.PairState.PIN_WRONG -> State.ERROR
        details.state == ComputerDetails.State.OFFLINE -> State.OFFLINE
        details.state == ComputerDetails.State.UNKNOWN -> State.CONNECTING
        details.pairState != PairingManager.PairState.PAIRED -> State.READY_TO_PAIR
        details.runningGameId != 0 -> State.IN_GAME
        else -> State.READY_TO_CONNECT
    }

    fun getRunningApp(): NvApp? {
        if (details.runningGameId == 0) { return null }
        return apps.find { it.appId == details.runningGameId }
    }
}
