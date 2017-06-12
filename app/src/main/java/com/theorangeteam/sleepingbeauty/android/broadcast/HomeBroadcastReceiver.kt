package com.theorangeteam.sleepingbeauty.android.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.awareness.fence.FenceState
import com.theorangeteam.sleepingbeauty.events.DeviceUnusedAtHomeEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by guilh on 10/06/2017.
 */
class HomeBroadcastReceiver : BroadcastReceiver()
{
    companion object
    {
        val FENCE_KEY: String = "HOME_FENCE"
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        val fenceState = FenceState.extract(intent)
        Log.d(HomeBroadcastReceiver::class.java.simpleName, "alterado estado fence ${fenceState.currentState}")
        if (TextUtils.equals(fenceState.fenceKey, FENCE_KEY)) when (fenceState.currentState)
        {
            FenceState.TRUE -> EventBus.getDefault().post(DeviceUnusedAtHomeEvent(true))
            FenceState.FALSE -> EventBus.getDefault().post(DeviceUnusedAtHomeEvent(false))
        }
    }
}