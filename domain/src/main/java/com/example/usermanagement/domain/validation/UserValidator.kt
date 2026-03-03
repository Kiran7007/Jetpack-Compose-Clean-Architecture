package com.example.usermanagement.domain.validation

import com.example.usermanagement.core.Constants
import com.example.usermanagement.core.DomainException

/**
 * Validator for user-related data.
 * Contains business rules for user validation.
 */
object UserValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    /**
     * Validates first name according to business rules
     */
    fun validateFirstName(firstName: String): ValidationResult {
        return when {
            firstName.isBlank() -> ValidationResult.Invalid("First name cannot be empty")
            firstName.length < Constants.MIN_NAME_LENGTH -> 
                ValidationResult.Invalid("First name must be at least ${Constants.MIN_NAME_LENGTH} characters")
            firstName.length > Constants.MAX_NAME_LENGTH -> 
                ValidationResult.Invalid("First name cannot exceed ${Constants.MAX_NAME_LENGTH} characters")
            !firstName.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' } ->
                ValidationResult.Invalid("First name contains invalid characters")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates last name according to business rules
     */
    fun validateLastName(lastName: String): ValidationResult {
        return when {
            lastName.isBlank() -> ValidationResult.Invalid("Last name cannot be empty")
            lastName.length < Constants.MIN_NAME_LENGTH -> 
                ValidationResult.Invalid("Last name must be at least ${Constants.MIN_NAME_LENGTH} characters")
            lastName.length > Constants.MAX_NAME_LENGTH -> 
                ValidationResult.Invalid("Last name cannot exceed ${Constants.MAX_NAME_LENGTH} characters")
            !lastName.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' } ->
                ValidationResult.Invalid("Last name contains invalid characters")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates email according to business rules
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email cannot be empty")
            email.length < Constants.MIN_EMAIL_LENGTH -> 
                ValidationResult.Invalid("Email is too short")
            email.length > Constants.MAX_EMAIL_LENGTH -> 
                ValidationResult.Invalid("Email is too long")
            !EMAIL_REGEX.matches(email) -> 
                ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates age according to business rules
     */
    fun validateAge(age: Int): ValidationResult {
        return when {
            age < Constants.MIN_AGE -> 
                ValidationResult.Invalid("Age must be at least ${Constants.MIN_AGE}")
            age > Constants.MAX_AGE -> 
                ValidationResult.Invalid("Age cannot exceed ${Constants.MAX_AGE}")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates avatar URL if provided
     */
    fun validateAvatarUrl(url: String?): ValidationResult {
        if (url.isNullOrBlank()) return ValidationResult.Valid
        
        return when {
            !url.startsWith("http://") && !url.startsWith("https://") ->
                ValidationResult.Invalid("Avatar URL must be a valid HTTP(S) URL")
            url.length > 500 ->
                ValidationResult.Invalid("Avatar URL is too long")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates user ID
     */
    fun validateUserId(userId: String): ValidationResult {
        return when {
            userId.isBlank() -> ValidationResult.Invalid("User ID cannot be empty")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Aggregates all validation errors into a single exception
     */
    fun throwIfInvalid(vararg results: ValidationResult) {
        val errors = results.filterIsInstance<ValidationResult.Invalid>()
        if (errors.isNotEmpty()) {
            val errorMap = errors.mapIndexed { index, result -> 
                "field_$index" to result.message 
            }.toMap()
            throw DomainException.ValidationException(
                errors = errorMap,
                message = errors.first().message
            )
        }
    }
}

/**
 * Sealed class representing validation result
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}
