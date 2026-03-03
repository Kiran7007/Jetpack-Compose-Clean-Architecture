package com.example.usermanagement.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.usermanagement.data.local.dao.UserDao
import com.example.usermanagement.data.local.dao.UserRemoteKeysDao
import com.example.usermanagement.data.local.entity.UserEntity
import com.example.usermanagement.data.local.entity.UserRemoteKeys

/**
 * Room database for the application.
 * Contains users and pagination keys.
 */
@Database(
    entities = [
        UserEntity::class,
        UserRemoteKeys::class
    ],
    version = 1,
    exportSchema = true
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userRemoteKeysDao(): UserRemoteKeysDao

    companion object {
        const val DATABASE_NAME = "user_management_db"
    }
}
