package com.example.airline.ui.screens.booking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

// ─── Airline branding palette (index-matched) ─────────────────────────────────
private val AirlineBgColors = listOf(
    Color(0xFF1565C0), Color(0xFF00838F), Color(0xFF2E7D32),
    Color(0xFF6A1B9A), Color(0xFFBF360C)
)
private val AirlineInitials = listOf("IA", "SL", "BS", "VA", "AD")
private val AirlineNames    = listOf("IndiAir", "SkyLink", "BlueStar", "VeloAir", "AirDesh")

// ─── Header palette ───────────────────────────────────────────────────────────
private val ResHdrTop     = Color(0xFF071226)
private val ResHdrMid     = Color(0xFF0B1B3A)
private val ResHdrBottom  = Color(0xFF1565C0)
private val ResOnHdr      = Color(0xFFEAF2FF)
private val ResOnHdrMuted = Color(0xFF8BAFD4)

// ─── Shared UI flight model ───────────────────────────────────────────────────
data class FlightUi(
    val id:            Int,
    val flightNumber:  String,
    val departureTime: String,   // HH:mm display string
    val arrivalTime:   String,   // HH:mm display string
    val pricePerSeat:  Int,
    val seatsLeft:     Int
)

@Composable
fun FlightResultsScreen(
    departureAirportId: Int,
    arrivalAirportId:   Int,
    departureCode:      String,
    arrivalCode:        String,
    selectedDate:       String,
    onBack:             () -> Unit,
    onFlightSelected:   (flight: FlightUi, departureCode: String, arrivalCode: String, date: String) -> Unit,
    viewModel:          FlightResultsViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()

    // Trigger search once when the screen is first shown
    LaunchedEffect(departureAirportId, arrivalAirportId) {
        viewModel.searchFlights(departureAirportId, arrivalAirportId)
    }

    // Map API FlightItem → FlightUi
    val flights: List<FlightUi> = when (val s = searchState) {
        is FlightSearchState.Success -> s.flights.map { f ->
            FlightUi(
                id            = f.id,
                flightNumber  = f.flightNumber,
                departureTime = parseDisplayTime(f.departureTime),
                arrivalTime   = parseDisplayTime(f.arrivalTime),
                pricePerSeat  = f.price,
                seatsLeft     = f.totalSeats
            )
        }
        else -> emptyList()
    }

    Scaffold(contentWindowInsets = WindowInsets(0)) { _ ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── Gradient header ─────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(ResHdrTop, ResHdrMid, ResHdrBottom))
                        )
                ) {
                    Icon(
                        imageVector        = Icons.Filled.FlightTakeoff,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.05f),
                        modifier           = Modifier
                            .size(140.dp)
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint               = Color.White
                            )
                        }

                        Column(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                            horizontalAlignment   = Alignment.CenterHorizontally,
                            verticalArrangement   = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text          = "$departureCode   ➜   $arrivalCode",
                                color         = ResOnHdr,
                                style         = MaterialTheme.typography.headlineMedium,
                                fontWeight    = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text  = selectedDate,
                                color = ResOnHdrMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            // Flight count chip
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color.White.copy(alpha = 0.14f)
                            ) {
                                val label = when (searchState) {
                                    is FlightSearchState.Searching -> "Searching…"
                                    is FlightSearchState.Success   -> "${flights.size} flights found"
                                    is FlightSearchState.Empty     -> "No flights found"
                                    is FlightSearchState.Error     -> "Error"
                                    else                           -> ""
                                }
                                Text(
                                    text       = label,
                                    modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    color      = ResOnHdr,
                                    style      = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            // ── States ──────────────────────────────────────────────────────
            when (val s = searchState) {
                is FlightSearchState.Searching -> item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text  = "Searching for flights…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is FlightSearchState.Error -> item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text      = s.message,
                                style     = MaterialTheme.typography.bodyMedium,
                                color     = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = {
                                viewModel.searchFlights(departureAirportId, arrivalAirportId)
                            }) { Text("Retry") }
                        }
                    }
                }

                is FlightSearchState.Empty -> item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.FlightTakeoff,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier           = Modifier.size(56.dp)
                            )
                            Text(
                                text      = "No flights available for this route.",
                                style     = MaterialTheme.typography.bodyLarge,
                                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                is FlightSearchState.Success -> {
                    itemsIndexed(flights) { idx, flight ->
                        FlightTicketCard(
                            flight        = flight,
                            departureCode = departureCode,
                            arrivalCode   = arrivalCode,
                            airlineIdx    = idx,
                            modifier      = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 14.dp),
                            onSelect      = {
                                onFlightSelected(flight, departureCode, arrivalCode, selectedDate)
                            }
                        )
                    }
                }

                else -> { /* Idle — nothing to show */ }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium airline ticket card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FlightTicketCard(
    flight:        FlightUi,
    departureCode: String,
    arrivalCode:   String,
    airlineIdx:    Int,
    onSelect:      () -> Unit,
    modifier:      Modifier = Modifier
) {
    val airlineColor   = AirlineBgColors[airlineIdx % AirlineBgColors.size]
    val airlineInitial = AirlineInitials[airlineIdx % AirlineInitials.size]
    val airlineName    = AirlineNames[airlineIdx % AirlineNames.size]
    val duration       = flightDuration(flight.departureTime, flight.arrivalTime)
    val seatsLow       = flight.seatsLeft in 1..6

    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        tonalElevation  = 1.dp,
        onClick         = onSelect
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Airline row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape    = CircleShape,
                        color    = airlineColor,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text       = airlineInitial,
                                color      = Color.White,
                                style      = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = airlineName,
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text  = flight.flightNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (seatsLow)
                            Color(0xFFFF6F00).copy(alpha = 0.12f)
                        else
                            Color(0xFF2E7D32).copy(alpha = 0.10f)
                    ) {
                        Text(
                            text       = "${flight.seatsLeft} seats",
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = if (seatsLow) Color(0xFFE65100) else Color(0xFF2E7D32)
                        )
                    }
                }

                // Times row
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = flight.departureTime,
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text  = departureCode,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(
                        modifier              = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment   = Alignment.CenterHorizontally,
                        verticalArrangement   = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text  = duration,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier              = Modifier.fillMaxWidth()
                        ) {
                            CardDashLine(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector        = Icons.Filled.FlightTakeoff,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary,
                                modifier           = Modifier.size(18.dp).padding(horizontal = 2.dp)
                            )
                            CardDashLine(modifier = Modifier.weight(1f))
                        }
                        Text(
                            text  = "Non-stop",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = flight.arrivalTime,
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text  = arrivalCode,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dashed divider
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 18.dp)
            ) {
                val dashW = 6.dp.toPx()
                val gapW  = 5.dp.toPx()
                var x     = 0f
                while (x < size.width) {
                    drawRect(
                        color   = Color.Gray.copy(alpha = 0.22f),
                        topLeft = Offset(x, 0f),
                        size    = Size(minOf(dashW, size.width - x), size.height)
                    )
                    x += dashW + gapW
                }
            }

            // Price + select row
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "per seat",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = formatResultCurrency(flight.pricePerSeat),
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = onSelect,
                    shape   = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text       = "Select  →",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Dashed line for flight path inside card ──────────────────────────────────
@Composable
private fun CardDashLine(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(1.dp)) {
        val dashW = 4.dp.toPx()
        val gapW  = 3.dp.toPx()
        var x     = 0f
        while (x < size.width) {
            drawRect(
                color   = Color.Gray.copy(alpha = 0.35f),
                topLeft = Offset(x, 0f),
                size    = Size(minOf(dashW, size.width - x), 1.dp.toPx())
            )
            x += dashW + gapW
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

/** Parses an ISO-8601 instant or a bare "HH:mm" string to a "HH:mm" display string.
 *  Converts to the device's local timezone so IST users see their local time. */
internal fun parseDisplayTime(raw: String): String {
    return try {
        val ldt = Instant.parse(raw).atZone(ZoneId.systemDefault())
        String.format("%02d:%02d", ldt.hour, ldt.minute)
    } catch (_: Exception) {
        if (raw.length >= 5 && raw[2] == ':') raw.take(5) else raw
    }
}

private fun flightDuration(dep: String, arr: String): String {
    // Try ISO instant parsing first (real API data)
    return try {
        val mins = Duration.between(Instant.parse(dep), Instant.parse(arr)).toMinutes()
        "${mins / 60}h ${mins % 60}m"
    } catch (_: Exception) {
        // Fallback for already-parsed "HH:mm" strings
        try {
            val (dH, dM) = dep.split(":").map { it.toInt() }
            val (aH, aM) = arr.split(":").map { it.toInt() }
            val mins     = (aH * 60 + aM) - (dH * 60 + dM)
            "${mins / 60}h ${mins % 60}m"
        } catch (_: Exception) { "N/A" }
    }
}

private fun formatResultCurrency(amount: Int): String {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${fmt.format(amount)}"
}
