package com.mouse.service_with_mvvm

import kotlinx.coroutines.flow.Flow

interface IBlueUtil {
    val blueDeviceList: Flow<String>

    //    suspend fun scan(): Flow<String>//掃描
    suspend fun connect()//連線
}