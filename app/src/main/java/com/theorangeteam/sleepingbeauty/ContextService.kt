package com.theorangeteam.sleepingbeauty

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.*
import com.google.android.gms.common.api.GoogleApiClient
import com.theorangeteam.sleepingbeauty.awareness.AwarenessService
import com.google.android.gms.awareness.state.HeadphoneState
import com.google.android.gms.awareness.state.Weather




/**
 * Created by ThomazFB on 6/10/17.
 */
class ContextService : Service {
    constructor() : super()

    private lateinit var googleApiClient: GoogleApiClient

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        Log.i(Service::class.java.simpleName, "Service created")
        super.onCreate()
        googleApiClient = AwarenessService.getGoogleApiService(this)
        googleApiClient.connect()
        initSnapshots()
    }

    @SuppressLint("MissingPermission")
    private fun initSnapshots() {
        Awareness.SnapshotApi.getHeadphoneState(googleApiClient)
                .setResultCallback { result -> onHeadphoneStateResult(result) }
        Awareness.SnapshotApi.getLocation(googleApiClient)
                .setResultCallback { result -> onLocationResult(result) }
        Awareness.SnapshotApi.getPlaces(googleApiClient)
                .setResultCallback { result -> onPlacesResult(result) }
        Awareness.SnapshotApi.getDetectedActivity(googleApiClient)
                .setResultCallback { result -> onActivityResult(result) }
        Awareness.SnapshotApi.getWeather(googleApiClient)
                .setResultCallback { result -> onWeatherResult(result) }
    }

    private fun onWeatherResult(weatherResult: WeatherResult) {
        if (weatherResult.status.isSuccess){
            val weather = weatherResult.weather
        }
    }

    fun onHeadphoneStateResult(@NonNull headphoneStateResult: HeadphoneStateResult) {
        if (headphoneStateResult.status.isSuccess) {
            val headphoneState = headphoneStateResult.headphoneState
            if (headphoneState.state == HeadphoneState.PLUGGED_IN) {

                Log.i(Service::class.java.simpleName, "Headphone plugged")
                //TODO: TRATAR EVENTO AQUI
            } else {
                //TODO: TRATAR EVENTO AQUI
            }
        }
    }

    fun onLocationResult(@NonNull locationResult: LocationResult) {
        if (locationResult.status.isSuccess) {
            val location = locationResult.location
            location.latitude
            location.longitude
            Log.i(Service::class.java.simpleName, "Location acquired: Latitude ${location.latitude} and Longitude ${location.longitude}")
            //TODO: TRATAR EVENTO AQUI
        }
    }

    fun onPlacesResult(placesResult: PlacesResult) {
        if (placesResult.status.isSuccess) {
            val places = placesResult.placeLikelihoods
            if (places != null) {
                Log.i(Service::class.java.simpleName, "Place acquired: Latitude")
                //TODO: TRATAR EVENTO AQUI
            }
        }
    }

    private fun onActivityResult(activityResult: DetectedActivityResult) {
        if (activityResult.status.isSuccess) {
            val ar = activityResult.activityRecognitionResult
            val probableActivity = ar.mostProbableActivity
            Log.i(Service::class.java.simpleName, "Activity acquired: ${probableActivity.type}")
            //TODO: TRATAR EVENTO AQUI
        }
    }
}