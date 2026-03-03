package com.example.usermanagement.domain.validation

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for UserValidator.
 * 
 * This demonstrates testing validation business logic.
 */
class UserValidatorTest {

    @Test
    fun `validateFirstName with valid name returns Valid`() {
        // Given
        val firstName = "John"

        // When
        val result = UserValidator.validateFirstName(firstName)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateFirstName with empty name returns Invalid`() {
        // Given
        val firstName = ""

        // When
        val result = UserValidator.validateFirstName(firstName)

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("First name cannot be empty", (result as ValidationResult.Invalid).message)
    }

    @Test
    fun `validateFirstName with too short name returns Invalid`() {
        // Given
        val firstName = "J"

        // When
        val result = UserValidator.validateFirstName(firstName)

        // Then
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateEmail with valid email returns Valid`() {
        // Given
        val email = "john.doe@example.com"

        // When
        val result = UserValidator.validateEmail(email)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateEmail with invalid format returns Invalid`() {
        // Given
        val invalidEmails = listOf(
            "invalid",
            "@example.com",
            "test@",
            "test@@example.com",
            "test@example"
        )

        // When & Then
        invalidEmails.forEach { email ->
            val result = UserValidator.validateEmail(email)
            assertTrue(result is ValidationResult.Invalid, "Expected invalid for: $email")
        }
    }

    @Test
    fun `validateAge with valid age returns Valid`() {
        // Given
        val age = 25

        // When
        val result = UserValidator.validateAge(age)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateAge with age below minimum returns Invalid`() {
        // Given
        val age = 0

        // When
        val result = UserValidator.validateAge(age)

        // Then
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateAge with age above maximum returns Invalid`() {
        // Given
        val age = 200

        // When
        val result = UserValidator.validateAge(age)

        // Then
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateAvatarUrl with null returns Valid`() {
        // Given
        val url: String? = null

        // When
        val result = UserValidator.validateAvatarUrl(url)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateAvatarUrl with valid HTTPS URL returns Valid`() {
        // Given
        val url = "https://example.com/avatar.jpg"

        // When
        val result = UserValidator.validateAvatarUrl(url)

        // Then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateAvatarUrl with non-HTTP URL returns Invalid`() {
        // Given
        val url = "ftp://example.com/avatar.jpg"

        // When
        val result = UserValidator.validateAvatarUrl(url)

        // Then
        assertTrue(result is ValidationResult.Invalid)
    }
}
