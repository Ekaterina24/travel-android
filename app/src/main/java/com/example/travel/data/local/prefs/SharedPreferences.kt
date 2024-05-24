package com.example.travel.data.local.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SharedPreferences(context: Context?) {

    private val preference = context?.getSharedPreferences(
        context.packageName,
        MODE_PRIVATE
    )

    private val editor = preference?.edit()
    private val keyTheme = "theme"

    var theme
        get() = preference?.getInt(keyTheme, 2)
        set(value) {
            if (value != null) {
                editor?.putInt(keyTheme, value)
            }
            editor?.commit()
        }

    val themeFlags = arrayOf(
        AppCompatDelegate.MODE_NIGHT_NO,
        AppCompatDelegate.MODE_NIGHT_YES,
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

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

    fun removeStringValue(KEY_NAME: String) {
        sharedPref.let {
            val editor: SharedPreferences.Editor? = sharedPref?.edit()
            editor?.remove(KEY_NAME)
            editor?.apply()
        }
    }

    fun saveBoolean(KEY_NAME: String, value: Boolean) {
        sharedPref?.let {
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean(KEY_NAME, value)
            editor.apply()
        }
    }

    fun getBooleanValue(KEY_NAME: String, defValue: Boolean = false): Boolean? {
        sharedPref.let { return sharedPref?.getBoolean(KEY_NAME, defValue) }
    }
}