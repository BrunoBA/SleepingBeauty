package com.theorangeteam.sleepingbeauty.android.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.awareness.fence.FenceState
import com.theorangeteam.sleepingbeauty.events.DeviceUnusedAtHomeEvent
import com.theorangeteam.sleepingbeauty.events.TiltStateChangedEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by tfbc on 12/06/2017.
 */
class TiltingBroadcastReceiver: BroadcastReceiver()
{

    companion object
    {
        val FENCE_KEY: String = "TILTING_FENCE"
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        val fenceState = FenceState.extract(intent)
        Log.d(HomeBroadcastReceiver::class.java.simpleName, "alterado estado da tilt fence ${fenceState.currentState}")
        if (TextUtils.equals(fenceState.fenceKey, TiltingBroadcastReceiver.FENCE_KEY)) when (fenceState.currentState)
        {
            FenceState.TRUE -> EventBus.getDefault().post(TiltStateChangedEvent(true))
            FenceState.FALSE -> EventBus.getDefault().post(TiltStateChangedEvent(false))
        }
    }
}