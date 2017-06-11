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
import android.content.IntentFilter
import android.app.PendingIntent
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.google.android.gms.awareness.fence.AwarenessFence
import com.google.android.gms.awareness.fence.DetectedActivityFence
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import com.google.android.gms.location.DetectedActivity
import com.theorangeteam.sleepingbeauty.BroadcastReceiver.HomeBroadcastReceiver
import com.theorangeteam.sleepingbeauty.BroadcastReceiver.ScreenReceiver
import com.theorangeteam.sleepingbeauty.android.Preferences
import com.theorangeteam.sleepingbeauty.events.HomeEvent
import com.theorangeteam.sleepingbeauty.events.ScreenEvent
import com.theorangeteam.sleepingbeauty.listeners.LightSensorListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by ThomazFB on 6/10/17.
 */
class ContextService : Service() {

    private lateinit var googleApiClient: GoogleApiClient
    private var myPendingIntent: PendingIntent? = null
    val FENCE_RECEIVE: String = "FENCE_RECEIVE"

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(Service::class.java.simpleName, "Service created")
        googleApiClient = AwarenessService.getGoogleApiService(this)
        googleApiClient.connect()
        initHomeFence()
        initScreenReceiver()
        initLightningSensor()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun initHomeFence() {
        initBroadcastReceiver()
        val homeStillFence = buildAwarenessFence()
        val fenceUpdateRequest = FenceUpdateRequest.Builder()
                .addFence(HomeBroadcastReceiver.FENCE_KEY, homeStillFence, myPendingIntent)
                .build()
        Awareness.FenceApi.updateFences(googleApiClient, fenceUpdateRequest)
                .setResultCallback { result ->
                    if (!result.isSuccess) {
                        Log.e(Service::class.java.simpleName, "erro ao inicializar fence")
                    }
                }
    }

    @SuppressLint("MissingPermission")
    private fun buildAwarenessFence(): AwarenessFence {
        val locationValues = Preferences.getLocationFromPreferences(this)
        val latitude = locationValues[Preferences.currentLatitude] as Double
        val longitude = locationValues[Preferences.currentLongitude] as Double
        val homeFence = LocationFence.`in`(latitude, longitude, 500.8, 50)

        val stillFence = DetectedActivityFence.during(DetectedActivity.STILL)
        val homeStillFence = AwarenessFence.and(homeFence, stillFence)
        return homeStillFence
    }

    private fun initBroadcastReceiver() {
        val intent = Intent(FENCE_RECEIVE)
        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val homeReceiver = HomeBroadcastReceiver()
        registerReceiver(homeReceiver, IntentFilter(FENCE_RECEIVE))
    }

    private fun initScreenReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        val mReceiver = ScreenReceiver()
        registerReceiver(mReceiver, filter)
    }

    private fun initLightningSensor() {
        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mSensorManager.registerListener(LightSensorListener(), lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        //TODO: falta configurar os eventos do sensor saber quanto caralhas de lux significa estar escuro
    }

    @Subscribe
    private fun onHomeEvent(homeEvent: HomeEvent) {
        if (homeEvent.inHomeArea) {
            //TODO: ESTÁ EM CASA
        } else {
            //TODO: TA EM CASA NÃO
        }
    }

    @Subscribe
    private fun onScreenEvent(screenEvent: ScreenEvent) {
        if (screenEvent.isScreenTurnedOn) {
            //TODO: TELA LIGADA
        } else {
            //TODO: TELA DESLIGADA
        }
    }

    //  region snaphshot API
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
        if (weatherResult.status.isSuccess) {
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
    //endregion

}