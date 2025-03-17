package com.limelight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainViewModel : ViewModel() {
    var showBottomSheet by mutableStateOf(false)
    var showSettings by mutableStateOf(false)
    var inputIp by mutableStateOf("192.168.1.1")
}
