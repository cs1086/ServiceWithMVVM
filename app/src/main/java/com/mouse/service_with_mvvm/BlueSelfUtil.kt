package com.mouse.service_with_mvvm

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlin.random.Random

class BlueSelfUtil(context: Context) : IBlueUtil {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    var bluetoothLeScanner: BluetoothLeScanner? = null
    override val blueDeviceList = flow {
        println("####blueutil.scan")
        locationsSource.collect {
            println("####scan.collect.it=$it")
            emit(it)
        }
        println("####blueutil.scan2")
    }

    init {
        mBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager!!.adapter
        bluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
    }

    @SuppressLint("MissingPermission")
    val locationsSource: Flow<String> = callbackFlow<String> {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                println("onScanResult.result=${result!!.device.name}")
                trySend(result!!.device.name).isSuccess
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                println("onBatchScanResults");
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                println("onScanFailed")
                trySend((1..10).random().toString())
            }
        }
        bluetoothLeScanner!!.startScan(scanCallback)
        awaitClose {

        }
    }
//    @SuppressLint("MissingPermission")
//    override suspend fun scan(): Flow<String> {
//        return flow<String> {
//            println("####blueutil.scan")
//            locationsSource.collect {
//                println("####scan.collect.it=$it")
//                emit(it)
//            }
//            println("####blueutil.scan2")
//        }
//    }

    override suspend fun connect() {
        TODO("Not yet implemented")
    }
}