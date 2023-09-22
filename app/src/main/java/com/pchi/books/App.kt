package com.pchi.books

import android.app.Application
import android.os.Handler
import android.os.Looper

class App : Application() {
    companion object {
        private lateinit var instance: App

        fun shared() = instance
    }

    val appHandler = Handler(Looper.getMainLooper())

    val appSP by lazy { getSharedPreferences("TEMPs", MODE_PRIVATE) ?: null }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}
