package com.example.usermanagement.di

import com.example.usermanagement.core.DispatcherProvider
import com.example.usermanagement.domain.analytics.AnalyticsTracker
import com.example.usermanagement.domain.repository.UserRepository
import com.example.usermanagement.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for UseCase dependencies.
 * Provides UseCases with ViewModelScoped lifecycle.
 * 
 * Note: UseCases are scoped to ViewModel lifecycle to ensure
 * they're recreated with each ViewModel instance.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetUserByIdUseCase(
        repository: UserRepository,
        analyticsTracker: AnalyticsTracker,
        dispatchers: DispatcherProvider
    ): GetUserByIdUseCase {
        return GetUserByIdUseCase(repository, analyticsTracker, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUsersUseCase(
        repository: UserRepository
    ): GetUsersUseCase {
        return GetUsersUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateUserUseCase(
        repository: UserRepository,
        analyticsTracker: AnalyticsTracker,
        dispatchers: DispatcherProvider
    ): CreateUserUseCase {
        return CreateUserUseCase(repository, analyticsTracker, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateUserUseCase(
        repository: UserRepository,
        analyticsTracker: AnalyticsTracker,
        dispatchers: DispatcherProvider
    ): UpdateUserUseCase {
        return UpdateUserUseCase(repository, analyticsTracker, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteUserUseCase(
        repository: UserRepository,
        analyticsTracker: AnalyticsTracker,
        dispatchers: DispatcherProvider
    ): DeleteUserUseCase {
        return DeleteUserUseCase(repository, analyticsTracker, dispatchers)
    }

    @Provides
    @ViewModelScoped
    fun provideRefreshUsersUseCase(
        repository: UserRepository,
        analyticsTracker: AnalyticsTracker,
        dispatchers: DispatcherProvider
    ): RefreshUsersUseCase {
        return RefreshUsersUseCase(repository, analyticsTracker, dispatchers)
    }
}
