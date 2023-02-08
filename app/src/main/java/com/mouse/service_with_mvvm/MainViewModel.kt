package com.mouse.service_with_mvvm

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.ContentValues
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*


class MainViewModel: ViewModel() {
    val mBinder: MutableLiveData<BlueService.BlueBinder> = MutableLiveData<BlueService.BlueBinder>()
    val isServiceCompleted:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    var _blueDeviceList = MutableStateFlow<List<String>>(arrayListOf())
    val blueDeviceList=_blueDeviceList.asStateFlow()
    val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(ContentValues.TAG, "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            println("####onServiceConnected")
            mBinder.value=iBinder as BlueService.BlueBinder
            isServiceCompleted.value=true
//            blueDeviceList=mBinder.getService().getBlueDeviceList()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(ContentValues.TAG, "ServiceConnection: disconnected from service.")
            mBinder.value=null
        }
    }
    suspend fun scan(){
        println("####viewmodle.scan")

        mBinder.value!!.getService()!!.scan().collect{
            println("####viewmodle.scan.it=$it")
            _blueDeviceList.value+=it
            _blueDeviceList.emit(_blueDeviceList.value)
        }

        println("####viewmodle.scan2")
    }

}