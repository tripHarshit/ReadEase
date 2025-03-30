package com.example.readease

import android.app.Application
import com.example.readease.di.AppContainer
import com.example.readease.di.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReadEaseApplication:Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}