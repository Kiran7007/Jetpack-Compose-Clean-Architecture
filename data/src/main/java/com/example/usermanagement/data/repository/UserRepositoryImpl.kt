package com.example.usermanagement.data.repository

import androidx.paging.*
import com.example.usermanagement.core.Constants
import com.example.usermanagement.core.DomainException
import com.example.usermanagement.core.Result
import com.example.usermanagement.core.retryIO
import com.example.usermanagement.data.local.database.UserDatabase
import com.example.usermanagement.data.mapper.*
import com.example.usermanagement.data.paging.UserRemoteMediator
import com.example.usermanagement.data.remote.api.UserApiService
import com.example.usermanagement.data.remote.error.safeApiCall
import com.example.usermanagement.domain.model.CreateUserInput
import com.example.usermanagement.domain.model.UpdateUserInput
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository.
 * 
 * This is where the magic happens:
 * - Implements cache-first strategy
 * - Coordinates between network and database
 * - Handles error mapping
 * - Implements retry logic
 * - Provides offline support
 * 
 * Key architectural decisions:
 * - Network responses are always cached
 * - Cache is single source of truth for reads
 * - Writes go to network first, then update cache
 * - Errors are mapped to domain exceptions
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val database: UserDatabase
) : UserRepository {

    private val userDao = database.userDao()

    /**
     * Get user by ID with cache-first strategy.
     * 
     * Strategy:
     * 1. If forceRefresh = false, try to get from cache first
     * 2. If cache miss or forceRefresh = true, fetch from network
     * 3. Update cache with network response
     * 4. Return domain model
     */
    override suspend fun getUserById(userId: String, forceRefresh: Boolean): Result<User> {
        return try {
            // Try cache first if not forcing refresh
            if (!forceRefresh) {
                val cachedUser = userDao.getUserById(userId)
                if (cachedUser != null) {
                    // Check if cache is still fresh (within 1 hour)
                    val cacheAge = System.currentTimeMillis() - cachedUser.cachedAt
                    val cacheExpiry = Constants.CACHE_EXPIRY_HOURS * 60 * 60 * 1000
                    
                    if (cacheAge < cacheExpiry) {
                        return Result.Success(cachedUser.toDomainModel())
                    }
                }
            }

            // Fetch from network with retry
            val response = retryIO(maxRetries = Constants.MAX_RETRY_ATTEMPTS) {
                safeApiCall {
                    apiService.getUserById(userId)
                }
            }

            // Extract user from response
            val userDto = response.data
                ?: throw DomainException.NotFoundException("User not found")

            // Update cache
            userDao.insertUser(userDto.toEntity())

            // Return domain model
            Result.Success(userDto.toDomainModel())

        } catch (e: DomainException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(
                DomainException.UnknownException(
                    message = e.message ?: "Failed to fetch user",
                    cause = e
                )
            )
        }
    }

    /**
     * Get paginated users with offline support.
     * Uses Paging 3 with RemoteMediator for seamless pagination.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                prefetchDistance = Constants.PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(
                apiService = apiService,
                database = database
            ),
            pagingSourceFactory = { userDao.getUsersPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    /**
     * Create a new user.
     * Network-first, then update cache on success.
     */
    override suspend fun createUser(input: CreateUserInput): Result<User> {
        return try {
            // Send to network with retry
            val response = retryIO(maxRetries = Constants.MAX_RETRY_ATTEMPTS) {
                safeApiCall {
                    apiService.createUser(input.toDto())
                }
            }

            val userDto = response.data
                ?: throw DomainException.UnknownException("User creation failed")

            // Update cache
            userDao.insertUser(userDto.toEntity())

            Result.Success(userDto.toDomainModel())

        } catch (e: DomainException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(
                DomainException.UnknownException(
                    message = e.message ?: "Failed to create user",
                    cause = e
                )
            )
        }
    }

    /**
     * Update an existing user.
     * Network-first, then update cache on success.
     */
    override suspend fun updateUser(input: UpdateUserInput): Result<User> {
        return try {
            // Send to network with retry
            val response = retryIO(maxRetries = Constants.MAX_RETRY_ATTEMPTS) {
                safeApiCall {
                    apiService.updateUser(
                        userId = input.id,
                        userDto = input.toDto()
                    )
                }
            }

            val userDto = response.data
                ?: throw DomainException.UnknownException("User update failed")

            // Update cache
            userDao.insertUser(userDto.toEntity())

            Result.Success(userDto.toDomainModel())

        } catch (e: DomainException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(
                DomainException.UnknownException(
                    message = e.message ?: "Failed to update user",
                    cause = e
                )
            )
        }
    }

    /**
     * Delete a user.
     * Network-first, then remove from cache on success.
     */
    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            // Send to network with retry
            retryIO(maxRetries = Constants.MAX_RETRY_ATTEMPTS) {
                safeApiCall {
                    apiService.deleteUser(userId)
                }
            }

            // Remove from cache
            userDao.deleteUserById(userId)

            Result.Success(Unit)

        } catch (e: DomainException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(
                DomainException.UnknownException(
                    message = e.message ?: "Failed to delete user",
                    cause = e
                )
            )
        }
    }

    /**
     * Refresh users list.
     * This is called during pull-to-refresh.
     * RemoteMediator will handle the actual refresh logic.
     */
    override suspend fun refreshUsers(): Result<Unit> {
        return try {
            // Clear cache to force RemoteMediator to refresh
            userDao.clearAll()
            database.userRemoteKeysDao().clearRemoteKeys()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(
                DomainException.CacheException(
                    message = "Failed to refresh users",
                    cause = e
                )
            )
        }
    }

    /**
     * Clear all cached data.
     */
    override suspend fun clearCache(): Result<Unit> {
        return try {
            userDao.clearAll()
            database.userRemoteKeysDao().clearRemoteKeys()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(
                DomainException.CacheException(
                    message = "Failed to clear cache",
                    cause = e
                )
            )
        }
    }
}
