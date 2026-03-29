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
    const val Login          = "login"
    const val Signup         = "signup"
    const val SignupWithRole = "signup?role={role}"
    const val UserMain       = "user-main"
    const val UserMainWithTab = "user-main?tab={tab}"
    const val AdminMain      = "admin-main"

    // Flight results: includes airport IDs for search + display codes for header
    const val FlightResults         = "flight-results"
    const val FlightResultsWithArgs =
        "flight-results?departureId={departureId}&arrivalId={arrivalId}" +
        "&departure={departure}&arrival={arrival}&date={date}"

    // Flight detail: includes DB flightId for booking creation
    const val FlightDetail         = "flight-detail"
    const val FlightDetailWithArgs =
        "flight-detail?flightId={flightId}" +
        "&departure={departure}&arrival={arrival}&date={date}" +
        "&flightNumber={flightNumber}&departureTime={departureTime}" +
        "&arrivalTime={arrivalTime}&pricePerSeat={pricePerSeat}"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController  = navController,
        startDestination = Routes.Login
    ) {

        // ── Auth ──────────────────────────────────────────────────────────────
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role == "Admin") Routes.AdminMain else "${Routes.UserMain}?tab=0"
                    navController.navigate(dest) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onSignupClick = { role ->
                    navController.navigate("${Routes.Signup}?role=${Uri.encode(role)}")
                }
            )
        }

        composable(
            route     = Routes.SignupWithRole,
            arguments = listOf(
                navArgument("role") {
                    type         = NavType.StringType
                    defaultValue = "Passenger"
                }
            )
        ) { backStackEntry ->
            SignupScreen(
                initialRole     = backStackEntry.arguments?.getString("role").orEmpty(),
                onSignupComplete = { role ->
                    val dest = if (role == "Admin") Routes.AdminMain else "${Routes.UserMain}?tab=0"
                    navController.navigate(dest) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

        // ── User main (tabs) ──────────────────────────────────────────────────
        composable(
            route     = Routes.UserMainWithTab,
            arguments = listOf(
                navArgument("tab") {
                    type         = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            UserMainScreen(
                initialTab = backStackEntry.arguments?.getInt("tab") ?: 0,
                onSearchFlights = { departureId, departure, arrivalId, arrival, selectedDate ->
                    navController.navigate(
                        "${Routes.FlightResults}" +
                        "?departureId=$departureId" +
                        "&arrivalId=$arrivalId" +
                        "&departure=${Uri.encode(departure)}" +
                        "&arrival=${Uri.encode(arrival)}" +
                        "&date=${Uri.encode(selectedDate)}"
                    )
                },
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // ── Admin main ────────────────────────────────────────────────────────
        composable(Routes.AdminMain) {
            AdminMainScreen(
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // ── Flight results ────────────────────────────────────────────────────
        composable(
            route     = Routes.FlightResultsWithArgs,
            arguments = listOf(
                navArgument("departureId") { type = NavType.IntType    },
                navArgument("arrivalId")   { type = NavType.IntType    },
                navArgument("departure")   { type = NavType.StringType },
                navArgument("arrival")     { type = NavType.StringType },
                navArgument("date")        { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            FlightResultsScreen(
                departureAirportId = args?.getInt("departureId")    ?: 0,
                arrivalAirportId   = args?.getInt("arrivalId")      ?: 0,
                departureCode      = args?.getString("departure").orEmpty(),
                arrivalCode        = args?.getString("arrival").orEmpty(),
                selectedDate       = args?.getString("date").orEmpty(),
                onBack             = { navController.popBackStack() },
                onFlightSelected   = { flight, departureCode, arrivalCode, date ->
                    navController.navigate(
                        "${Routes.FlightDetail}" +
                        "?flightId=${flight.id}" +
                        "&departure=${Uri.encode(departureCode)}" +
                        "&arrival=${Uri.encode(arrivalCode)}" +
                        "&date=${Uri.encode(date)}" +
                        "&flightNumber=${Uri.encode(flight.flightNumber)}" +
                        "&departureTime=${Uri.encode(flight.departureTime)}" +
                        "&arrivalTime=${Uri.encode(flight.arrivalTime)}" +
                        "&pricePerSeat=${flight.pricePerSeat}"
                    )
                }
            )
        }

        // ── Flight detail / booking ───────────────────────────────────────────
        composable(
            route     = Routes.FlightDetailWithArgs,
            arguments = listOf(
                navArgument("flightId")      { type = NavType.IntType    },
                navArgument("departure")     { type = NavType.StringType },
                navArgument("arrival")       { type = NavType.StringType },
                navArgument("date")          { type = NavType.StringType },
                navArgument("flightNumber")  { type = NavType.StringType },
                navArgument("departureTime") { type = NavType.StringType },
                navArgument("arrivalTime")   { type = NavType.StringType },
                navArgument("pricePerSeat")  { type = NavType.IntType    }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            FlightDetailScreen(
                flightId           = args?.getInt("flightId")           ?: 0,
                departureCode      = args?.getString("departure").orEmpty(),
                arrivalCode        = args?.getString("arrival").orEmpty(),
                selectedDate       = args?.getString("date").orEmpty(),
                flightNumber       = args?.getString("flightNumber").orEmpty(),
                departureTime      = args?.getString("departureTime").orEmpty(),
                arrivalTime        = args?.getString("arrivalTime").orEmpty(),
                pricePerSeat       = args?.getInt("pricePerSeat")        ?: 0,
                onBack             = { navController.popBackStack() },
                onBookingConfirmed = {
                    navController.navigate("${Routes.UserMain}?tab=1") {
                        popUpTo(Routes.UserMain) { inclusive = false }
                    }
                }
            )
        }
    }
}
