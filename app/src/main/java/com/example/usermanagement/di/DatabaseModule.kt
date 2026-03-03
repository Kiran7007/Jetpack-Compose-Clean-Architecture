package com.example.usermanagement.di

import android.content.Context
import androidx.room.Room
import com.example.usermanagement.core.Constants
import com.example.usermanagement.data.local.dao.UserDao
import com.example.usermanagement.data.local.dao.UserRemoteKeysDao
import com.example.usermanagement.data.local.database.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(
        @ApplicationContext context: Context
    ): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRemoteKeysDao(database: UserDatabase): UserRemoteKeysDao {
        return database.userRemoteKeysDao()
    }
}
