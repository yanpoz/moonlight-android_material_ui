package com.limelight.ui.utils

import com.limelight.computers.Computer
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

object SampleData {
    val OnlinePairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Gaming PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val OnlineUnpairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Gaming PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.NOT_PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val OfflineComputer = Computer(
        details = ComputerDetails().apply {
            name = "Offline PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.OFFLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val UnknownComputer = Computer(
        details = ComputerDetails().apply {
            name = "Unknown PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.UNKNOWN
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )
}
