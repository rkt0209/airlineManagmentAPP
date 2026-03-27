package com.example.airline.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.screens.auth.LoginScreen
import com.example.airline.ui.screens.auth.SignupScreen
import com.example.airline.ui.screens.booking.FlightDetailScreen
import com.example.airline.ui.screens.booking.FlightResultsScreen
import com.example.airline.ui.screens.main.AdminMainScreen
import com.example.airline.ui.screens.main.UserMainScreen

private object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val SignupWithRole = "signup?role={role}"
    const val UserMain = "user-main"
    const val UserMainWithTab = "user-main?tab={tab}"
    const val AdminMain = "admin-main"
    const val FlightResults = "flight-results"
    const val FlightResultsWithArgs =
        "flight-results?departure={departure}&arrival={arrival}&date={date}"
    const val FlightDetail = "flight-detail"
    const val FlightDetailWithArgs =
        "flight-detail?departure={departure}&arrival={arrival}&date={date}&flightNumber={flightNumber}&departureTime={departureTime}&arrivalTime={arrivalTime}&pricePerSeat={pricePerSeat}"
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
                onLoginSuccess = { role ->
                    if (role == "Admin") {
                        navController.navigate(Routes.AdminMain) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate("${Routes.UserMain}?tab=0") {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                },
                onSignupClick = { role ->
                    navController.navigate("${Routes.Signup}?role=${Uri.encode(role)}")
                }
            )
        }

        composable(
            route = Routes.SignupWithRole,
            arguments = listOf(
                navArgument("role") {
                    type = NavType.StringType
                    defaultValue = "Passenger"
                }
            )
        ) { backStackEntry ->
            SignupScreen(
                initialRole = backStackEntry.arguments?.getString("role").orEmpty(),
                onSignupComplete = { role ->
                    if (role == "Admin") {
                        navController.navigate(Routes.AdminMain) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate("${Routes.UserMain}?tab=0") {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Routes.UserMainWithTab,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            UserMainScreen(
                initialTab = backStackEntry.arguments?.getInt("tab") ?: 0,
                onSearchFlights = { departureCode, arrivalCode, selectedDate ->
                    navController.navigate(
                        "${Routes.FlightResults}?departure=$departureCode&arrival=$arrivalCode&date=$selectedDate"
                    )
                }
            )
        }

        composable(Routes.AdminMain) {
            AdminMainScreen()
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
                onBack = { navController.popBackStack() },
                onFlightSelected = { flight, departureCode, arrivalCode, date ->
                    navController.navigate(
                        "${Routes.FlightDetail}?" +
                            "departure=${Uri.encode(departureCode)}&" +
                            "arrival=${Uri.encode(arrivalCode)}&" +
                            "date=${Uri.encode(date)}&" +
                            "flightNumber=${Uri.encode(flight.flightNumber)}&" +
                            "departureTime=${Uri.encode(flight.departureTime)}&" +
                            "arrivalTime=${Uri.encode(flight.arrivalTime)}&" +
                            "pricePerSeat=${flight.pricePerSeat}"
                    )
                }
            )
        }

        composable(
            route = Routes.FlightDetailWithArgs,
            arguments = listOf(
                navArgument("departure") { type = NavType.StringType },
                navArgument("arrival") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("flightNumber") { type = NavType.StringType },
                navArgument("departureTime") { type = NavType.StringType },
                navArgument("arrivalTime") { type = NavType.StringType },
                navArgument("pricePerSeat") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            FlightDetailScreen(
                departureCode = backStackEntry.arguments?.getString("departure").orEmpty(),
                arrivalCode = backStackEntry.arguments?.getString("arrival").orEmpty(),
                selectedDate = backStackEntry.arguments?.getString("date").orEmpty(),
                flightNumber = backStackEntry.arguments?.getString("flightNumber").orEmpty(),
                departureTime = backStackEntry.arguments?.getString("departureTime").orEmpty(),
                arrivalTime = backStackEntry.arguments?.getString("arrivalTime").orEmpty(),
                pricePerSeat = backStackEntry.arguments?.getInt("pricePerSeat") ?: 0,
                onBack = { navController.popBackStack() },
                onBookingConfirmed = {
                    navController.navigate("${Routes.UserMain}?tab=1") {
                        popUpTo(Routes.UserMain) { inclusive = false }
                    }
                }
            )
        }
    }
}
