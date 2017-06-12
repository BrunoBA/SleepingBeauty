package com.theorangeteam.sleepingbeauty.android

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.theorangeteam.sleepingbeauty.events.HomeLocationConfiguredEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by ThomazFB on 6/10/17.
 */
object Preferences
{
    private val sharedPreferencesKey = "SLEEPING_BEAUTY_SHARED_PREFERENCES"
    private val customHomeLocationInserted = "CUSTOM_HOME_LOCATION_INSERTED"
    private val sharedPreferences: SharedPreferences by lazy { loadSharedPreferences(Application.instance!!.applicationContext) }
    private val defaultStartingSleepTime = 22
    private val defaultEndingSleepTime = 7
    val currentLatitudeKey = "CURRENT_LATITUDE"
    val currentLongitudeKey = "CURRENT_LONGITUDE"
    val startingSleepTimeKey = "STARTING_SLEEP_TIME"
    val endingSleepTimeKey = "ENDING_SLEEP_TIME"

    fun saveHomeLocationIntoPreferences(location: Location)
    {
        val editor = sharedPreferences.edit()
        editor.putFloat(currentLatitudeKey, location.latitude.toFloat())
        editor.putFloat(currentLongitudeKey, location.longitude.toFloat())
        editor.putBoolean(customHomeLocationInserted, true)
        editor.apply()
        EventBus.getDefault().post(HomeLocationConfiguredEvent())
    }

    fun saveStartingSleepTimeIntoPreferences(startingSleepTime: Int)
    {
        val editor = sharedPreferences.edit()
        editor.putInt(startingSleepTimeKey, startingSleepTime)
        editor.apply()
    }

    fun saveEndingSleepTimeIntoPreferences(endingSleepTime: Int)
    {
        val editor = sharedPreferences.edit()
        editor.putInt(endingSleepTimeKey, endingSleepTime)
        editor.apply()
    }

    fun getHomeLocationFromPreferences(): Map<String, Double>
    {
        val locationValues = HashMap<String, Double>()
        locationValues.put(currentLatitudeKey, sharedPreferences.getFloat(currentLatitudeKey, 0f).toDouble())
        locationValues.put(currentLongitudeKey, sharedPreferences.getFloat(currentLongitudeKey, 0f).toDouble())
        return locationValues
    }

    fun getUserSleepingTimeRangeFromPreferences(): Map<String, Int>
    {
        val sleepingTimeValues = HashMap<String, Int>()
        sleepingTimeValues.put(startingSleepTimeKey, sharedPreferences.getInt(startingSleepTimeKey, defaultStartingSleepTime))
        sleepingTimeValues.put(endingSleepTimeKey, sharedPreferences.getInt(endingSleepTimeKey, defaultEndingSleepTime))
        return sleepingTimeValues
    }


    fun thereIsAHomeLocationConfigured(): Boolean
    {
        return sharedPreferences.getBoolean(customHomeLocationInserted, false)
    }

    private fun loadSharedPreferences(context: Context): SharedPreferences
    {
        return context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
    }
}