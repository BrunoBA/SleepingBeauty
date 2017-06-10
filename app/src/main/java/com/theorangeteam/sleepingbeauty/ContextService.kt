package com.theorangeteam.sleepingbeauty

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by ThomazFB on 6/10/17.
 */
class ContextService : Service
{
    constructor() : super()

    override fun onBind(intent: Intent?): IBinder
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}