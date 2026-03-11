package com.limelight.computers

import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager

data class Computer(
    val details: ComputerDetails,
    val apps: List<NvApp> = emptyList(),
    val position: Int = 0,
    val pairResult: PairingManager.PairState? = null, //needed
    val pairPin: String? = null,
    val applistPoller: ComputerManagerService.ApplistPoller? = null
) {
    fun getRunningApp(): NvApp? {
        if (details.runningGameId == 0) { return null }
        return apps.find { it.appId == details.runningGameId }
    }
}
