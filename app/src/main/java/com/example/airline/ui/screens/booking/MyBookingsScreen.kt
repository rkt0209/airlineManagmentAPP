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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Hardcoded palette so boarding-pass gradient is immune to dynamic color
private val PassHeaderStart  = Color(0xFF071226)
private val PassHeaderEnd    = Color(0xFF1565C0)
private val PassOnHeader     = Color(0xFFEAF2FF)
private val PassOnHeaderMuted = Color(0xFF8BAFD4)

data class BookingUi(
    val bookingId:   String,
    val route:       String,
    val flightNumber: String,
    val seatsBooked: Int,
    val totalCost:   String,
    val status:      String
)

@Composable
fun MyBookingsScreen(
    onBackHome:   () -> Unit,
    outerPadding: PaddingValues = PaddingValues()
) {
    val bookings = remember {
        listOf(
            BookingUi("BKG-98765", "DEL to BOM", "FL-101", 2, "₹10,998", "Confirmed"),
            BookingUi("BKG-98112", "BLR to HYD", "FL-407", 1, "₹5,980",  "Confirmed"),
            BookingUi("BKG-97240", "CCU to DEL", "FL-233", 3, "₹18,450", "Confirmed"),
            BookingUi("BKG-96318", "BOM to BLR", "FL-592", 1, "₹7,250",  "Confirmed")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = outerPadding.calculateBottomPadding() + 16.dp
            )
        ) {
            // ── Page header ───────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(PassHeaderStart, Color(0xFF0D2247), Color(0xFF0B2A5A))
                            )
                        )
                        .padding(
                            top    = outerPadding.calculateTopPadding() + 28.dp,
                            start  = 28.dp,
                            end    = 28.dp,
                            bottom = 28.dp
                        )
                ) {
                    Column {
                        Text(
                            text          = "MY BOOKINGS",
                            color         = Color(0xFF1E88E5),
                            style         = MaterialTheme.typography.labelLarge,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 5.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text       = "Your Trips",
                            color      = PassOnHeader,
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${bookings.size} upcoming bookings",
                            color = PassOnHeaderMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // ── Boarding pass cards ───────────────────────────────────────────
            item { Spacer(Modifier.height(16.dp)) }

            items(bookings) { booking ->
                BoardingPassCard(
                    booking           = booking,
                    screenBackground  = MaterialTheme.colorScheme.background,
                    modifier          = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )
            }

            // ── Book new flight button ────────────────────────────────────────
            item {
                Button(
                    onClick  = onBackHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape    = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text       = "Search New Flight",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Boarding pass card
// Design:
//   ┌─────────────────────────────────────────────────────────────┐
//   │  [Gradient navy header]  DEL ──✈──  BOM   FL-101  [chip]   │
//   ├──○ ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─ ○──│  ← tearline
//   │  Booking ID    Seats    Total Paid                          │
//   │  ▌▌ ▌▌▌ ▌ ▌▌▌ ▌ ▌▌▌▌ ▌ ▌▌ …  (barcode)                   │
//   └─────────────────────────────────────────────────────────────┘
// The half-circles at the tearline are circles whose centers sit on the left/right
// edges of the card. Because Surface clips to RoundedCornerShape(20dp) and the
// circles are at the straight mid-card edges (not corners), exactly half of each
// circle is visible — creating the classic boarding-pass notch.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BoardingPassCard(
    booking:          BookingUi,
    screenBackground: Color,
    modifier:         Modifier = Modifier
) {
    val parts    = booking.route.split(" to ")
    val fromCode = parts.getOrElse(0) { "---" }
    val toCode   = parts.getOrElse(1) { "---" }

    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        shadowElevation = 10.dp,
        tonalElevation  = 2.dp
    ) {
        Column {
            // ── Gradient header ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(PassHeaderStart, PassHeaderEnd)),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Route row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Origin
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = fromCode,
                                style      = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text  = cityNameFor(fromCode),
                                style = MaterialTheme.typography.labelMedium,
                                color = PassOnHeaderMuted
                            )
                            Text(
                                text  = "Departure",
                                style = MaterialTheme.typography.labelSmall,
                                color = PassOnHeaderMuted.copy(alpha = 0.6f)
                            )
                        }

                        // Flight path: dashes + plane icon + dashes
                        Row(
                            modifier          = Modifier.weight(1f).padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FlightDashLine(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector        = Icons.Filled.FlightTakeoff,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.85f),
                                modifier           = Modifier
                                    .size(22.dp)
                                    .padding(horizontal = 2.dp)
                            )
                            FlightDashLine(modifier = Modifier.weight(1f))
                        }

                        // Destination
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = toCode,
                                style      = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text  = cityNameFor(toCode),
                                style = MaterialTheme.typography.labelMedium,
                                color = PassOnHeaderMuted
                            )
                            Text(
                                text  = "Arrival",
                                style = MaterialTheme.typography.labelSmall,
                                color = PassOnHeaderMuted.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Sub-row: flight number + status chip
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = booking.flightNumber,
                            style = MaterialTheme.typography.labelLarge,
                            color = PassOnHeaderMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        BoardingStatusChip(status = booking.status)
                    }
                }
            }

            // ── Tear-off divider ──────────────────────────────────────────────
            // Box height is 24dp; circles (24dp diameter) are centered on the
            // left/right edges — Surface clips them to half-circles.
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
            ) {
                // Left notch circle
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = (-12).dp)
                        .background(screenBackground, CircleShape)
                        .align(Alignment.CenterStart)
                )
                // Dashed divider line
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                ) {
                    val dashW = 8.dp.toPx()
                    val gapW  = 6.dp.toPx()
                    var x     = 0f
                    while (x < size.width) {
                        drawRect(
                            color   = Color.Gray.copy(alpha = 0.30f),
                            topLeft = Offset(x, 0f),
                            size    = Size(minOf(dashW, size.width - x), size.height * 2)
                        )
                        x += dashW + gapW
                    }
                }
                // Right notch circle
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 12.dp)
                        .background(screenBackground, CircleShape)
                        .align(Alignment.CenterEnd)
                )
            }

            // ── Bottom section: info + barcode ────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info grid
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PassInfoItem(label = "Booking ID",  value = booking.bookingId)
                    PassInfoItem(label = "Seats",       value = booking.seatsBooked.toString())
                    PassInfoItem(label = "Total Paid",  value = booking.totalCost)
                }

                // Barcode placeholder
                BarcodeStrip(bookingId = booking.bookingId)
            }
        }
    }
}

// ─── Composable helpers ───────────────────────────────────────────────────────

@Composable
private fun BoardingStatusChip(status: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFF00C853).copy(alpha = 0.18f)
    ) {
        Text(
            text       = "● $status",
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF00C853)
        )
    }
}

@Composable
private fun PassInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Horizontal dashed line drawn with Canvas — used inside the route header. */
@Composable
private fun FlightDashLine(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.height(1.dp)
    ) {
        val dashW = 4.dp.toPx()
        val gapW  = 3.dp.toPx()
        var x     = 0f
        while (x < size.width) {
            drawRect(
                color   = Color.White.copy(alpha = 0.40f),
                topLeft = Offset(x, 0f),
                size    = Size(minOf(dashW, size.width - x), 1.dp.toPx())
            )
            x += dashW + gapW
        }
    }
}

/** Simulated barcode strip — a deterministic sequence of bar widths derived from the booking ID. */
@Composable
private fun BarcodeStrip(bookingId: String) {
    val barColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)

    // Deterministic bar pattern seeded by bookingId hash so each pass looks unique
    val seed    = bookingId.hashCode()
    val pattern = (0 until 40).map { i ->
        (((seed ushr (i * 3)) and 0x07) + 1).coerceIn(1, 4)
    }

    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            val unitPx = 2.dp.toPx()
            var x      = 0f
            pattern.forEachIndexed { idx, w ->
                val barW = w * unitPx
                if (idx % 2 == 0) {   // even index = bar, odd = gap
                    drawRect(
                        color   = barColor,
                        topLeft = Offset(x, 0f),
                        size    = Size(barW, size.height)
                    )
                }
                x += barW
                if (x >= size.width) return@forEachIndexed
            }
        }

        Text(
            text      = bookingId,
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 3.sp
        )
    }
}

/** Maps IATA code → city short-name for the boarding pass display. */
private fun cityNameFor(code: String) = when (code) {
    "DEL" -> "Delhi"
    "BOM" -> "Mumbai"
    "BLR" -> "Bengaluru"
    "HYD" -> "Hyderabad"
    "CCU" -> "Kolkata"
    else  -> code
}
