package com.example.usermanagement

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class with Hilt integration.
 * @HiltAndroidApp triggers Hilt's code generation.
 */
@HiltAndroidApp
class UserManagementApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize application-level components here if needed
    }
}
