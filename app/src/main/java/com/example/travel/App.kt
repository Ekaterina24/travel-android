package com.example.travel

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase

class App : Application() {

//    @Inject
//    lateinit var applicationComponent: ApplicationComponent

    lateinit var db: TravelDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        db = TravelDatabase.getInstance(this)



    }

    // static Java
    companion object {
        // для получения инстанс апп извне (установка только внутри в методе onCreate)
        lateinit var INSTANCE: App
            private set
    }

}