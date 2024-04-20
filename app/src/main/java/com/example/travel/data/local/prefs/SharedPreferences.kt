package com.example.travel.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context?) {
    companion object {
        const val PREFS_NAME = "my_prefs"
    }

    val sharedPref: android.content.SharedPreferences? =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, value: String) {
        sharedPref.let {
            val editor: SharedPreferences.Editor? = sharedPref?.edit()
            editor?.putString(KEY_NAME, value)
            editor?.apply()
        }
    }

    fun saveFloat(KEY_NAME: String, value: Float) {
        sharedPref.let {
            val editor: SharedPreferences.Editor? = sharedPref?.edit()
            editor?.putFloat(KEY_NAME, value)
            editor?.apply()
        }
    }

    fun getStringValue(KEY_NAME: String, def: String? = null): String? = sharedPref?.getString(KEY_NAME, def)
    fun getFloatValue(KEY_NAME: String, def: Float = 0f): Float? = sharedPref?.getFloat(KEY_NAME, def)
}