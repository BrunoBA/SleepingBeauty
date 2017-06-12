package com.theorangeteam.sleepingbeauty.android.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.theorangeteam.sleepingbeauty.events.AmbientLightChangedEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by guilh on 11/06/2017.
 */
class LightSensorListener : SensorEventListener
{
    override fun onSensorChanged(event: SensorEvent)
    {
        val luxValues = event.values[0]
        Log.d(LightSensorListener::class.java.simpleName, "Valor iluminação: $luxValues")
        if (luxValues <= 3.0)
        {
            EventBus.getDefault().post(AmbientLightChangedEvent(false))
        }
        else
        {
            EventBus.getDefault().post(AmbientLightChangedEvent(true))
        }
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int)
    {

    }
}