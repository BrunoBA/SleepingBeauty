package com.theorangeteam.sleepingbeauty.android.component

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import butterknife.bindView
import com.theorangeteam.sleepingbeauty.R
import com.theorangeteam.sleepingbeauty.android.Preferences

/**
 * Created by ThomazFB on 6/10/17.
 */

class SettingsDialog : DialogFragment()
{
    val currentLocationAsHomeButton: Button by bindView(R.id.set_current_location_as_home_button)
    val editStartingSleepTimeButton: Button by bindView(R.id.edit_starting_time_button)
    val editEndingSleepTimeButton: Button by bindView(R.id.edit_ending_time_button)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater?.inflate(R.layout.component_settings_dialog, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        currentLocationAsHomeButton.setOnClickListener { view -> onCurrentLocationAsHomeClick() }
        editStartingSleepTimeButton.setOnClickListener { view -> onEditStartingSleepTimeClick() }
        editEndingSleepTimeButton.setOnClickListener { view -> onEditEndingSleepTimeClick() }
    }

    override fun onResume()
    {
        super.onResume()
        resizeDialog()
    }

    private fun resizeDialog()
    {
        val dialog = dialog
        if (dialog != null)
        {
            val window = dialog.window
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        resizeDialog()
    }

    private fun onCurrentLocationAsHomeClick()
    {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val currentLocation = locationManager.loadCurrentLocation()
        Preferences.saveHomeLocationIntoPreferences(currentLocation)
    }

    private fun onEditStartingSleepTimeClick()
    {
        TimePickerDialog(context, { timePicker, hour, minute ->  Preferences.saveStartingSleepTimeIntoPreferences(hour)},
                Preferences.getUserSleepingTimeRangeFromPreferences().get(Preferences.startingSleepTimeKey)!!, 0, true).show()
        
    }

    private fun onEditEndingSleepTimeClick()
    {
        TimePickerDialog(context, { timePicker, hour, minute ->  Preferences.saveEndingSleepTimeIntoPreferences(hour)},
                Preferences.getUserSleepingTimeRangeFromPreferences().get(Preferences.endingSleepTimeKey)!!, 0, true).show()
    }

    @SuppressLint("MissingPermission")
    fun LocationManager.loadCurrentLocation(): Location
    {
        val providers = getProviders(true)
        return getLastKnownLocation(providers[0])
    }
}
