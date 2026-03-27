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
import com.example.airline.ui.screens.booking.HomeScreen
import com.example.airline.ui.screens.booking.MyBookingsScreen

private object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val Home = "home"
    const val FlightResults = "flight-results"
    const val FlightResultsWithArgs =
        "flight-results?departure={departure}&arrival={arrival}&date={date}"
    const val FlightDetail = "flight-detail"
    const val FlightDetailWithArgs =
        "flight-detail?departure={departure}&arrival={arrival}&date={date}&flightNumber={flightNumber}&departureTime={departureTime}&arrivalTime={arrivalTime}&pricePerSeat={pricePerSeat}"
    const val MyBookings = "my-bookings"
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
                    navController.navigate(Routes.MyBookings) {
                        popUpTo(Routes.FlightDetail) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MyBookings) {
            MyBookingsScreen(
                onBackHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}
