package com.example.usermanagement.core

object Constants {
    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE_KEY = 1
    const val PREFETCH_DISTANCE = 5

    // Network
    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    const val CONNECT_TIMEOUT_SECONDS = 30L

    // Cache
    const val CACHE_EXPIRY_HOURS = 1L
    const val MAX_CACHE_SIZE_MB = 10L

    // Retry
    const val MAX_RETRY_ATTEMPTS = 3
    const val INITIAL_RETRY_DELAY_MS = 1000L
    const val MAX_RETRY_DELAY_MS = 10000L
    const val RETRY_BACKOFF_FACTOR = 2.0

    // Database
    const val DATABASE_NAME = "user_management_db"
    const val DATABASE_VERSION = 1

    // Validation
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
    const val MIN_EMAIL_LENGTH = 5
    const val MAX_EMAIL_LENGTH = 100
    const val MIN_AGE = 1
    const val MAX_AGE = 150
}
