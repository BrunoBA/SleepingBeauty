package com.theorangeteam.sleepingbeauty.android.activity

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.theorangeteam.sleepingbeauty.android.PermissionHandler
import com.theorangeteam.sleepingbeauty.R

/**
 * Created by ThomazFB on 6/10/17.
 */

abstract class PermissionActivity : AppCompatActivity()
{
    internal var permissionAcceptanceDialog: AlertDialog? = null
    var isPermissionDeniedDialogVisible = false
    var isRequestOnGoing = false

    override fun onResume()
    {
        super.onResume()
        PermissionHandler.doPermissionRoutine(this)
    }

    abstract fun permissionList(): Array<String>

    fun onPermissionDenied()
    {
        permissionAcceptanceDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_denied_dialog_title))
                .setMessage(getString(R.string.permission_denied_explanation))
                .setPositiveButton(getString(R.string.retry_text), PermissionHandler.retryPermissionAcceptance(this))
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

    @TargetApi(Build.VERSION_CODES.M)
    fun handleSpecialPermission()
    {
        if (!Settings.System.canWrite(this))
        {
            permissionAcceptanceDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.write_settings_dialog_title))
                    .setMessage(getString(R.string.write_settings_permission_explanation))
                    .setPositiveButton(getString(R.string.settings_text), startWriteSettingsView())
                    .setCancelable(false)
                    .setOnKeyListener(onDialogBackPress())
                    .create()
            permissionAcceptanceDialog!!.show()
        }
    }

    private fun startWriteSettingsView(): DialogInterface.OnClickListener
    {
        return DialogInterface.OnClickListener { dialog, which ->
            val writeSettingsIntent = Intent("android.settings.action.MANAGE_WRITE_SETTINGS")
            writeSettingsIntent.data = Uri.parse("package:" + getPackageName())
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
        PermissionHandler.permissionCheckup(this)
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
