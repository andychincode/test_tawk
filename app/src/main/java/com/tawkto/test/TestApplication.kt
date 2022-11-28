package com.tawkto.test

import android.app.Application

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TestApplication
            private set
    }

}