package com.example.travel.presentation.utils

import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.travel.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//fun Fragment.openFragment(f: Fragment) {
//    (activity as AppCompatActivity).supportFragmentManager
//        .beginTransaction()
//        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//        .replace(R.id.placeHolder, f)
//        .commit()
//}

//fun AppCompatActivity.openFragment(f: Fragment) {
//    if (supportFragmentManager.fragments.isNotEmpty()) {
//        if (supportFragmentManager.fragments[0].javaClass == f.javaClass) return
//    }
//    supportFragmentManager
//        .beginTransaction()
//        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//        .replace(R.id.placeHolder, f)
//        .commit()
//}

fun Fragment.showToast(s: String) {
    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.checkPermissionGranted(permission: String): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            activity as AppCompatActivity,
            permission
        ) -> true
        else -> false
    }
}

fun Fragment.addPermissionToRequestedList(
    stateOfPermission: Boolean, permission: String,
    list: ArrayList<String>
) {
    if (!stateOfPermission)
        list.add(permission)
}

fun EditText.textChangedFlow(): Flow<String> {
    return callbackFlow {
        var isInitialized = false
        val textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isInitialized) {
                    isInitialized = true
                    return
                }
                trySendBlocking(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        this@textChangedFlow.addTextChangedListener(textChangedListener)
        awaitClose {
            this@textChangedFlow.removeTextChangedListener(textChangedListener)
        }
    }
}