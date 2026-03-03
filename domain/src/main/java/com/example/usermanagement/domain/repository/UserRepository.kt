package com.example.usermanagement.domain.repository

import androidx.paging.PagingData
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.model.CreateUserInput
import com.example.usermanagement.domain.model.UpdateUserInput
import com.example.usermanagement.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defined in the domain layer.
 * Implementation will be in the data layer.
 * This interface defines all data operations for User entities.
 */
interface UserRepository {

    /**
     * Get a user by ID.
     * Cache-first strategy: returns cached data if available and fresh,
     * otherwise fetches from network and updates cache.
     */
    suspend fun getUserById(userId: String, forceRefresh: Boolean = false): Result<User>

    /**
     * Get paginated list of users with offline support.
     * Uses Paging 3 with RemoteMediator for seamless pagination.
     */
    fun getUsers(): Flow<PagingData<User>>

    /**
     * Create a new user.
     * On success, updates local cache.
     */
    suspend fun createUser(input: CreateUserInput): Result<User>

    /**
     * Update an existing user.
     * On success, updates local cache.
     */
    suspend fun updateUser(input: UpdateUserInput): Result<User>

    /**
     * Delete a user by ID.
     * On success, removes from local cache.
     */
    suspend fun deleteUser(userId: String): Result<Unit>

    /**
     * Refresh users list from network.
     * Clears cache and fetches fresh data.
     */
    suspend fun refreshUsers(): Result<Unit>

    /**
     * Clear all cached users.
     * Useful for logout scenarios.
     */
    suspend fun clearCache(): Result<Unit>
}
