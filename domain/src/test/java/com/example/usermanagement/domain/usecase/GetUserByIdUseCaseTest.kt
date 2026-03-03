package com.example.usermanagement.domain.usecase

import com.example.usermanagement.core.DefaultDispatcherProvider
import com.example.usermanagement.core.DomainException
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GetUserByIdUseCase.
 * 
 * This demonstrates:
 * - How to test UseCases
 * - Mocking dependencies
 * - Testing validation logic
 * - Testing analytics tracking
 * - Testing error scenarios
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetUserByIdUseCaseTest {

    private lateinit var getUserByIdUseCase: GetUserByIdUseCase
    private lateinit var repository: UserRepository
    private lateinit var analyticsTracker: AnalyticsTracker
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = DefaultDispatcherProvider()

    private val testUser = User(
        id = "1",
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        age = 30,
        avatarUrl = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        repository = mockk()
        analyticsTracker = mockk(relaxed = true)
        getUserByIdUseCase = GetUserByIdUseCase(repository, analyticsTracker, dispatchers)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke with valid userId returns success`() = runTest {
        // Given
        val userId = "1"
        coEvery { repository.getUserById(userId, false) } returns Result.Success(testUser)

        // When
        val result = getUserByIdUseCase(userId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
        verify { analyticsTracker.trackEvent("user_fetch_success", any()) }
        coVerify { repository.getUserById(userId, false) }
    }

    @Test
    fun `invoke with empty userId returns validation error`() = runTest {
        // Given
        val userId = ""

        // When
        val result = getUserByIdUseCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is DomainException.ValidationException)
        verify { analyticsTracker.trackEvent("user_fetch_failure", any()) }
        coVerify(exactly = 0) { repository.getUserById(any(), any()) }
    }

    @Test
    fun `invoke with network error tracks failure analytics`() = runTest {
        // Given
        val userId = "1"
        val networkException = DomainException.NetworkException("Network error")
        coEvery { repository.getUserById(userId, false) } returns Result.Error(networkException)

        // When
        val result = getUserByIdUseCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(networkException, (result as Result.Error).exception)
        verify { 
            analyticsTracker.trackEvent(
                "user_fetch_failure",
                match { it["error"] == "NetworkException" }
            )
        }
    }

    @Test
    fun `invoke with forceRefresh true calls repository with forceRefresh`() = runTest {
        // Given
        val userId = "1"
        coEvery { repository.getUserById(userId, true) } returns Result.Success(testUser)

        // When
        val result = getUserByIdUseCase(userId, forceRefresh = true)

        // Then
        assertTrue(result is Result.Success)
        coVerify { repository.getUserById(userId, true) }
    }
}
