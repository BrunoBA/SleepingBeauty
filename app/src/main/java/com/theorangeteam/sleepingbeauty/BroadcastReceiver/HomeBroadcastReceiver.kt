package com.theorangeteam.sleepingbeauty.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Switch
import com.google.android.gms.awareness.fence.FenceState
import com.theorangeteam.sleepingbeauty.Events.HomeEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by guilh on 10/06/2017.
 */
class HomeBroadcastReceiver : BroadcastReceiver() {
    companion object {
        val FENCE_KEY: String = "HOME_FENCE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val fenceState = FenceState.extract(intent)
        if (TextUtils.equals(fenceState.fenceKey, FENCE_KEY)) when (fenceState.currentState) {
            FenceState.TRUE -> EventBus.getDefault().post(HomeEvent(true))
            FenceState.FALSE -> EventBus.getDefault().post(HomeEvent(true))
        }
    }
}