package com.example.airline.ui.screens.booking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// ─── Airline branding palette (index-matched with static flight list) ─────────
private val AirlineBgColors = listOf(
    Color(0xFF1565C0), // IndiAir  – deep blue
    Color(0xFF00838F), // SkyLink  – teal
    Color(0xFF2E7D32), // BlueStar – forest green
    Color(0xFF6A1B9A), // VeloAir  – purple
    Color(0xFFBF360C), // AirDesh  – deep orange
)
private val AirlineInitials = listOf("IA", "SL", "BS", "VA", "AD")
private val AirlineNames    = listOf("IndiAir", "SkyLink", "BlueStar", "VeloAir", "AirDesh")

// ─── Header palette (immune to dynamic color) ─────────────────────────────────
private val ResHdrTop    = Color(0xFF071226)
private val ResHdrMid    = Color(0xFF0B1B3A)
private val ResHdrBottom = Color(0xFF1565C0)
private val ResOnHdr     = Color(0xFFEAF2FF)
private val ResOnHdrMuted = Color(0xFF8BAFD4)

data class FlightUi(
    val flightNumber:  String,
    val departureTime: String,
    val arrivalTime:   String,
    val pricePerSeat:  Int,
    val seatsLeft:     Int
)

@Composable
fun FlightResultsScreen(
    departureCode:    String,
    arrivalCode:      String,
    selectedDate:     String,
    onBack:           () -> Unit,
    onFlightSelected: (flight: FlightUi, departureCode: String, arrivalCode: String, date: String) -> Unit
) {
    val flights = remember(departureCode, arrivalCode, selectedDate) {
        listOf(
            FlightUi("FL-101", "06:15", "08:35", 5499, 12),
            FlightUi("FL-233", "09:10", "11:30", 6150, 7),
            FlightUi("FL-407", "13:45", "16:00", 5980, 18),
            FlightUi("FL-592", "18:20", "20:40", 7250, 5),
            FlightUi("FL-730", "21:10", "23:25", 6780, 9),
        )
    }

    Scaffold(contentWindowInsets = WindowInsets(0)) { _ ->
        LazyColumn(
            modifier        = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding  = PaddingValues(bottom = 32.dp)
        ) {

            // ── Gradient header ─────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(ResHdrTop, ResHdrMid, ResHdrBottom)
                            )
                        )
                ) {
                    // Watermark plane
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
                        // Back button row
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

                        // Route + metadata
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
                            // "N flights found" chip
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color.White.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text     = "${flights.size} flights found",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    color    = ResOnHdr,
                                    style    = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // ── Flight ticket cards ─────────────────────────────────────────
            item { Spacer(Modifier.height(8.dp)) }

            itemsIndexed(flights) { idx, flight ->
                FlightTicketCard(
                    flight        = flight,
                    departureCode = departureCode,
                    arrivalCode   = arrivalCode,
                    airlineIdx    = idx,
                    modifier      = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom     = 14.dp),
                    onSelect      = {
                        onFlightSelected(flight, departureCode, arrivalCode, selectedDate)
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium airline ticket card
// Layout:
//   ┌────────────────────────────────────────────────────────────────────────┐
//   │  [●] IndiAir  FL-101                            ⬤ 12 seats left        │
//   │  06:15 ─ ─ ─ ─ ✈ ─ ─ ─ ─ 08:35   (2h 20m)                            │
//   ├─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─ ─┤
//   │  ₹5,499 / seat                                      [ Select → ]       │
//   └────────────────────────────────────────────────────────────────────────┘
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
    val seatsLow       = flight.seatsLeft <= 6

    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        tonalElevation  = 1.dp,
        onClick         = onSelect
    ) {
        Column {
            // ── Upper section ──────────────────────────────────────────────
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
                    // Airline logo circle
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

                    // Seats badge
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (seatsLow)
                            Color(0xFFFF6F00).copy(alpha = 0.12f)
                        else
                            Color(0xFF2E7D32).copy(alpha = 0.10f)
                    ) {
                        Text(
                            text     = "${flight.seatsLeft} seats",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color    = if (seatsLow) Color(0xFFE65100) else Color(0xFF2E7D32)
                        )
                    }
                }

                // Times row
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Departure
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

                    // Flight path
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
                                modifier           = Modifier
                                    .size(18.dp)
                                    .padding(horizontal = 2.dp)
                            )
                            CardDashLine(modifier = Modifier.weight(1f))
                        }
                        Text(
                            text  = "Non-stop",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Arrival
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

            // ── Dashed divider ─────────────────────────────────────────────
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

            // ── Price + select row ─────────────────────────────────────────
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

private fun flightDuration(dep: String, arr: String): String {
    val (dH, dM) = dep.split(":").map { it.toInt() }
    val (aH, aM) = arr.split(":").map { it.toInt() }
    val mins     = (aH * 60 + aM) - (dH * 60 + dM)
    return "${mins / 60}h ${mins % 60}m"
}

private fun formatResultCurrency(amount: Int): String {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${fmt.format(amount)}"
}
