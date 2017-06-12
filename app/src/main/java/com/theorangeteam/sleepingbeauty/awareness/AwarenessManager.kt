package com.theorangeteam.sleepingbeauty.awareness

import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.GoogleApiClient
import android.media.AudioManager



/**
 * Created by tfbc on 07/06/2017.
 */
class AwarenessManager(val context: Context)
{
    private val audioManager: AudioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var isDeviceUnusedAtHome: Boolean = false
        set(value)
        {
            isDeviceUnusedAtHome = value
            checkDeviceSleepingContext()
        }
    var isScreenInactive: Boolean = false
        set(value)
        {
            isScreenInactive = value
            checkDeviceSleepingContext()
        }
    var isAmbientLightDim: Boolean = false
        set(value)
        {
            isAmbientLightDim = value
            checkDeviceSleepingContext()
        }

    private fun checkDeviceSleepingContext()
    {
        val isSleeping = isDeviceUnusedAtHome && isScreenInactive && isAmbientLightDim
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