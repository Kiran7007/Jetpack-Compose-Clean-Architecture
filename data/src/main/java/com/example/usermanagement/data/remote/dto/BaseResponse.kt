package com.example.usermanagement.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Base response wrapper for API responses.
 * Provides consistent response structure across all endpoints.
 */
data class BaseResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: T?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("errors")
    val errors: Map<String, String>? = null,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Paginated response wrapper
 */
data class PaginatedResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<T>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalItems")
    val totalItems: Int,

    @SerializedName("hasNext")
    val hasNext: Boolean,

    @SerializedName("hasPrevious")
    val hasPrevious: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
