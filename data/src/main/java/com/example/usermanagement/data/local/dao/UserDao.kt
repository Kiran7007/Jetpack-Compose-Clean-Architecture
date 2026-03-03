package com.example.usermanagement.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.usermanagement.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity.
 * Provides methods for local database operations.
 */
@Dao
interface UserDao {

    /**
     * Get paginated users for Paging 3
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getUsersPagingSource(): PagingSource<Int, UserEntity>

    /**
     * Get all users as Flow
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Get user by ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Get user by ID as Flow for reactive updates
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    /**
     * Insert single user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Insert multiple users
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Update user
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Delete user by ID
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    /**
     * Delete all users (for refresh)
     */
    @Query("DELETE FROM users")
    suspend fun clearAll()

    /**
     * Get count of cached users
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    /**
     * Check if user exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :userId)")
    suspend fun userExists(userId: String): Boolean

    /**
     * Get users older than timestamp (for cache expiry)
     */
    @Query("SELECT * FROM users WHERE cached_at < :timestamp")
    suspend fun getStaleUsers(timestamp: Long): List<UserEntity>

    /**
     * Delete stale users
     */
    @Query("DELETE FROM users WHERE cached_at < :timestamp")
    suspend fun deleteStaleUsers(timestamp: Long): Int
}
