package com.example.airline.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.screens.auth.LoginScreen
import com.example.airline.ui.screens.auth.SignupScreen
import com.example.airline.ui.screens.booking.FlightResultsScreen
import com.example.airline.ui.screens.booking.HomeScreen

private object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val Home = "home"
    const val FlightResults = "flight-results"
    const val FlightResultsWithArgs =
        "flight-results?departure={departure}&arrival={arrival}&date={date}"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Login
    ) {
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Routes.Signup)
                }
            )
        }

        composable(Routes.Signup) {
            SignupScreen(
                onSignupComplete = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Signup) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen(
                onSearchFlights = { departureCode, arrivalCode, selectedDate ->
                    navController.navigate(
                        "${Routes.FlightResults}?departure=$departureCode&arrival=$arrivalCode&date=$selectedDate"
                    )
                }
            )
        }

        composable(
            route = Routes.FlightResultsWithArgs,
            arguments = listOf(
                navArgument("departure") { type = NavType.StringType },
                navArgument("arrival") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FlightResultsScreen(
                departureCode = backStackEntry.arguments?.getString("departure").orEmpty(),
                arrivalCode = backStackEntry.arguments?.getString("arrival").orEmpty(),
                selectedDate = backStackEntry.arguments?.getString("date").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
