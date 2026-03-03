package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DefaultDispatcherProvider
import com.example.usermanagement.core.DomainException
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.model.CreateUserInput
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for CreateUserUseCase.
 * 
 * This demonstrates:
 * - Testing business logic (age validation)
 * - Testing input validation
 * - Testing analytics tracking
 * - Testing repository interaction
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserUseCaseTest {

    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var repository: UserRepository
    private lateinit var analyticsTracker: AnalyticsTracker
    private val dispatchers = DefaultDispatcherProvider()

    @Before
    fun setup() {
        repository = mockk()
        analyticsTracker = mockk(relaxed = true)
        createUserUseCase = CreateUserUseCase(repository, analyticsTracker, dispatchers)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke with valid input creates user successfully`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            age = 25
        )
        val createdUser = User(
            id = "1",
            firstName = input.firstName,
            lastName = input.lastName,
            email = input.email,
            age = input.age,
            avatarUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { repository.createUser(input) } returns Result.Success(createdUser)

        // When
        val result = createUserUseCase(input)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(createdUser, (result as Result.Success).data)
        verify { analyticsTracker.trackEvent("user_created", any()) }
        coVerify { repository.createUser(input) }
    }

    @Test
    fun `invoke with age under 13 returns validation error due to COPPA`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "Child",
            lastName = "User",
            email = "child@example.com",
            age = 12
        )

        // When
        val result = createUserUseCase(input)

        // Then
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is DomainException.ValidationException)
        assertTrue(exception.message.contains("13"))
        verify { 
            analyticsTracker.trackEvent(
                "user_creation_failure",
                match { it["reason"] == "age_restriction" }
            )
        }
        coVerify(exactly = 0) { repository.createUser(any()) }
    }

    @Test
    fun `invoke with invalid email returns validation error`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "John",
            lastName = "Doe",
            email = "invalid-email",
            age = 25
        )

        // When
        val result = createUserUseCase(input)

        // Then
        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is DomainException.ValidationException)
        verify { 
            analyticsTracker.trackEvent(
                "user_creation_failure",
                match { it["reason"] == "validation_error" }
            )
        }
    }

    @Test
    fun `invoke with short first name returns validation error`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "J",
            lastName = "Doe",
            email = "john@example.com",
            age = 25
        )

        // When
        val result = createUserUseCase(input)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is DomainException.ValidationException)
    }

    @Test
    fun `invoke with invalid age returns validation error`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            age = 200
        )

        // When
        val result = createUserUseCase(input)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is DomainException.ValidationException)
    }
}
