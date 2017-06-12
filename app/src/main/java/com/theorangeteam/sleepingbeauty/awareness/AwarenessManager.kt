package com.theorangeteam.sleepingbeauty.awareness

import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.GoogleApiClient
import android.media.AudioManager
import com.theorangeteam.sleepingbeauty.android.Preferences
import java.util.*


/**
 * Created by tfbc on 07/06/2017.
 */
class AwarenessManager(val context: Context)
{
    private val audioManager: AudioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var isDeviceHomeAndStill: Boolean = false
        get() = field
        set(value)
        {
            field = value
            checkDeviceSleepingContext()
        }
    var isScreenInactive: Boolean = false
        get() = field
        set(value)
        {
            field = value
            checkDeviceSleepingContext()
        }
    var isAmbientLightDim: Boolean = false
        get() = field
        set(value)
        {
            field = value
            checkDeviceSleepingContext()
        }
    var isDeviceSettled: Boolean = false
        get() = field
        set(value)
        {
            field = value
            checkDeviceSleepingContext()
        }

    private fun checkDeviceSleepingContext()
    {
        Log.i(AwarenessManager::class.java.simpleName, "Device home and still: $isDeviceHomeAndStill")
        Log.i(AwarenessManager::class.java.simpleName, "Device with screen inactive: $isScreenInactive")
        Log.i(AwarenessManager::class.java.simpleName, "Device in a dim light ambient: $isAmbientLightDim")
        //Log.i(AwarenessManager::class.java.simpleName, "Device is settled: $isDeviceSettled")
        Log.i(AwarenessManager::class.java.simpleName, "Is current time a sleeping time: ${isSleepingTime()}")

        val isSleeping = isDeviceHomeAndStill && isScreenInactive && isAmbientLightDim && isSleepingTime()
        if (isSleeping) activateSleepingMode() else deactivateSleepingMode()
    }


    private fun activateSleepingMode()
    {
        Log.i(AwarenessManager::class.java.simpleName, "Activating Sleeping Mode")
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    private fun deactivateSleepingMode()
    {
        Log.i(AwarenessManager::class.java.simpleName, "Deactivating Sleeping Mode")
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    private fun isSleepingTime(): Boolean
    {
        val userSleepingTime = Preferences.getUserSleepingTimeRangeFromPreferences()
        val currentTime = loadCustomCalendar(Calendar.HOUR, 0)
        val fromTime = loadCustomCalendar(userSleepingTime.get(Preferences.startingSleepTimeKey)!!, 0)
        val toTime = loadCustomCalendar(userSleepingTime.get(Preferences.endingSleepTimeKey)!!, 1)

        return currentTime.after(fromTime) && currentTime.before(toTime)
    }

    private fun loadCustomCalendar(hourOfDay: Int, daysForward: Int): Calendar
    {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.add(Calendar.DAY_OF_YEAR, daysForward)
        return calendar
    }

    companion object Factory
    {
        fun getGoogleApiService(@NonNull context: Context): GoogleApiClient
        {
            return GoogleApiClient.Builder(context)
                    .addApi(Awareness.API)
                    .build()
        }
    }
}