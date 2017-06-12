package com.theorangeteam.sleepingbeauty.android

import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import com.theorangeteam.sleepingbeauty.android.activity.PermissionActivity

/**
 * Created by ThomazFB on 6/10/17.
 */

object PermissionControl
{
    private val REQUEST_CODE = 0x1

    fun allPermissionsAreGranted(permissionActivity: PermissionActivity): Boolean
    {
        var hasPermission = true

        for (permission in permissionActivity.permissionList())
        {
            if (ContextCompat.checkSelfPermission(permissionActivity,
                    permission) != PackageManager.PERMISSION_GRANTED)
            {
                hasPermission = false
                break
            }
        }
        return hasPermission
    }

    fun doPermissionRoutine(activity: PermissionActivity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.isAnyPermissionOnGoing)
        {
            for (permission in activity.permissionList())
            {
                if (PermissionControl.isPermissionUnauthorized(activity, permission) && !activity.shouldShowRequestPermissionRationale(permission))
                {

                    activity.requestPermissions(activity.permissionList(), PermissionControl.REQUEST_CODE)
                    activity.isRequestOnGoing = true
                    break
                }
            }
            if (!activity.isRequestOnGoing)
            {
                PermissionControl.permissionCheckup(activity)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun isPermissionUnauthorized(activity: Activity, permission: String): Boolean
    {
        return activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
    }

    fun permissionCheckup(activity: PermissionActivity)
    {
        if (!activity.isPermissionDeniedDialogVisible && activity.isPermissionAcceptanceDialogVisible)
        {
            if (PermissionControl.isNeverAskAgainEnabled(activity))
            {
                activity.onPermissionBlocked()
            }
            else if (PermissionControl.isAnyPermissionStillUnauthorized(activity))
            {
                activity.onPermissionDenied()
            }
            else
            {
                activity.handleSpecialPermission()
            }
            activity.isRequestOnGoing = false
        }
    }

    private fun isNeverAskAgainEnabled(activity: PermissionActivity): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            for (permission in activity.permissionList())
            {
                if (!activity.shouldShowRequestPermissionRationale(permission) && PermissionControl.isPermissionUnauthorized(activity, permission))
                {
                    return true
                }
            }
        }
        return false
    }

    private fun isAnyPermissionStillUnauthorized(activity: PermissionActivity): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            for (permission in activity.permissionList())
            {
                if (PermissionControl.isPermissionUnauthorized(activity, permission))
                {
                    return true
                }
            }
        }
        return false
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.M)
    fun retryPermissionAcceptance(activity: PermissionActivity): DialogInterface.OnClickListener
    {
        return android.content.DialogInterface.OnClickListener { dialog, which ->
            activity.requestPermissions(activity.permissionList(), PermissionControl.REQUEST_CODE)
            activity.isPermissionDeniedDialogVisible = false
            activity.isRequestOnGoing = false
        }
    }
}
