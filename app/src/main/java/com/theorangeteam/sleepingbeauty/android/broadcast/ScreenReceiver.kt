package com.theorangeteam.sleepingbeauty.android.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theorangeteam.sleepingbeauty.events.ScreenActivationChangedEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by guilh on 11/06/2017.
 */
class ScreenReceiver : android.content.BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        if (intent.action == Intent.ACTION_SCREEN_OFF)
        {
            EventBus.getDefault().post(ScreenActivationChangedEvent(false))
        }
        else if (intent.action == Intent.ACTION_SCREEN_ON)
        {
            EventBus.getDefault().post(ScreenActivationChangedEvent(true))
        }
    }
}