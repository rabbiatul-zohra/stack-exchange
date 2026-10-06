package com.example.stackexchange.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.user.ui.SearchUsers
import com.example.stackexchange.user.ui.UserDetailScreen

private const val SEARCH_ROUTE = "search"
private const val USER_DETAILS_ROUTE = "user/{userId}"

@Composable
fun AppNavHost(
    usersViewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SEARCH_ROUTE,
        modifier = modifier
    ) {

        composable(
            route = SEARCH_ROUTE
        ) {
            SearchUsers(
                viewModel = usersViewModel,
                onUserClick = { userId ->
                    navController.navigate(
                        "user/$userId"
                    )
                }
            )
        }

        composable(
            route = USER_DETAILS_ROUTE,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getInt("userId")
                ?: return@composable

            UserDetailScreen()
        }
    }
}