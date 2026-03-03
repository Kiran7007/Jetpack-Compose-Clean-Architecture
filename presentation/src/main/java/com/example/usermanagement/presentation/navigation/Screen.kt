package com.example.usermanagement.presentation.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    data object UserList : Screen("user_list")
    data object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: String) = "user_detail/$userId"
    }
    data object CreateUser : Screen("create_user")
    data object UpdateUser : Screen("update_user/{userId}") {
        fun createRoute(userId: String) = "update_user/$userId"
    }
}
