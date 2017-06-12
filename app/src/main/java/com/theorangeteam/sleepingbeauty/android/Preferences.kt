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
    private val sharedPreferences: SharedPreferences by lazy { loadSharedPreferences(context) }
    private lateinit var context: Context
    val currentLatitude = "CURRENT_LATITUDE"
    val currentLongitude = "CURRENT_LONGITUDE"

    fun saveHomeLocationIntoPreferences(context: Context, location: Location)
    {
        this.context = context
        val editor = sharedPreferences.edit()
        editor.putFloat(currentLatitude, location.latitude.toFloat())
        editor.putFloat(currentLongitude, location.longitude.toFloat())
        editor.putBoolean(customHomeLocationInserted, true)
        editor.apply()
        EventBus.getDefault().post(HomeLocationConfiguredEvent())
    }

    fun getHomeLocationFromPreferences(context: Context): Map<String, Double>
    {
        this.context = context
        val locationValues = HashMap<String, Double>()
        locationValues.put(currentLatitude, sharedPreferences.getFloat(currentLatitude, 0f).toDouble())
        locationValues.put(currentLongitude, sharedPreferences.getFloat(currentLongitude, 0f).toDouble())
        return locationValues
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