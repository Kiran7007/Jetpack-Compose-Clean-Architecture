package com.example.usermanagement.domain.model

/**
 * Input model for creating a new user.
 * Used by CreateUserUseCase.
 */
data class CreateUserInput(
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val avatarUrl: String? = null
)
