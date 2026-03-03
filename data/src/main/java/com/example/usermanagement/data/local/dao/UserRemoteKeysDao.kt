package com.example.usermanagement.data.local.dao

import androidx.room.*
import com.example.usermanagement.data.local.entity.UserRemoteKeys

/**
 * DAO for UserRemoteKeys.
 * Used by RemoteMediator to track pagination state.
 */
@Dao
interface UserRemoteKeysDao {

    /**
     * Get remote keys for a user
     */
    @Query("SELECT * FROM user_remote_keys WHERE user_id = :userId")
    suspend fun getRemoteKeys(userId: String): UserRemoteKeys?

    /**
     * Insert or replace remote keys
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<UserRemoteKeys>)

    /**
     * Clear all remote keys
     */
    @Query("DELETE FROM user_remote_keys")
    suspend fun clearRemoteKeys()

    /**
     * Delete remote keys older than timestamp
     */
    @Query("DELETE FROM user_remote_keys WHERE created_at < :timestamp")
    suspend fun deleteOldKeys(timestamp: Long): Int
}
