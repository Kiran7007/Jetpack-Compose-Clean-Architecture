package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import com.example.usermanagement.domain.validation.UserValidator
import kotlinx.coroutines.withContext

/**
 * UseCase for fetching a user by ID.
 * 
 * Business Logic:
 * - Validates user ID before fetching
 * - Tracks analytics for success/failure
 * - Handles force refresh logic
 * - Can be used for profile viewing, user details, etc.
 */
class GetUserByIdUseCase(
    private val repository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatchers: DispatcherProvider
) {

    suspend operator fun invoke(
        userId: String,
        forceRefresh: Boolean = false
    ): Result<User> = withContext(dispatchers.io) {
        // Validate input
        val validationResult = UserValidator.validateUserId(userId)
        if (validationResult is com.example.usermanagement.domain.validation.ValidationResult.Invalid) {
            analyticsTracker.trackEvent("user_fetch_failure", mapOf(
                "reason" to "validation_error",
                "message" to validationResult.message
            ))
            return@withContext Result.Error(
                com.example.usermanagement.core.DomainException.ValidationException(
                    message = validationResult.message
                )
            )
        }

        // Fetch user from repository
        val result = repository.getUserById(userId, forceRefresh)

        // Track analytics
        result.onSuccess { user ->
            analyticsTracker.trackEvent("user_fetch_success", mapOf(
                "user_id" to user.id,
                "from_cache" to (!forceRefresh).toString()
            ))
        }.onError { exception ->
            analyticsTracker.trackEvent("user_fetch_failure", mapOf(
                "user_id" to userId,
                "error" to exception.javaClass.simpleName,
                "message" to exception.message
            ))
        }

        result
    }
}
