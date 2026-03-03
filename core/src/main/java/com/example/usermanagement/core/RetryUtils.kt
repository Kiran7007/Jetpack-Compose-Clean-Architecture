package com.example.usermanagement.core

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.math.pow

/**
 * Retry configuration for network requests
 */
data class RetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMillis: Long = 1000L,
    val maxDelayMillis: Long = 10000L,
    val factor: Double = 2.0,
    val retryableExceptions: Set<Class<out Exception>> = setOf(IOException::class.java)
)

/**
 * Executes a suspend function with exponential backoff retry logic.
 * Only retries on IOException (network errors), not on business logic errors.
 */
suspend fun <T> retryWithExponentialBackoff(
    config: RetryConfig = RetryConfig(),
    block: suspend () -> T
): T {
    var currentDelay = config.initialDelayMillis
    var lastException: Exception? = null

    repeat(config.maxRetries) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            
            // Don't retry if exception is not retryable
            val shouldRetry = config.retryableExceptions.any { it.isInstance(e) }
            if (!shouldRetry || attempt == config.maxRetries - 1) {
                throw e
            }

            // Calculate delay with exponential backoff
            val delayMillis = minOf(
                currentDelay,
                config.maxDelayMillis
            )
            
            delay(delayMillis)
            
            // Increase delay for next retry
            currentDelay = (currentDelay * config.factor).toLong()
        }
    }

    // This should never be reached, but throw last exception if it does
    throw lastException ?: IllegalStateException("Retry failed without exception")
}

/**
 * Extension function for easier retry usage
 */
suspend fun <T> retryIO(
    maxRetries: Int = 3,
    block: suspend () -> T
): T = retryWithExponentialBackoff(
    config = RetryConfig(maxRetries = maxRetries),
    block = block
)
