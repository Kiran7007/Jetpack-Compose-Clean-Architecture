package com.example.usermanagement.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.usermanagement.presentation.userlist.UserListScreen

/**
 * Navigation graph for the app
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.UserList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // User List Screen
        composable(Screen.UserList.route) {
            UserListScreen(
                onNavigateToDetail = { userId ->
                    navController.navigate(Screen.UserDetail.createRoute(userId))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateUser.route)
                }
            )
        }

        // User Detail Screen
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // UserDetailScreen will be created next
            // UserDetailScreen(userId = userId, onNavigateBack = { navController.popBackStack() })
        }

        // Create User Screen
        composable(Screen.CreateUser.route) {
            // CreateUserScreen will be created next
            // CreateUserScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Update User Screen
        composable(
            route = Screen.UpdateUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // UpdateUserScreen will be created next
            // UpdateUserScreen(userId = userId, onNavigateBack = { navController.popBackStack() })
        }
    }
}
