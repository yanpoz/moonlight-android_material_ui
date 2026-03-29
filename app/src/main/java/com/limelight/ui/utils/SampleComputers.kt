package com.limelight.ui.utils

import com.limelight.computers.Computer
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

object SampleComputers {
    val RunningGameComputer = Computer(
        details = ComputerDetails().apply {
            name = "Running DOOM"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 456
        },
        apps = emptyList()
    )

    val OnlinePairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Online Paired"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val OnlineUnpairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Online not paired"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.NOT_PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val OfflineComputer = Computer(
        details = ComputerDetails().apply {
            name = "Offline Paired"
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

    val PairingFailedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Failed PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PIN_WRONG
            runningGameId = 0
        },
        apps = emptyList()
    )
}
