package com.example.airline.ui.screens.booking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Locale

// Hardcoded palette so the hero gradient ignores dynamic color on Android 12+
private val DetailHdrTop     = Color(0xFF071226)
private val DetailHdrMid     = Color(0xFF0B1B3A)
private val DetailHdrBottom  = Color(0xFF1E88E5)
private val DetailOnHdr      = Color(0xFFEAF2FF)
private val DetailOnHdrMuted = Color(0xFF8BAFD4)

@Composable
fun FlightDetailScreen(
    flightId:           Int,
    departureCode:      String,
    arrivalCode:        String,
    selectedDate:       String,
    flightNumber:       String,
    departureTime:      String,
    arrivalTime:        String,
    pricePerSeat:       Int,
    onBack:             () -> Unit,
    onBookingConfirmed: () -> Unit,
    viewModel:          BookingViewModel = hiltViewModel()
) {
    val createState     by viewModel.createState.collectAsState()
    var seats           by remember { mutableIntStateOf(1) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val baseFare  = seats * pricePerSeat
    val taxes     = baseFare * 18 / 100
    val totalFare = baseFare + taxes
    val duration  = flightDurationDetail(departureTime, arrivalTime)

    // React to booking state changes
    LaunchedEffect(createState) {
        when (val s = createState) {
            is BookingCreateState.Success -> {
                showSuccessDialog = true
            }
            is BookingCreateState.Error -> {
                errorMessage = s.message
                showErrorDialog = true
            }
            else -> { /* Loading / Idle — handled by button UI */ }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Force user to tap the button */ },
            icon  = {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = Color(0xFF00C853),
                    modifier           = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text      = "Booking Confirmed!",
                    style     = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text  = {
                Text(
                    text      = "Your booking for $flightNumber is confirmed.\nA confirmation email has been sent.",
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetCreateState()
                        onBookingConfirmed()
                    }
                ) { Text("View My Bookings") }
            }
        )
    }

    // Error dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false; viewModel.resetCreateState() },
            title  = { Text("Booking Failed") },
            text   = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false; viewModel.resetCreateState() }) {
                    Text("OK")
                }
            }
        )
    }

    val isLoading = createState is BookingCreateState.Loading

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Surface(
                modifier        = Modifier.fillMaxWidth(),
                color           = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text  = "Total payable",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text       = formatDetailCurrency(totalFare),
                                style      = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text  = "$seats seat${if (seats > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text  = "incl. taxes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (!isLoading) {
                                viewModel.createBooking(flightId, seats)
                            }
                        },
                        enabled  = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                color       = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text  = "Processing booking…",
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else {
                            Icon(
                                imageVector        = Icons.Filled.FlightTakeoff,
                                contentDescription = null,
                                modifier           = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text       = "Confirm Booking",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero gradient section ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(DetailHdrTop, DetailHdrMid, DetailHdrBottom))
                    )
            ) {
                Icon(
                    imageVector        = Icons.Filled.FlightTakeoff,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.05f),
                    modifier           = Modifier
                        .size(160.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
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
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text          = "$departureCode   ✈   $arrivalCode",
                            color         = DetailOnHdr,
                            style         = MaterialTheme.typography.headlineMedium,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            textAlign     = TextAlign.Center
                        )

                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text       = departureTime,
                                    color      = DetailOnHdr,
                                    style      = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text  = departureCode,
                                    color = DetailOnHdrMuted,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            Column(
                                modifier            = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text  = duration,
                                    color = DetailOnHdrMuted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                HeroFlightLine()
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text       = arrivalTime,
                                    color      = DetailOnHdr,
                                    style      = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text  = arrivalCode,
                                    color = DetailOnHdrMuted,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text     = "$flightNumber  •  $selectedDate",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                color    = DetailOnHdr,
                                style    = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // ── Content cards ───────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SeatSelectorCard(
                    seats        = seats,
                    pricePerSeat = pricePerSeat,
                    onDecrement  = { if (seats > 1) seats-- },
                    onIncrement  = { if (seats < 9) seats++ }
                )

                FareBreakdownCard(
                    seats        = seats,
                    pricePerSeat = pricePerSeat,
                    baseFare     = baseFare,
                    taxes        = taxes,
                    totalFare    = totalFare
                )

                Spacer(Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Seat selector card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeatSelectorCard(
    seats:        Int,
    pricePerSeat: Int,
    onDecrement:  () -> Unit,
    onIncrement:  () -> Unit
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        tonalElevation  = 2.dp
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = "Passengers",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text  = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(pricePerSeat)} per seat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = if (seats > 1)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = LocalIndication.current,
                                onClick           = onDecrement
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "−",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color      = if (seats > 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    Text(
                        text       = seats.toString(),
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        modifier   = Modifier.width(28.dp),
                        textAlign  = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = LocalIndication.current,
                                onClick           = onIncrement
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "+",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Seat count indicator dots
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(9) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = if (i < seats)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Fare breakdown
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FareBreakdownCard(
    seats:        Int,
    pricePerSeat: Int,
    baseFare:     Int,
    taxes:        Int,
    totalFare:    Int
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        tonalElevation  = 2.dp
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text       = "Fare Summary",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            FareRow(
                label = "Base Fare ($seats × ₹${
                    NumberFormat.getNumberInstance(Locale("en", "IN")).format(pricePerSeat)
                })",
                value = formatDetailCurrency(baseFare)
            )

            FareRow(
                label = "Taxes & Fees (18% GST)",
                value = formatDetailCurrency(taxes)
            )

            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                val dashW = 8.dp.toPx()
                val gapW  = 6.dp.toPx()
                var x     = 0f
                while (x < size.width) {
                    drawRect(
                        color   = Color.Gray.copy(alpha = 0.25f),
                        topLeft = Offset(x, 0f),
                        size    = Size(minOf(dashW, size.width - x), size.height * 2)
                    )
                    x += dashW + gapW
                }
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Total",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text       = formatDetailCurrency(totalFare),
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text     = "✓  Includes free cancellation within 24 hours",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ─── Composable helpers ───────────────────────────────────────────────────────

@Composable
private fun FareRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text       = value,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HeroFlightLine() {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier              = Modifier.fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
            val dashW = 4.dp.toPx(); val gapW = 3.dp.toPx(); var x = 0f
            while (x < size.width) {
                drawRect(Color.White.copy(alpha = 0.35f), Offset(x, 0f), Size(minOf(dashW, size.width - x), 1.dp.toPx()))
                x += dashW + gapW
            }
        }
        Icon(Icons.Filled.FlightTakeoff, null, tint = Color.White.copy(alpha = 0.70f),
            modifier = Modifier.padding(horizontal = 6.dp).size(20.dp))
        Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
            val dashW = 4.dp.toPx(); val gapW = 3.dp.toPx(); var x = 0f
            while (x < size.width) {
                drawRect(Color.White.copy(alpha = 0.35f), Offset(x, 0f), Size(minOf(dashW, size.width - x), 1.dp.toPx()))
                x += dashW + gapW
            }
        }
    }
}

private fun flightDurationDetail(dep: String, arr: String): String {
    val (dH, dM) = dep.split(":").map { it.toInt() }
    val (aH, aM) = arr.split(":").map { it.toInt() }
    val mins = (aH * 60 + aM) - (dH * 60 + dM)
    return "${mins / 60}h ${mins % 60}m"
}

private fun formatDetailCurrency(amount: Int): String {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${fmt.format(amount)}"
}
