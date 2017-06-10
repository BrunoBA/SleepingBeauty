package com.theorangeteam.sleepingbeauty.android.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.KeyEvent

import com.theorangeteam.sleepingbeauty.PermissionControl
import com.theorangeteam.sleepingbeauty.R

/**
 * Created by ThomazFB on 6/10/17.
 */

abstract class PermissionActivity : Activity()
{
    internal var permissionAcceptanceDialog: AlertDialog? = null
    var isPermissionDeniedDialogVisible = false
    var isRequestOnGoing = false

    override fun onResume()
    {
        super.onResume()
        PermissionControl.doPermissionRoutine(this)
    }

    val permissions: Array<String>
        get() = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun onPermissionDenied()
    {
        permissionAcceptanceDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_denied_dialog_title))
                .setMessage(getString(R.string.permission_denied_explanation))
                .setPositiveButton(getString(R.string.retry_text), PermissionControl.retryPermissionAcceptance(this))
                .setCancelable(false)
                .setOnKeyListener(onDialogBackPress())
                .create()
        permissionAcceptanceDialog!!.show()
        isPermissionDeniedDialogVisible = true
        isRequestOnGoing = false
    }

    fun onPermissionBlocked()
    {
        permissionAcceptanceDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_blocked_dialog_title))
                .setMessage(getString(R.string.permission_blocked_explanation))
                .setPositiveButton(getString(R.string.settings_text), callApplicationSettingsView())
                .setCancelable(false)
                .setOnKeyListener(onDialogBackPress())
                .create()
        permissionAcceptanceDialog!!.show()
        isPermissionDeniedDialogVisible = true
        isRequestOnGoing = false
    }

    private fun startWriteSettingsView(): DialogInterface.OnClickListener
    {
        return DialogInterface.OnClickListener { dialog, which ->
            val writeSettingsIntent = Intent("android.settings.action.MANAGE_WRITE_SETTINGS")
            writeSettingsIntent.data = Uri.parse("package:" + packageName)
            startActivity(writeSettingsIntent)
        }
    }

    fun onDialogBackPress(): DialogInterface.OnKeyListener
    {
        return DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                dialog.dismiss()
                isPermissionDeniedDialogVisible = false
                onBackPressed()
            }
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        PermissionControl.permissionCheckup(this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun callApplicationSettingsView(): DialogInterface.OnClickListener
    {
        return DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            isPermissionDeniedDialogVisible = false
            isRequestOnGoing = false
        }
    }

    val isPermissionAcceptanceDialogVisible: Boolean
        get() = permissionAcceptanceDialog == null || !permissionAcceptanceDialog!!.isShowing

    val isAnyPermissionOnGoing: Boolean
        get() = !isPermissionDeniedDialogVisible && !isRequestOnGoing
}
