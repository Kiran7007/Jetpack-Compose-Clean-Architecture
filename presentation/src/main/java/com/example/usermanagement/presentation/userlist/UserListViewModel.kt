package com.example.usermanagement.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.usermanagement.core.Result
import com.example.usermanagement.domain.model.User
import com.example.usermanagement.domain.usecase.DeleteUserUseCase
import com.example.usermanagement.domain.usecase.GetUsersUseCase
import com.example.usermanagement.domain.usecase.RefreshUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for User List Screen.
 * 
 * Responsibilities:
 * - Coordinates UseCases (does NOT contain business logic)
 * - Manages UI state
 * - Handles UI events
 * - Exposes immutable state to UI
 * - Manages navigation events as one-time effects
 * 
 * This is a THIN ViewModel - all business logic is in UseCases.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(UserListUiState.Empty)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    // Paging data
    val pagingData: Flow<PagingData<User>> = getUsersUseCase()
        .cachedIn(viewModelScope)

    // Navigation events (one-time events)
    private val _navigationEvents = Channel<UserListNavigationEvent>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    /**
     * Handle UI events
     */
    fun onEvent(event: UserListUiEvent) {
        when (event) {
            is UserListUiEvent.Refresh -> refresh()
            is UserListUiEvent.NavigateToUserDetail -> navigateToDetail(event.userId)
            is UserListUiEvent.NavigateToCreateUser -> navigateToCreate()
            is UserListUiEvent.DeleteUser -> deleteUser(event.userId)
            is UserListUiEvent.ErrorDismissed -> dismissError()
        }
    }

    /**
     * Refresh users list
     */
    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null, showError = false) }
            
            when (val result = refreshUsersUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isRefreshing = false,
                            error = result.exception.getUserMessage(),
                            showError = true
                        )
                    }
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Delete a user
     */
    private fun deleteUser(userId: String) {
        viewModelScope.launch {
            when (val result = deleteUserUseCase(userId)) {
                is Result.Success -> {
                    // Success - paging will automatically update
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.exception.getUserMessage(),
                            showError = true
                        )
                    }
                }
                is Result.Loading -> {
                    // Not used for delete
                }
            }
        }
    }

    /**
     * Navigate to user detail
     */
    private fun navigateToDetail(userId: String) {
        viewModelScope.launch {
            _navigationEvents.send(UserListNavigationEvent.NavigateToDetail(userId))
        }
    }

    /**
     * Navigate to create user
     */
    private fun navigateToCreate() {
        viewModelScope.launch {
            _navigationEvents.send(UserListNavigationEvent.NavigateToCreate)
        }
    }

    /**
     * Dismiss error message
     */
    private fun dismissError() {
        _uiState.update { it.copy(error = null, showError = false) }
    }
}
