package com.theorangeteam.sleepingbeauty.awareness

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.AwarenessFence
import com.google.android.gms.awareness.fence.DetectedActivityFence
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.DetectedActivity
import com.theorangeteam.sleepingbeauty.android.Preferences
import com.theorangeteam.sleepingbeauty.android.broadcast.HomeBroadcastReceiver
import com.theorangeteam.sleepingbeauty.android.broadcast.ScreenReceiver
import com.theorangeteam.sleepingbeauty.android.broadcast.LightSensorReceiver
import com.theorangeteam.sleepingbeauty.android.broadcast.TiltingBroadcastReceiver
import com.theorangeteam.sleepingbeauty.events.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by ThomazFB on 6/10/17.
 */
class AwarenessService : Service()
{

    private lateinit var googleApiClient: GoogleApiClient
    private var homeBroadcastIntent: PendingIntent? = null
    private var tiltBroadcastIntent: PendingIntent? = null
    private val awarenessManager: AwarenessManager by lazy { AwarenessManager(this) }
    val HOME_FENCE_RECEIVE: String = "HOME_FENCE_RECEIVE"
    val TILT_FENCE_RECEIVE: String = "TILT_FENCE_RECEIVE"

    override fun onBind(intent: Intent?): IBinder
    {
        TODO("not implemented")
    }

    override fun onCreate()
    {
        super.onCreate()
        Log.i(Service::class.java.simpleName, "Service created")
        googleApiClient = AwarenessManager.getGoogleApiService(this)
        googleApiClient.connect()
        EventBus.getDefault().register(this)
        initContextSensors()
    }

    override fun onDestroy()
    {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
    fun onHomeLocationConfigured(event: HomeLocationConfiguredEvent)
    {
        initContextSensors()
    }

    private fun initContextSensors()
    {
        if(Preferences.thereIsAHomeLocationConfigured()) {
            initHomeFence()
            initScreenReceiver()
            initLightningSensor()
        }
    }

    private fun initHomeFence()
    {
        homeBroadcastIntent = initBroadcastReceiver(HOME_FENCE_RECEIVE, HomeBroadcastReceiver(), 0)
        tiltBroadcastIntent = initBroadcastReceiver(TILT_FENCE_RECEIVE, TiltingBroadcastReceiver(), 1)
        val homeStillFence = buildAwarenessFence()
        val fenceUpdateRequest = buildFenceRequest(homeStillFence)
        loadFence(fenceUpdateRequest)
    }

    private fun buildFenceRequest(homeStillFence: AwarenessFence): FenceUpdateRequest? {
        return FenceUpdateRequest.Builder()
                .addFence(HomeBroadcastReceiver.FENCE_KEY, homeStillFence, homeBroadcastIntent)
                .addFence(TiltingBroadcastReceiver.FENCE_KEY, DetectedActivityFence.during(DetectedActivity.TILTING), tiltBroadcastIntent)
                .build()
    }

    private fun loadFence(fenceUpdateRequest: FenceUpdateRequest?) {
        Awareness.FenceApi.updateFences(googleApiClient, fenceUpdateRequest)
                .setResultCallback { result -> onFenceLoadingResult(result) }
    }

    private fun onFenceLoadingResult(result: Status) {
        if (!result.isSuccess) {
            Log.e(Service::class.java.simpleName, "erro ao inicializar fence")
        }
    }

    private fun buildAwarenessFence(): AwarenessFence
    {
        val homeFence = loadLocationFence()
        val stillFence = DetectedActivityFence.during(DetectedActivity.STILL)
        val homeStillFence = AwarenessFence.and(homeFence, stillFence)
        return homeStillFence
    }

    @SuppressLint("MissingPermission")
    private fun loadLocationFence(): AwarenessFence?
    {
        val locationValues = Preferences.getHomeLocationFromPreferences()
        val latitude = locationValues[Preferences.currentLatitudeKey] as Double
        val longitude = locationValues[Preferences.currentLongitudeKey] as Double
        val homeFence = LocationFence.`in`(latitude, longitude, 100.0, 50)
        return homeFence
    }

    private fun initBroadcastReceiver(intentAction: String, broadcastReceiver: BroadcastReceiver, requestCode: Int): PendingIntent
    {
        val intent = Intent(intentAction)
        val broadcastIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0)
        registerReceiver(broadcastReceiver, IntentFilter(intentAction))
        return broadcastIntent
    }

    private fun initScreenReceiver()
    {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        val mReceiver = ScreenReceiver()
        registerReceiver(mReceiver, filter)
    }

    private fun initLightningSensor()
    {
        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mSensorManager.registerListener(LightSensorReceiver(), lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    @Subscribe
    fun onHomeFenceCrossedEvent(deviceUnusedAtHomeEvent: DeviceUnusedAtHomeEvent)
    {
        awarenessManager.isDeviceHomeAndStill = deviceUnusedAtHomeEvent.inHomeArea
    }

    @Subscribe
    fun onScreenActivationChangedEvent(screenActivationChangedEvent: ScreenActivationChangedEvent)
    {
        awarenessManager.isScreenInactive = !screenActivationChangedEvent.isScreenTurnedOn
    }

    @Subscribe
    fun onLightEvent(ambientLightChangedEvent: AmbientLightChangedEvent)
    {
        awarenessManager.isAmbientLightDim = !ambientLightChangedEvent.isLightBright
    }

    @Subscribe
    fun onTiltEvent(tiltStateChangedEvent: TiltStateChangedEvent)
    {
        awarenessManager.isDeviceSettled = !tiltStateChangedEvent.isTilting
    }

}