package com.example.airline.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.airline.ui.screens.booking.HomeScreen
import com.example.airline.ui.screens.booking.MyBookingsScreen
import com.example.airline.ui.screens.profile.UserProfileScreen

@Composable
fun UserMainScreen(
    initialTab: Int,
    onSearchFlights: (
        departureId:   Int,
        departure:     String,
        arrivalId:     Int,
        arrival:       String,
        selectedDate:  String
    ) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab.coerceIn(0, 2)) }

    Scaffold(
        bottomBar = {
            CurvedBottomNav(
                selectedTab  = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            0    -> HomeScreen(onSearchFlights = onSearchFlights, outerPadding = innerPadding)
            1    -> MyBookingsScreen(onBackHome = { selectedTab = 0 }, outerPadding = innerPadding)
            else -> UserProfileScreen(outerPadding = innerPadding, onLogout = onLogout)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom curved (wave) bottom navigation
// Layout: Box(88dp tall)
//   • Canvas draws the bar shape starting at y=30dp, with a smooth concave
//     notch at the horizontal center that dips down to accommodate the FAB.
//   • The 60dp FAB is placed at Alignment.TopCenter so its center sits exactly
//     on the bar's top line (y=30dp), giving it the "raised" look.
//   • Left (My Bookings) and Right (Profile) columns sit in the lower portion.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CurvedBottomNav(
    selectedTab:  Int,
    onTabSelected: (Int) -> Unit
) {
    val surfaceColor   = MaterialTheme.colorScheme.surface
    val activeColor    = MaterialTheme.colorScheme.primary
    val inactiveColor  = MaterialTheme.colorScheme.onSurfaceVariant
    val fabActiveContainer   = MaterialTheme.colorScheme.primary
    val fabInactiveContainer = MaterialTheme.colorScheme.secondaryContainer
    val onFabActive    = MaterialTheme.colorScheme.onPrimary
    val onFabInactive  = MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        // ── Wave-shaped bar background ──────────────────────────────────────
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
        ) {
            val barTop  = 30.dp.toPx()   // flat bar top line (= FAB half-height)
            val w       = size.width
            val h       = size.height
            val cx      = w / 2f
            val notchR  = 38.dp.toPx()   // notch "radius" — slightly larger than FAB radius
            val smooth  = 18.dp.toPx()   // horizontal extent of the S-curve transition

            // The path traces the bar's top surface:
            //   flat → left S-curve ↘ → valley bottom → right S-curve ↗ → flat
            // then closes via the outer rectangle edges.
            val path = Path().apply {
                moveTo(0f, barTop)
                lineTo(cx - notchR - smooth, barTop)
                // Left entry curve: sweeps down into the notch valley
                cubicTo(
                    cx - notchR, barTop,
                    cx - notchR, barTop + notchR,
                    cx,          barTop + notchR
                )
                // Right exit curve: sweeps back up out of the valley
                cubicTo(
                    cx + notchR, barTop + notchR,
                    cx + notchR, barTop,
                    cx + notchR + smooth, barTop
                )
                lineTo(w, barTop)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            // Subtle top-edge shadow (drawn first, slightly larger via draw order)
            drawPath(path, color = Color.Black.copy(alpha = 0.08f))
            // Solid bar fill
            drawPath(path, color = surfaceColor)

            // 1 dp separator line on the flat portions only (skip the notch)
            val sepH = 1.dp.toPx()
            drawRect(
                color   = Color.Black.copy(alpha = 0.07f),
                topLeft = Offset(0f, barTop),
                size    = Size(cx - notchR - smooth, sepH)
            )
            drawRect(
                color   = Color.Black.copy(alpha = 0.07f),
                topLeft = Offset(cx + notchR + smooth, barTop),
                size    = Size(w - (cx + notchR + smooth), sepH)
            )
        }

        // ── Left tab: My Bookings ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(1) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector        = Icons.Filled.Bookmark,
                contentDescription = "My Bookings",
                tint               = if (selectedTab == 1) activeColor else inactiveColor,
                modifier           = Modifier.size(24.dp)
            )
            Text(
                text  = "Bookings",
                style = MaterialTheme.typography.labelSmall,
                color = if (selectedTab == 1) activeColor else inactiveColor
            )
        }

        // ── Center tab: Search Flights (raised FAB) ─────────────────────────
        FloatingActionButton(
            onClick        = { onTabSelected(0) },
            modifier       = Modifier
                .align(Alignment.TopCenter)
                .size(60.dp),
            containerColor = if (selectedTab == 0) fabActiveContainer else fabInactiveContainer,
            elevation      = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp
            )
        ) {
            Icon(
                imageVector        = Icons.Filled.FlightTakeoff,
                contentDescription = "Search Flights",
                tint               = if (selectedTab == 0) onFabActive else onFabInactive,
                modifier           = Modifier.size(28.dp)
            )
        }

        // "Flights" label under the FAB
        Text(
            text     = "Flights",
            style    = MaterialTheme.typography.labelSmall,
            color    = if (selectedTab == 0) activeColor else inactiveColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        // ── Right tab: Profile ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(2) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector        = Icons.Filled.Person,
                contentDescription = "Profile",
                tint               = if (selectedTab == 2) activeColor else inactiveColor,
                modifier           = Modifier.size(24.dp)
            )
            Text(
                text  = "Profile",
                style = MaterialTheme.typography.labelSmall,
                color = if (selectedTab == 2) activeColor else inactiveColor
            )
        }
    }
}
