package com.example.usermanagement.data.analytics

import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AnalyticsTracker.
 * This implementation is in the data layer, keeping domain pure.
 */
@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun trackEvent(eventName: String, parameters: Map<String, String>) {
        firebaseAnalytics.logEvent(eventName) {
            parameters.forEach { (key, value) ->
                param(key, value)
            }
        }
    }

    override fun setUserProperty(propertyName: String, value: String) {
        firebaseAnalytics.setUserProperty(propertyName, value)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun clearUserId() {
        firebaseAnalytics.setUserId(null)
    }
}
