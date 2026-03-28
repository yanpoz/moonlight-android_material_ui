package com.limelight.viewmodel

import com.limelight.computers.Computer
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class MockMainViewModel : MainViewModel() {
    private val _computers = MutableStateFlow<List<Computer>>(emptyList())
    override val computers: StateFlow<List<Computer>> = _computers

    private val _isRefreshing = MutableStateFlow(false)
    override val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        val apps = listOf(
            NvApp("App 1", 1, false),
            NvApp("App 2", 2, true)
        )

        val details1 = ComputerDetails()
        details1.state = ComputerDetails.State.ONLINE
        details1.uuid = UUID.randomUUID().toString()
        details1.pairState = PairingManager.PairState.PAIRED
        details1.activeAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
        details1.name = "Gaming PC"
        details1.runningGameId = 2

        val computer1 = Computer(
            details = details1,
            apps = apps
        )

        val details2 = ComputerDetails()
        details2.uuid = UUID.randomUUID().toString()
        details2.pairState = PairingManager.PairState.NOT_PAIRED
        details2.activeAddress = ComputerDetails.AddressTuple("192.168.1.101", 47989)
        details2.name = "Workstation"

        val computer2 = Computer(
            details = details2,
            apps = emptyList()
        )

        _computers.value = listOf(computer1, computer2)
    }

    override fun updateComputerApps() {}
}
