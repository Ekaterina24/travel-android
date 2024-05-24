package com.example.travel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.local.prefs.SharedPreferences

class App : Application() {

//    @Inject
//    lateinit var applicationComponent: ApplicationComponent

    lateinit var db: TravelDatabase
        private set



    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        db = TravelDatabase.getInstance(this)

        val sharedPreferences = SharedPreferences(this)
        AppCompatDelegate.setDefaultNightMode(sharedPreferences.themeFlags[sharedPreferences.theme!!])



    }

    // static Java
    companion object {
        // для получения инстанс апп извне (установка только внутри в методе onCreate)
        lateinit var INSTANCE: App
            private set
    }

}