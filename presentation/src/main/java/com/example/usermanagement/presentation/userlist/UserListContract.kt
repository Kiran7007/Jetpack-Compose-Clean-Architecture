package com.example.usermanagement.presentation.userlist

import com.example.usermanagement.domain.model.User

/**
 * UI State for User List Screen.
 * Single immutable state object following unidirectional data flow.
 */
data class UserListUiState(
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showError: Boolean = false
) {
    companion object {
        val Empty = UserListUiState()
    }
}

/**
 * UI Events for User List Screen.
 * Represents user interactions.
 */
sealed class UserListUiEvent {
    data object Refresh : UserListUiEvent()
    data class NavigateToUserDetail(val userId: String) : UserListUiEvent()
    data object NavigateToCreateUser : UserListUiEvent()
    data class DeleteUser(val userId: String) : UserListUiEvent()
    data object ErrorDismissed : UserListUiEvent()
}

/**
 * Navigation events (one-time events)
 */
sealed class UserListNavigationEvent {
    data class NavigateToDetail(val userId: String) : UserListNavigationEvent()
    data object NavigateToCreate : UserListNavigationEvent()
}
