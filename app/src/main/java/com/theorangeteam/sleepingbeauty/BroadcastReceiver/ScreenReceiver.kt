package com.theorangeteam.sleepingbeauty.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theorangeteam.sleepingbeauty.Events.ScreenEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by guilh on 11/06/2017.
 */
class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            EventBus.getDefault().post(ScreenEvent(false))
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            EventBus.getDefault().post(ScreenEvent(true))
        }
    }
}