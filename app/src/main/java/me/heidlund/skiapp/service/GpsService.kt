package me.heidlund.skiapp.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import me.heidlund.skiapp.GPS_SERVICE_NOTIFICATION_CHANNEL_ID
import me.heidlund.skiapp.GPS_SERVICE_NOTIFICATION_CHANNEL_ID_STRING
import me.heidlund.skiapp.GPS_SERVICE_NOTIFICATION_CHANNEL_NAME
import me.heidlund.skiapp.R
import java.text.MessageFormat


class GpsService: Service() {
    private val TAG = "GpsService"
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val binder = MyBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MyBinder : Binder() {
        fun stopService() {
            Log.d(TAG, "Binder stopService: ")
            stopGpsTracking()
        }

    }

    fun stopGpsTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "stopGpsTracking: ")
        stopForeground(ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    private fun startForeground() {
        val locationPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermission != PermissionChecker.PERMISSION_GRANTED) {
            // Without camera permissions the service cannot run in the foreground
            //TODO: Consider informing user or updating your app UI if visible.
            Log.e(TAG, "startForeground: NO PERMISSIONs")
            stopSelf()
            return
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationManager.getNotificationChannel(GPS_SERVICE_NOTIFICATION_CHANNEL_ID_STRING) == null){
            Log.d(TAG, "startForeground: ADDING NOTEFICATION CHANNEL")
            val channel = NotificationChannel(GPS_SERVICE_NOTIFICATION_CHANNEL_ID_STRING,
                GPS_SERVICE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
                description = "Channel description"
            }
            notificationManager.createNotificationChannel(channel)
        }


        try {
            val notification =
                NotificationCompat.Builder(this, GPS_SERVICE_NOTIFICATION_CHANNEL_ID_STRING)
                    .setContentTitle("GPS Service Running")
                    .setContentText("Tracking your location")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()
            ServiceCompat.startForeground(
                this,
                GPS_SERVICE_NOTIFICATION_CHANNEL_ID, // Cannot be 0
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e(TAG, "startForeground: COUD NOT START GPS SERVICE", e)
                stopSelf()
                // TODO:App not in a valid state to start foreground service
                // (e.g. started from bg)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        if (intent?.action == "STOP_SERVICE") {
            stopSelf()
        }
        startForeground()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, this.mainLooper)



        return START_NOT_STICKY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult != null) {
                if (locationResult == null) {
                    return
                }
                //Showing the latitude, longitude and accuracy on the home screen.
                for (location in locationResult.locations) {
                    Log.i(TAG, "onLocationResult: "+MessageFormat.format(
                        "Lat: {0} Long: {1} Accuracy: {2}", location.latitude,
                        location.longitude, location.accuracy
                    ))
                }
            }
        }
    };

    override fun startForegroundService(service: Intent?): ComponentName? {
        Log.d(TAG, "startForegroundService: ")
        return super.startForegroundService(service)
    }

    override fun stopService(name: Intent?): Boolean {
        Log.d(TAG, "stopService: ")
        return super.stopService(name)
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate()
    }



}