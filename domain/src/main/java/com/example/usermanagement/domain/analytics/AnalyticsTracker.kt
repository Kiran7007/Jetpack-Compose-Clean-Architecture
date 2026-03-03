package com.example.usermanagement.domain.analytics

/**
 * Analytics tracker interface defined in domain layer.
 * This abstraction prevents domain layer from depending on Firebase directly.
 * Implementation will be in data/infrastructure layer.
 * 
 * In a production app, this might track to multiple analytics providers:
 * - Firebase Analytics
 * - Mixpanel
 * - Amplitude
 * - Custom internal analytics
 */
interface AnalyticsTracker {

    /**
     * Track a named event with optional parameters
     */
    fun trackEvent(eventName: String, parameters: Map<String, String> = emptyMap())

    /**
     * Set user properties for analytics
     */
    fun setUserProperty(propertyName: String, value: String)

    /**
     * Identify user for analytics
     */
    fun setUserId(userId: String)

    /**
     * Clear user identification (logout)
     */
    fun clearUserId()
}
