package me.heidlund.skiapp

import android.Manifest
import android.app.ActivityManager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import me.heidlund.skiapp.service.GpsService
import me.heidlund.skiapp.ui.theme.SkiAppTheme

class MainActivity : ComponentActivity() {
    var TAG = "MainActivity"
    var boundService = mutableStateOf<GpsService.MyBinder?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            SkiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    Button(onClick = { if (boundService.value != null) {
                        stopGpsService()
                    } else {
                        startGpsService()
                    } }) { Text(text = if (boundService.value != null) "Stop Service" else "Start Service")
                    }
                }) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

            }
        }
    }
    }



    override fun onResume() {
        super.onResume()
        if(boundService.value == null){
            val isBound = bindService(Intent(this, GpsService::class.java), serviceConnection, Context.BIND_NOT_FOREGROUND)
            if(!isBound){
                boundService.value = null
            }
        }

    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as GpsService.MyBinder
            boundService.value = binder
            Log.d(TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            boundService.value = null
            Log.d(TAG, "onServiceDisconnected: ")
        }
    }

    private fun startGpsService() {

        if(verifyPermissions()){
            val intent = Intent(this, GpsService::class.java)
            val componentName = this.startForegroundService(intent)
            bindService(intent,serviceConnection, Context.BIND_NOT_FOREGROUND)

        }else{
            requestPermissions()
        }
    }
    private fun stopGpsService() {
        Log.d(TAG, "stopGpsService: ")
        if(boundService.value != null){
             boundService.value!!.stopService()
            boundService.value = null;
        }

    }




    private fun verifyPermissions(): Boolean {
        val locationPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return (locationPermission == PermissionChecker.PERMISSION_GRANTED)
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            GPS_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GPS_PERMISSION_REQUEST_CODE) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this,"The Application requires the gps permission to run", Toast.LENGTH_LONG).show()
            }else{
                startGpsService()
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SkiAppTheme {
        Greeting("Android")
    }
}