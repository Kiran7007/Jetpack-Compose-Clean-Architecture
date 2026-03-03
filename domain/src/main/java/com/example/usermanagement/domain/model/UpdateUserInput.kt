package com.example.usermanagement.domain.model

/**
 * Input model for updating an existing user.
 * All fields are optional except id.
 */
data class UpdateUserInput(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val avatarUrl: String? = null
)
