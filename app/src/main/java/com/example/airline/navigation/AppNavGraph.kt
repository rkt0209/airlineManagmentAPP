package com.example.airline.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airline.ui.screens.auth.LoginScreen
import com.example.airline.ui.screens.auth.SignupScreen

private object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val Home = "home"
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
                    navController.popBackStack()
                }
            )
        }

        // Dummy route so we can verify navigation UX step-by-step.
        composable(Routes.Home) {
            HomePlaceholderScreen(
                onBackToLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun HomePlaceholderScreen(onBackToLogin: () -> Unit) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Home (dummy)",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Login was successful. Next step will implement real content.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onBackToLogin) {
                            Text("Back to Login")
                        }
                    }
                }
            }
        }
    }
}

