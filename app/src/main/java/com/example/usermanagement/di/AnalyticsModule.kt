package com.example.usermanagement.di

import android.content.Context
import com.example.usermanagement.data.analytics.FirebaseAnalyticsTracker
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for analytics dependencies.
 * Provides analytics tracker implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsTracker {
        return FirebaseAnalyticsTracker(firebaseAnalytics)
    }
}
