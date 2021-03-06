package com.mapledev.runtracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.mapledev.runtracker.R
import com.mapledev.runtracker.ui.MainActivity
import com.mapledev.runtracker.utils.Constants.ACTION_PAUSE_SERVICE
import com.mapledev.runtracker.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.mapledev.runtracker.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.mapledev.runtracker.utils.Constants.ACTION_STOP_SERVICE
import com.mapledev.runtracker.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.mapledev.runtracker.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.mapledev.runtracker.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.mapledev.runtracker.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.mapledev.runtracker.utils.Constants.NOTIFICATION_ID
import com.mapledev.runtracker.utils.TrackingUtility
import timber.log.Timber

typealias  Polyline = MutableList<LatLng>
typealias ListOfPolylines = MutableList<Polyline>
class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<ListOfPolylines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.value = mutableListOf()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    //Called when an action is sent to this service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service")
                    }

                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pause service")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            if (isTracking.value!!){
                locationResult?.locations?.let { locations ->
                    for (location in locations){
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                pathPoints.value?.apply {
                    last().add(pos)
                    pathPoints.postValue(this)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermission(this)){
              val locationRequest = LocationRequest().apply {
                  interval = LOCATION_UPDATE_INTERVAL
                  fastestInterval = FASTEST_LOCATION_INTERVAL
                  priority = PRIORITY_HIGH_ACCURACY
              }

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }


        }
    }
    private fun startForegroundService(){
        addEmptyPolyline()
        isTracking.value = true
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )


}