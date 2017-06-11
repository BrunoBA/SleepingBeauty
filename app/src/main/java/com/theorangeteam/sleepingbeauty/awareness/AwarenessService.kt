package com.theorangeteam.sleepingbeauty.awareness

import android.content.Context
import android.support.annotation.NonNull
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by tfbc on 07/06/2017.
 */
interface AwarenessService {

    companion object Factory
    {
        fun getGoogleApiService(@NonNull context : Context) : GoogleApiClient
        {
            return GoogleApiClient.Builder(context)
                    .addApi(Awareness.API)
                    .build()
        }
    }
}