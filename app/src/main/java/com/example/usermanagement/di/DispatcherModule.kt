package com.example.usermanagement.di

import com.example.usermanagement.core.DefaultDispatcherProvider
import com.example.usermanagement.core.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for coroutine dispatchers.
 * Provides dispatcher provider for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}
