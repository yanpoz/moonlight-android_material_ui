package com.limelight.computers

import com.limelight.nvstream.http.ComputerDetails

class ComposeComputerManagerListener(
    private val onComputerUpdated: (ComputerDetails) -> Unit
) : ComputerManagerListener {
    override fun notifyComputerUpdated(computer: ComputerDetails) {
        onComputerUpdated(computer)
    }
}