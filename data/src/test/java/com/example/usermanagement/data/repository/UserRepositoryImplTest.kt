package com.example.usermanagement.data.repository

import androidx.paging.PagingConfig
import com.example.usermanagement.core.Constants
import com.example.usermanagement.core.Result
import com.example.usermanagement.data.local.dao.UserDao
import com.example.usermanagement.data.local.dao.UserRemoteKeysDao
import com.example.usermanagement.data.local.database.UserDatabase
import com.example.usermanagement.data.local.entity.UserEntity
import com.example.usermanagement.data.remote.api.UserApiService
import com.example.usermanagement.data.remote.dto.BaseResponse
import com.example.usermanagement.data.remote.dto.UserDto
import com.example.usermanagement.domain.model.CreateUserInput
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for UserRepositoryImpl.
 * 
 * This demonstrates:
 * - Testing repository logic
 * - Mocking network and database
 * - Testing cache-first strategy
 * - Testing error handling
 */
class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private lateinit var apiService: UserApiService
    private lateinit var database: UserDatabase
    private lateinit var userDao: UserDao
    private lateinit var remoteKeysDao: UserRemoteKeysDao

    private val testUserDto = UserDto(
        id = "1",
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        age = 30,
        avatarUrl = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private val testUserEntity = UserEntity(
        id = "1",
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        age = 30,
        avatarUrl = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        cachedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        apiService = mockk()
        database = mockk()
        userDao = mockk()
        remoteKeysDao = mockk()
        
        every { database.userDao() } returns userDao
        every { database.userRemoteKeysDao() } returns remoteKeysDao
        
        repository = UserRepositoryImpl(apiService, database)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getUserById returns cached user when cache is fresh`() = runTest {
        // Given
        val userId = "1"
        val freshCachedUser = testUserEntity.copy(cachedAt = System.currentTimeMillis())
        coEvery { userDao.getUserById(userId) } returns freshCachedUser

        // When
        val result = repository.getUserById(userId, forceRefresh = false)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(userId, result.data.id)
        coVerify { userDao.getUserById(userId) }
        coVerify(exactly = 0) { apiService.getUserById(any()) }
    }

    @Test
    fun `getUserById fetches from network when forceRefresh is true`() = runTest {
        // Given
        val userId = "1"
        val response = BaseResponse(
            success = true,
            data = testUserDto,
            message = null
        )
        coEvery { apiService.getUserById(userId) } returns Response.success(response)
        coEvery { userDao.insertUser(any()) } just Runs

        // When
        val result = repository.getUserById(userId, forceRefresh = true)

        // Then
        assertTrue(result is Result.Success)
        coVerify { apiService.getUserById(userId) }
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `createUser sends request to API and caches result`() = runTest {
        // Given
        val input = CreateUserInput(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            age = 30
        )
        val response = BaseResponse(
            success = true,
            data = testUserDto,
            message = null
        )
        coEvery { apiService.createUser(any()) } returns Response.success(response)
        coEvery { userDao.insertUser(any()) } just Runs

        // When
        val result = repository.createUser(input)

        // Then
        assertTrue(result is Result.Success)
        coVerify { apiService.createUser(any()) }
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `deleteUser removes user from cache on success`() = runTest {
        // Given
        val userId = "1"
        val response = BaseResponse<Unit>(
            success = true,
            data = Unit,
            message = null
        )
        coEvery { apiService.deleteUser(userId) } returns Response.success(response)
        coEvery { userDao.deleteUserById(userId) } just Runs

        // When
        val result = repository.deleteUser(userId)

        // Then
        assertTrue(result is Result.Success)
        coVerify { apiService.deleteUser(userId) }
        coVerify { userDao.deleteUserById(userId) }
    }

    @Test
    fun `clearCache removes all users and keys`() = runTest {
        // Given
        coEvery { userDao.clearAll() } just Runs
        coEvery { remoteKeysDao.clearRemoteKeys() } just Runs

        // When
        val result = repository.clearCache()

        // Then
        assertTrue(result is Result.Success)
        coVerify { userDao.clearAll() }
        coVerify { remoteKeysDao.clearRemoteKeys() }
    }
}
