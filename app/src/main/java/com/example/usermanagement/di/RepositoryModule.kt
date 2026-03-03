package com.example.usermanagement.di

import com.example.usermanagement.data.local.database.UserDatabase
import com.example.usermanagement.data.remote.api.UserApiService
import com.example.usermanagement.data.repository.UserRepositoryImpl
import com.example.usermanagement.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies.
 * Binds repository interfaces to implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: UserApiService,
        database: UserDatabase
    ): UserRepository {
        return UserRepositoryImpl(apiService, database)
    }
}
