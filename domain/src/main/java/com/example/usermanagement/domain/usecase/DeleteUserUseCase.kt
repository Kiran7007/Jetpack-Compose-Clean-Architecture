package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.repository.UserRepository
import com.example.usermanagement.domain.validation.UserValidator
import kotlinx.coroutines.withContext

/**
 * UseCase for deleting a user.
 * 
 * Business Logic:
 * - Validates user ID
 * - Verifies user exists before deletion
 * - Tracks analytics for deletion success/failure
 * - Can be extended to implement soft delete vs hard delete
 * 
 * In a fintech app, this might also:
 * - Check deletion permissions (admin only)
 * - Verify no active transactions/loans exist
 * - Archive user data instead of deleting (GDPR compliance)
 * - Trigger account closure workflows
 * - Send confirmation emails
 * - Check for dependent records (beneficiaries, linked accounts)
 */
class DeleteUserUseCase(
    private val repository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatchers: DispatcherProvider
) {

    suspend operator fun invoke(userId: String): Result<Unit> = withContext(dispatchers.io) {
        // Validate user ID
        val validationResult = UserValidator.validateUserId(userId)
        if (validationResult is com.example.usermanagement.domain.validation.ValidationResult.Invalid) {
            analyticsTracker.trackEvent("user_deletion_failure", mapOf(
                "reason" to "validation_error",
                "message" to validationResult.message
            ))
            return@withContext Result.Error(
                com.example.usermanagement.core.DomainException.ValidationException(
                    message = validationResult.message
                )
            )
        }

        // Business rule: Verify user exists before deletion
        // This prevents unnecessary API calls and provides better error messages
        val userResult = repository.getUserById(userId, forceRefresh = false)
        if (userResult is Result.Error) {
            analyticsTracker.trackEvent("user_deletion_failure", mapOf(
                "reason" to "user_not_found",
                "user_id" to userId
            ))
            return@withContext userResult.map { } // Convert Result<User> to Result<Unit>
        }

        // Delete user via repository
        val result = repository.deleteUser(userId)

        // Track analytics
        result.onSuccess {
            analyticsTracker.trackEvent("user_deleted", mapOf(
                "user_id" to userId
            ))
        }.onError { exception ->
            analyticsTracker.trackEvent("user_deletion_failure", mapOf(
                "user_id" to userId,
                "error" to exception.javaClass.simpleName,
                "message" to exception.message
            ))
        }

        result
    }
}
