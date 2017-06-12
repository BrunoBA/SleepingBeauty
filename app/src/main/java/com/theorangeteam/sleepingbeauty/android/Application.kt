package com.theorangeteam.sleepingbeauty.android

import android.app.Application

/**
 * Created by tfbc on 12/06/2017.
 */
class Application: Application()
{
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object Instance
    {
        var instance: Application? = null
    }
}