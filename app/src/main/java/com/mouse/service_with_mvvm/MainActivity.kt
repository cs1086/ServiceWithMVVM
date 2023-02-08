package com.mouse.service_with_mvvm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mouse.service_with_mvvm.ui.theme.ServiceWithMVVMTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by lazy {
        MainViewModel()
    }

    //設置要訪問的權限的操作
    private val multiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            it.entries.forEach {
                when (it.key) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        Log.d("GG", "ACCESS_FINE_LOCATION ${it.value}")
                    }
                    Manifest.permission.BLUETOOTH_CONNECT -> {
                        Log.d("GG", "BLUETOOTH_CONNECT ${it.value}")
                    }
                    Manifest.permission.BLUETOOTH_SCAN -> {
                        Log.d("GG", "BLUETOOTH_SCAN ${it.value}")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, BlueService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, BlueService::class.java))
        } else {
            startService(Intent(this, BlueService::class.java))
        }
        bindService(intent, viewModel.serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            ServiceWithMVVMTheme {
                // A surface container using the 'background' color from the theme
                val isServiceCompleted by viewModel.isServiceCompleted.observeAsState(false)
                if (isServiceCompleted) {
                    println("####setContent.bind=$isServiceCompleted")
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Greeting(viewModel)
                    }
                }
            }
        }

    }
}

@Composable
fun Greeting(viewModel: MainViewModel) {
    val deviceList by viewModel.blueDeviceList.collectAsState()
    Column {
        Button(onClick = {
            println("####onClick")
            MainScope().launch {
                viewModel.scan()
            }
        }) {
            Text("掃描", fontSize = 40.sp)
        }
        LazyColumn {
            items(deviceList.size) { index ->
                println("####mainactivity")
                Text(
                    deviceList[index],
                    fontSize = 40.sp,
                    color = Color.Black
                )
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ServiceWithMVVMTheme {
        //Greeting("Android")
    }
}