package com.theorangeteam.sleepingbeauty.listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

/**
 * Created by guilh on 11/06/2017.
 */
class LightSensorListener : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
        val millibars_of_pressure = event.values[0]
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {

    }
}