package com.icon.aibrowserasistor.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHelper(private val activity: ComponentActivity) {
    private var launcher: ActivityResultLauncher<String>? = null

    fun register(onGranted: () -> Unit, onDenied: () -> Unit) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onGranted() else onDenied()
        }
    }

    fun ensureRecordAudioPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
        val granted = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onGranted()
        } else {
            launcher?.launch(Manifest.permission.RECORD_AUDIO) ?: onDenied()
        }
    }
}
