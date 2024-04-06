package com.example.travel.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.travel.MainActivity

class PermissionUtils(
    private val mainActivity: MainActivity
) {

    val launcher = mainActivity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {map ->
        val msg = if (map.values.all { it }) {
            "Permissions is granted"
        } else {"Permissions is not granted"}
        Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show()

    }

    fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                mainActivity,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!isAllGranted) {
            launcher.launch(REQUEST_PERMISSIONS)
        }

    }

    companion object{
        private var REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

        }.toTypedArray()
    }
}