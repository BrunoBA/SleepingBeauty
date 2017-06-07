package com.theorangeteam.sleepingbeauty.awareness

import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by tfbc on 07/06/2017.
 */
interface AwarenessService {

    companion object Factory
    {
        private lateinit var googleApiClient: GoogleApiClient

        fun getGoogleApiService() : GoogleApiClient
        {
            return googleApiClient
        }
    }
}