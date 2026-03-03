package com.example.usermanagement.core

/**
 * A generic wrapper class for handling success and error states.
 * This provides type-safe error handling without throwing exceptions in the domain layer.
 */
sealed class Result<out T> {
    
    data class Success<T>(val data: T) : Result<T>()
    
    data class Error(val exception: DomainException) : Result<Nothing>()
    
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    /**
     * Returns data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns data if Success, throws exception if Error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Cannot get data from Loading state")
    }

    /**
     * Transform success data
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }

    /**
     * Execute action if success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute action if error
     */
    inline fun onError(action: (DomainException) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    companion object {
        /**
         * Wraps a suspend function with try-catch and returns Result
         */
        suspend inline fun <T> runCatching(crossinline block: suspend () -> T): Result<T> {
            return try {
                Success(block())
            } catch (e: DomainException) {
                Error(e)
            } catch (e: Exception) {
                Error(DomainException.UnknownException(e.message ?: "Unknown error"))
            }
        }
    }
}
