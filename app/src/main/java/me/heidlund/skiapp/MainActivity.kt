package me.heidlund.skiapp

import android.Manifest

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LiveData
import me.heidlund.skiapp.models.SkiPoint
import me.heidlund.skiapp.service.GpsService
import me.heidlund.skiapp.ui.theme.SkiAppTheme
import me.heidlund.skiapp.uiComponents.LiveView
import me.heidlund.skiapp.uiComponents.WebbView

class MainActivity : ComponentActivity() {
    var TAG = "MainActivity"
    var boundService = mutableStateOf<GpsService.MyBinder?>(null)
    var liveData: MutableState<LiveData<SkiPoint>?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {

            SkiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize().padding(Dp(10f)), bottomBar = {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = { if (boundService.value != null) {
                        stopGpsService()
                    } else {
                        startGpsService()
                    } }) { Text(text = if (boundService.value != null) "Stop Service" else "Start Service")
                    }
                }){ _ ->
                    if (liveData.value == null) {
                        WebbView()
                    }else{
                        val data = liveData.value!!.observeAsState()
                        LiveView(modifier = Modifier.fillMaxSize(1f), skiPoint = data)
                    }



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
            liveData.value = binder.getService().currentData
            Log.d(TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            boundService.value = null
            liveData.value = null
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

