package com.example.usermanagement.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for User from API.
 * This represents the exact structure returned by the API.
 */
data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("created_at")
    val createdAt: Long,

    @SerializedName("updated_at")
    val updatedAt: Long
)

/**
 * DTO for creating a user
 */
data class CreateUserDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

/**
 * DTO for updating a user
 */
data class UpdateUserDto(
    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("age")
    val age: Int? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)
