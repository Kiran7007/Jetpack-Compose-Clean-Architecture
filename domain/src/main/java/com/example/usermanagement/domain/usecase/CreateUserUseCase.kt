package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.model.CreateUserInput
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import com.example.usermanagement.domain.validation.UserValidator
import kotlinx.coroutines.withContext

/**
 * UseCase for creating a new user.
 * 
 * Business Logic:
 * - Validates all input fields (first name, last name, email, age)
 * - Ensures email format is valid
 * - Checks age constraints
 * - Validates avatar URL if provided
 * - Tracks analytics for creation success/failure
 * - Can be extended to check for duplicate emails, permissions, etc.
 * 
 * In a fintech app, this might also:
 * - Verify KYC requirements
 * - Check user creation permissions
 * - Validate against blacklists
 * - Trigger welcome email workflows
 */
class CreateUserUseCase(
    private val repository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatchers: DispatcherProvider
) {

    suspend operator fun invoke(input: CreateUserInput): Result<User> = withContext(dispatchers.io) {
        // Validate all input fields
        val firstNameValidation = UserValidator.validateFirstName(input.firstName)
        val lastNameValidation = UserValidator.validateLastName(input.lastName)
        val emailValidation = UserValidator.validateEmail(input.email)
        val ageValidation = UserValidator.validateAge(input.age)
        val avatarValidation = UserValidator.validateAvatarUrl(input.avatarUrl)

        // Aggregate validation errors
        try {
            UserValidator.throwIfInvalid(
                firstNameValidation,
                lastNameValidation,
                emailValidation,
                ageValidation,
                avatarValidation
            )
        } catch (e: com.example.usermanagement.core.DomainException.ValidationException) {
            analyticsTracker.trackEvent("user_creation_failure", mapOf(
                "reason" to "validation_error",
                "errors" to e.errors.toString()
            ))
            return@withContext Result.Error(e)
        }

        // Business rule: For this example, let's add a rule that users under 13 cannot be created
        // This is a real business rule (COPPA compliance in US)
        if (input.age < 13) {
            val exception = com.example.usermanagement.core.DomainException.ValidationException(
                message = "Users under 13 cannot be registered due to privacy regulations"
            )
            analyticsTracker.trackEvent("user_creation_failure", mapOf(
                "reason" to "age_restriction",
                "age" to input.age.toString()
            ))
            return@withContext Result.Error(exception)
        }

        // Create user via repository
        val result = repository.createUser(input)

        // Track analytics
        result.onSuccess { user ->
            analyticsTracker.trackEvent("user_created", mapOf(
                "user_id" to user.id,
                "age" to user.age.toString(),
                "is_adult" to user.isAdult().toString()
            ))
        }.onError { exception ->
            analyticsTracker.trackEvent("user_creation_failure", mapOf(
                "error" to exception.javaClass.simpleName,
                "message" to exception.message
            ))
        }

        result
    }
}
