package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.model.UpdateUserInput
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import com.example.usermanagement.domain.validation.UserValidator
import kotlinx.coroutines.withContext

/**
 * UseCase for updating an existing user.
 * 
 * Business Logic:
 * - Validates user ID
 * - Validates only the fields that are being updated (non-null fields)
 * - Fetches existing user to verify existence before update
 * - Ensures at least one field is being updated
 * - Tracks analytics for update success/failure
 * 
 * In a fintech app, this might also:
 * - Check update permissions (user can only update their own profile)
 * - Validate that sensitive fields (email) require additional verification
 * - Audit log all changes for compliance
 * - Rate-limit updates to prevent abuse
 */
class UpdateUserUseCase(
    private val repository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatchers: DispatcherProvider
) {

    suspend operator fun invoke(input: UpdateUserInput): Result<User> = withContext(dispatchers.io) {
        // Validate user ID
        val userIdValidation = UserValidator.validateUserId(input.id)
        if (userIdValidation is com.example.usermanagement.domain.validation.ValidationResult.Invalid) {
            analyticsTracker.trackEvent("user_update_failure", mapOf(
                "reason" to "invalid_user_id",
                "message" to userIdValidation.message
            ))
            return@withContext Result.Error(
                com.example.usermanagement.core.DomainException.ValidationException(
                    message = userIdValidation.message
                )
            )
        }

        // Business rule: At least one field must be updated
        if (input.firstName == null && 
            input.lastName == null && 
            input.email == null && 
            input.age == null && 
            input.avatarUrl == null) {
            val exception = com.example.usermanagement.core.DomainException.ValidationException(
                message = "At least one field must be provided for update"
            )
            analyticsTracker.trackEvent("user_update_failure", mapOf(
                "reason" to "no_fields_to_update"
            ))
            return@withContext Result.Error(exception)
        }

        // Validate provided fields
        val validations = mutableListOf<com.example.usermanagement.domain.validation.ValidationResult>()
        
        input.firstName?.let { validations.add(UserValidator.validateFirstName(it)) }
        input.lastName?.let { validations.add(UserValidator.validateLastName(it)) }
        input.email?.let { validations.add(UserValidator.validateEmail(it)) }
        input.age?.let { validations.add(UserValidator.validateAge(it)) }
        validations.add(UserValidator.validateAvatarUrl(input.avatarUrl))

        try {
            UserValidator.throwIfInvalid(*validations.toTypedArray())
        } catch (e: com.example.usermanagement.core.DomainException.ValidationException) {
            analyticsTracker.trackEvent("user_update_failure", mapOf(
                "reason" to "validation_error",
                "errors" to e.errors.toString()
            ))
            return@withContext Result.Error(e)
        }

        // Business rule: Age restriction applies to updates too
        if (input.age != null && input.age < 13) {
            val exception = com.example.usermanagement.core.DomainException.ValidationException(
                message = "Age cannot be updated to less than 13 due to privacy regulations"
            )
            analyticsTracker.trackEvent("user_update_failure", mapOf(
                "reason" to "age_restriction",
                "age" to input.age.toString()
            ))
            return@withContext Result.Error(exception)
        }

        // Update user via repository
        val result = repository.updateUser(input)

        // Track analytics
        result.onSuccess { user ->
            val updatedFields = mutableListOf<String>()
            input.firstName?.let { updatedFields.add("firstName") }
            input.lastName?.let { updatedFields.add("lastName") }
            input.email?.let { updatedFields.add("email") }
            input.age?.let { updatedFields.add("age") }
            input.avatarUrl?.let { updatedFields.add("avatarUrl") }

            analyticsTracker.trackEvent("user_updated", mapOf(
                "user_id" to user.id,
                "updated_fields" to updatedFields.joinToString(",")
            ))
        }.onError { exception ->
            analyticsTracker.trackEvent("user_update_failure", mapOf(
                "user_id" to input.id,
                "error" to exception.javaClass.simpleName,
                "message" to exception.message
            ))
        }

        result
    }
}
