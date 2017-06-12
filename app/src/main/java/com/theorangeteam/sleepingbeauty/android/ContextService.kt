package com.theorangeteam.sleepingbeauty.android

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
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
import com.google.android.gms.location.DetectedActivity
import com.theorangeteam.sleepingbeauty.android.broadcast.HomeBroadcastReceiver
import com.theorangeteam.sleepingbeauty.android.broadcast.ScreenReceiver
import com.theorangeteam.sleepingbeauty.awareness.AwarenessManager
import com.theorangeteam.sleepingbeauty.events.AmbientLightChangedEvent
import com.theorangeteam.sleepingbeauty.events.DeviceUnusedAtHomeEvent
import com.theorangeteam.sleepingbeauty.events.ScreenActivationChangedEvent
import com.theorangeteam.sleepingbeauty.android.listener.LightSensorListener
import com.theorangeteam.sleepingbeauty.events.HomeLocationConfiguredEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by ThomazFB on 6/10/17.
 */
class ContextService : Service()
{

    private lateinit var googleApiClient: GoogleApiClient
    private var myPendingIntent: PendingIntent? = null
    private val awarenessManager: AwarenessManager by lazy { AwarenessManager(this) }
    val FENCE_RECEIVE: String = "FENCE_RECEIVE"

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
        initHomeFence()
        initScreenReceiver()
        initLightningSensor()
    }

    private fun initHomeFence()
    {
        initBroadcastReceiver()
        val homeStillFence = buildAwarenessFence()
        val fenceUpdateRequest = FenceUpdateRequest.Builder()
                .addFence(HomeBroadcastReceiver.FENCE_KEY, homeStillFence, myPendingIntent)
                .build()
        Awareness.FenceApi.updateFences(googleApiClient, fenceUpdateRequest)
                .setResultCallback { result ->
                    if (!result.isSuccess)
                    {
                        Log.e(Service::class.java.simpleName, "erro ao inicializar fence")
                    }
                }
    }

    private fun buildAwarenessFence(): AwarenessFence
    {
        val homeFence = loadLocationFence()
        val stillFence = DetectedActivityFence.during(DetectedActivity.STILL)
        //val start = 22L * 60L * 60L * 1000L //ISSO É CONVERSÃO DE HORAS PARA MILISEGUNDOS
        //val end =  6L * 60L * 60L * 1000L
        ///val timeFence = TimeFence.inDailyInterval(TimeZone.getDefault(),end, start)

        val homeStillFence = AwarenessFence.and(homeFence, stillFence)
        return homeStillFence
    }

    @SuppressLint("MissingPermission")
    private fun loadLocationFence(): AwarenessFence?
    {
        val locationValues = Preferences.getHomeLocationFromPreferences(this)
        val latitude = locationValues[Preferences.currentLatitude] as Double
        val longitude = locationValues[Preferences.currentLongitude] as Double
        val homeFence = LocationFence.`in`(latitude, longitude, 100.0, 50)
        return homeFence
    }

    private fun initBroadcastReceiver()
    {
        val intent = Intent(FENCE_RECEIVE)
        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val homeReceiver = HomeBroadcastReceiver()
        registerReceiver(homeReceiver, IntentFilter(FENCE_RECEIVE))
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
        mSensorManager.registerListener(LightSensorListener(), lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    @Subscribe
    fun onHomeFenceCrossedEvent(deviceUnusedAtHomeEvent: DeviceUnusedAtHomeEvent)
    {
        awarenessManager.isDeviceUnusedAtHome = deviceUnusedAtHomeEvent.inHomeArea
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

}