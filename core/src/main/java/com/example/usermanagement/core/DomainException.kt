package com.example.usermanagement.core

/**
 * Base sealed class for all domain exceptions.
 * These are domain-level errors that are independent of any framework.
 */
sealed class DomainException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Network-related errors
     */
    data class NetworkException(
        override val message: String = "Network error occurred",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Server returned an error
     */
    data class ServerException(
        val code: Int,
        override val message: String,
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Resource not found (404)
     */
    data class NotFoundException(
        override val message: String = "Resource not found",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Unauthorized (401)
     */
    data class UnauthorizedException(
        override val message: String = "Unauthorized access",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Validation error (client-side or server 400)
     */
    data class ValidationException(
        val errors: Map<String, String> = emptyMap(),
        override val message: String = "Validation failed"
    ) : DomainException(message)

    /**
     * Database/cache error
     */
    data class CacheException(
        override val message: String = "Cache error occurred",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * No internet connection
     */
    data class NoInternetException(
        override val message: String = "No internet connection"
    ) : DomainException(message)

    /**
     * Timeout error
     */
    data class TimeoutException(
        override val message: String = "Request timed out",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Unknown error
     */
    data class UnknownException(
        override val message: String = "An unknown error occurred",
        override val cause: Throwable? = null
    ) : DomainException(message, cause)

    /**
     * Get user-friendly error message
     */
    fun getUserMessage(): String = when (this) {
        is NetworkException -> "Unable to connect. Please check your connection."
        is NoInternetException -> "No internet connection. Please check your network."
        is TimeoutException -> "Request timed out. Please try again."
        is NotFoundException -> "Requested resource not found."
        is UnauthorizedException -> "You are not authorized to perform this action."
        is ValidationException -> message
        is ServerException -> "Server error: $message"
        is CacheException -> "Local storage error. Please try again."
        is UnknownException -> "Something went wrong. Please try again."
    }
}
