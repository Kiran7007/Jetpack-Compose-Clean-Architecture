package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.repository.UserRepository
import kotlinx.coroutines.withContext

/**
 * UseCase for refreshing the users list.
 * 
 * Business Logic:
 * - Forces a refresh from the network
 * - Clears stale cache data
 * - Tracks analytics for refresh success/failure
 * - Used for pull-to-refresh functionality
 * 
 * This is called when user explicitly requests fresh data,
 * not during normal pagination which uses RemoteMediator.
 */
class RefreshUsersUseCase(
    private val repository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatchers: DispatcherProvider
) {

    suspend operator fun invoke(): Result<Unit> = withContext(dispatchers.io) {
        val result = repository.refreshUsers()

        // Track analytics
        result.onSuccess {
            analyticsTracker.trackEvent("users_refreshed", mapOf(
                "timestamp" to System.currentTimeMillis().toString()
            ))
        }.onError { exception ->
            analyticsTracker.trackEvent("users_refresh_failure", mapOf(
                "error" to exception.javaClass.simpleName,
                "message" to exception.message
            ))
        }

        result
    }
}
