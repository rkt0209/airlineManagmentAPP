package com.example.airline.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.airline.ui.screens.booking.HomeScreen
import com.example.airline.ui.screens.booking.MyBookingsScreen
import com.example.airline.ui.screens.profile.UserProfileScreen

@Composable
fun UserMainScreen(
    initialTab: Int,
    onSearchFlights: (departureCode: String, arrivalCode: String, selectedDate: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab.coerceIn(0, 2)) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Bookmark, contentDescription = "Bookings") },
                    label = { Text("Bookings") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(
                onSearchFlights = onSearchFlights,
                outerPadding = innerPadding
            )
            1 -> MyBookingsScreen(
                onBackHome = { selectedTab = 0 },
                outerPadding = innerPadding
            )
            else -> UserProfileScreen(
                outerPadding = innerPadding,
                onLogout = onLogout
            )
        }
    }
}
