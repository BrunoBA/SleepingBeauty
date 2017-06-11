package com.theorangeteam.sleepingbeauty.android.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.theorangeteam.sleepingbeauty.ContextService
import com.theorangeteam.sleepingbeauty.PermissionControl
import com.theorangeteam.sleepingbeauty.R
import com.theorangeteam.sleepingbeauty.android.component.SettingsDialog

class HomeActivity : PermissionActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onResume()
    {
        super.onResume()
        startContextService()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        val action = item?.itemId
        when(action)
        {
            R.id.action_settings -> return onSettingsClick()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun permissionList(): Array<String> = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    //:lennyface
    private fun startContextService()
    {
        if (PermissionControl.allPermissionsAreGranted(this))
        {
            val serviceIntent = Intent(this, ContextService::class.java)
            startService(serviceIntent)
        }
    }

    private fun onSettingsClick(): Boolean
    {
        SettingsDialog().show(supportFragmentManager, "settingsDialog")
        return true
    }
}

