package com.example.airline.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
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
import com.example.airline.ui.screens.admin.AdminAirplanesScreen
import com.example.airline.ui.screens.admin.AdminAirportsScreen
import com.example.airline.ui.screens.admin.AdminCitiesScreen
import com.example.airline.ui.screens.admin.AdminFlightsScreen
import com.example.airline.ui.screens.profile.AdminProfileScreen

@Composable
fun AdminMainScreen(onLogout: () -> Unit) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            AdminCurvedBottomNav(
                selectedTab   = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            0    -> AdminCitiesScreen(onBack = {}, showBackButton = false, outerPadding = innerPadding)
            1    -> AdminAirportsScreen(outerPadding = innerPadding)
            2    -> AdminAirplanesScreen(outerPadding = innerPadding)
            3    -> AdminFlightsScreen(outerPadding = innerPadding)
            else -> AdminProfileScreen(outerPadding = innerPadding, onLogout = onLogout)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Admin 5-tab curved bottom navigation
//
// Visual layout:
//   [Cities]  [Airports]   ( ✈ Flights FAB )   [Airplanes]  [Profile]
//    idx=0      idx=1            idx=3             idx=2        idx=4
//
// The wave/notch shape is identical to CurvedBottomNav in UserMainScreen —
// same Canvas path, same 90dp Box height, same 60dp raised FAB.
// Four tab columns (2 left, 2 right) sit in the flat regions of the bar.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminCurvedBottomNav(
    selectedTab:   Int,
    onTabSelected: (Int) -> Unit
) {
    val surfaceColor         = MaterialTheme.colorScheme.surface
    val activeColor          = MaterialTheme.colorScheme.primary
    val inactiveColor        = MaterialTheme.colorScheme.onSurfaceVariant
    val fabActiveContainer   = MaterialTheme.colorScheme.primary
    val fabInactiveContainer = MaterialTheme.colorScheme.secondaryContainer
    val onFabActive          = MaterialTheme.colorScheme.onPrimary
    val onFabInactive        = MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        // ── Wave-shaped bar background ──────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            val barTop = 30.dp.toPx()   // flat bar top = FAB half-height
            val w      = size.width
            val h      = size.height
            val cx     = w / 2f
            val notchR = 38.dp.toPx()   // notch radius
            val smooth = 18.dp.toPx()   // S-curve horizontal extent

            val path = Path().apply {
                moveTo(0f, barTop)
                lineTo(cx - notchR - smooth, barTop)
                cubicTo(cx - notchR, barTop, cx - notchR, barTop + notchR, cx, barTop + notchR)
                cubicTo(cx + notchR, barTop + notchR, cx + notchR, barTop, cx + notchR + smooth, barTop)
                lineTo(w, barTop)
                lineTo(w, h); lineTo(0f, h); close()
            }

            drawPath(path, color = Color.Black.copy(alpha = 0.08f))
            drawPath(path, color = surfaceColor)

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

        // ── Left pair: Cities (0) + Airports (1) ───────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Cities
            Column(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(0) }
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.LocationCity,
                    contentDescription = "Cities",
                    tint               = if (selectedTab == 0) activeColor else inactiveColor,
                    modifier           = Modifier.size(22.dp)
                )
                Text(
                    text  = "Cities",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTab == 0) activeColor else inactiveColor
                )
            }
            // Airports
            Column(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(1) }
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Place,
                    contentDescription = "Airports",
                    tint               = if (selectedTab == 1) activeColor else inactiveColor,
                    modifier           = Modifier.size(22.dp)
                )
                Text(
                    text  = "Airports",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTab == 1) activeColor else inactiveColor
                )
            }
        }

        // ── Center FAB: Flights (3) ─────────────────────────────────────────
        FloatingActionButton(
            onClick        = { onTabSelected(3) },
            modifier       = Modifier
                .align(Alignment.TopCenter)
                .size(60.dp),
            containerColor = if (selectedTab == 3) fabActiveContainer else fabInactiveContainer,
            elevation      = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp
            )
        ) {
            Icon(
                imageVector        = Icons.Filled.Flight,
                contentDescription = "Flights",
                tint               = if (selectedTab == 3) onFabActive else onFabInactive,
                modifier           = Modifier.size(28.dp)
            )
        }
        Text(
            text     = "Flights",
            style    = MaterialTheme.typography.labelSmall,
            color    = if (selectedTab == 3) activeColor else inactiveColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        // ── Right pair: Airplanes (2) + Profile (4) ─────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Airplanes
            Column(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(2) }
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.AirplanemodeActive,
                    contentDescription = "Airplanes",
                    tint               = if (selectedTab == 2) activeColor else inactiveColor,
                    modifier           = Modifier.size(22.dp)
                )
                Text(
                    text  = "Planes",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTab == 2) activeColor else inactiveColor
                )
            }
            // Profile
            Column(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = LocalIndication.current,
                    onClick           = { onTabSelected(4) }
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint               = if (selectedTab == 4) activeColor else inactiveColor,
                    modifier           = Modifier.size(22.dp)
                )
                Text(
                    text  = "Profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTab == 4) activeColor else inactiveColor
                )
            }
        }
    }
}
