package com.example.usermanagement.domain.usecase

import androidx.paging.PagingData
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for fetching paginated list of users.
 * 
 * Business Logic:
 * - Provides seamless pagination with offline support
 * - Uses cache-first strategy via RemoteMediator
 * - Handles loading states automatically
 * - Supports pull-to-refresh via RemoteMediator
 * 
 * Note: This is a simpler UseCase as pagination logic is handled by RemoteMediator.
 * In a real fintech app, you might add:
 * - Filtering logic (by role, status, etc.)
 * - Sorting logic
 * - Permission checks
 */
class GetUsersUseCase(
    private val repository: UserRepository
) {

    operator fun invoke(): Flow<PagingData<User>> {
        return repository.getUsers()
    }
}
