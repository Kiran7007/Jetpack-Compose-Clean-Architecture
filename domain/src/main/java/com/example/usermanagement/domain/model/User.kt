package com.example.usermanagement.domain.model

/**
 * Domain model representing a User entity.
 * This is a pure Kotlin class with no Android dependencies.
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val avatarUrl: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    val fullName: String
        get() = "$firstName $lastName"

    val initials: String
        get() = "${firstName.firstOrNull()?.uppercase() ?: ""}${lastName.firstOrNull()?.uppercase() ?: ""}"

    fun isAdult(): Boolean = age >= 18

    companion object {
        fun empty() = User(
            id = "",
            firstName = "",
            lastName = "",
            email = "",
            age = 0,
            avatarUrl = null,
            createdAt = 0L,
            updatedAt = 0L
        )
    }
}
