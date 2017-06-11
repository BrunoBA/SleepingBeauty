package com.theorangeteam.sleepingbeauty.android

import android.content.Context
import android.content.SharedPreferences
import android.location.Location

/**
 * Created by ThomazFB on 6/10/17.
 */
object Preferences
{
    private val sharedPreferencesKey = "SLEEPING_BEAUTY_SHARED_PREFERENCES"
    private val sharedPreferences: SharedPreferences by lazy { loadSharedPreferences(context) }
    private lateinit var context: Context
    val currentLatitude = "CURRENT_LATITUDE"
    val currentLongitude = "CURRENT_LONGITUDE"

    fun saveLocationIntoPreferences(context: Context, location: Location)
    {
        this.context = context
        val editor = sharedPreferences.edit()
        editor.putFloat(currentLatitude, location.latitude.toFloat())
        editor.putFloat(currentLongitude, location.longitude.toFloat())
        editor.apply()
    }

    fun getLocationFromPreferences(context: Context): Map<String, Double>
    {
        this.context = context
        val locationValues = HashMap<String, Double>()
        locationValues.put(currentLatitude, sharedPreferences.getFloat(currentLatitude, 0f).toDouble())
        locationValues.put(currentLongitude, sharedPreferences.getFloat(currentLongitude, 0f).toDouble())
        return locationValues
    }

    private fun loadSharedPreferences(context: Context): SharedPreferences
    {
        return context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
    }
}