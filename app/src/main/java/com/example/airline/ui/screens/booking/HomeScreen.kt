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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Hardcoded airline palette — prevents dynamic color from overriding the header on Android 12+
private val HeaderGradientTop    = Color(0xFF071226)
private val HeaderGradientMid    = Color(0xFF0B1B3A)
private val HeaderGradientBottom = Color(0xFF1A3A6E)
private val HeaderOnColor        = Color(0xFFEAF2FF)
private val HeaderMutedColor     = Color(0xFF8BAFD4)
private val HeaderAccentColor    = Color(0xFF1E88E5)

data class AirportOption(
    val code:        String,
    val displayName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchFlights: (departureCode: String, arrivalCode: String, selectedDate: String) -> Unit,
    outerPadding:    PaddingValues = PaddingValues()
) {
    val airports = remember {
        listOf(
            AirportOption("DEL", "Delhi - Indira Gandhi Intl"),
            AirportOption("BOM", "Mumbai - Chhatrapati Shivaji Intl"),
            AirportOption("BLR", "Bengaluru - Kempegowda Intl"),
            AirportOption("HYD", "Hyderabad - Rajiv Gandhi Intl"),
            AirportOption("CCU", "Kolkata - Netaji Subhas Chandra Bose Intl")
        )
    }

    var departure     by remember { mutableStateOf(airports.first()) }
    var arrival       by remember { mutableStateOf(airports[1]) }
    var selectedDate  by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            // ── Gradient welcome header ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(HeaderGradientTop, HeaderGradientMid, HeaderGradientBottom)
                        )
                    )
            ) {
                // Decorative large plane icon (background watermark)
                Icon(
                    imageVector        = Icons.Filled.FlightTakeoff,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.06f),
                    modifier           = Modifier
                        .size(160.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp, top = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top    = outerPadding.calculateTopPadding() + 28.dp,
                            start  = 28.dp,
                            end    = 28.dp,
                            bottom = 56.dp  // extra space so search card overlaps visually
                        )
                ) {
                    Text(
                        text     = "AIRLINE",
                        color    = HeaderAccentColor,
                        style    = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 5.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text     = "Hello,",
                        color    = HeaderMutedColor,
                        style    = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text     = "Passenger",
                        color    = HeaderOnColor,
                        style    = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text     = "Where would you like to fly today?",
                        color    = HeaderMutedColor,
                        style    = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ── Floating search card ──────────────────────────────────────────
            // Negative top margin via Box offset trick: we use padding(-24dp) on the column
            // instead to pull the card up under the header.
            Surface(
                modifier       = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 0.dp),
                shape          = RoundedCornerShape(28.dp),
                shadowElevation = 16.dp,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text       = "Book a Flight",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = "Select your route and travel date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    // Departure
                    AirportSelectorRow(
                        icon     = Icons.Filled.FlightTakeoff,
                        label    = "From",
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconBg   = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        selected = departure,
                        options  = airports,
                        onSelect = { departure = it }
                    )

                    RowDivider()

                    // Arrival
                    AirportSelectorRow(
                        icon     = Icons.Filled.FlightLand,
                        label    = "To",
                        iconTint = Color(0xFF00897B),
                        iconBg   = Color(0xFF00897B).copy(alpha = 0.10f),
                        selected = arrival,
                        options  = airports.filter { it.code != departure.code },
                        onSelect = { arrival = it }
                    )

                    RowDivider()

                    // Date
                    DateSelectorRow(
                        selectedDate = selectedDate,
                        onClick      = { showDatePicker = true }
                    )

                    Spacer(Modifier.height(20.dp))

                    // Search button
                    Button(
                        onClick  = { onSearchFlights(departure.code, arrival.code, selectedDate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Search,
                            contentDescription = null,
                            modifier           = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = "Search Flights",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Quick tips card ───────────────────────────────────────────────
            Surface(
                modifier       = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                shape          = RoundedCornerShape(20.dp),
                color          = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier            = Modifier.padding(16.dp),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector        = Icons.Filled.FlightTakeoff,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.secondary,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text       = "Pro tip",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text  = "Book at least 3 weeks in advance for the best fares on domestic routes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(outerPadding.calculateBottomPadding() + 24.dp))
        }
    }

    // ── Date picker dialog ────────────────────────────────────────────────────
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton    = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ─── Premium airport selector row ────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AirportSelectorRow(
    icon:     ImageVector,
    label:    String,
    iconTint: Color,
    iconBg:   Color,
    selected: AirportOption,
    options:  List<AirportOption>,
    onSelect: (AirportOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cityName = selected.displayName.substringBefore(" -").trim()

    ExposedDropdownMenuBox(
        expanded        = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Row(
            modifier            = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon pill
            Surface(
                shape    = CircleShape,
                color    = iconBg,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        tint               = iconTint,
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }

            // Label + selected value
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text       = "${selected.code}  ·  $cityName",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector        = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(20.dp)
            )
        }

        ExposedDropdownMenu(
            expanded        = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { airport ->
                DropdownMenuItem(
                    text    = { Text("${airport.code}  ·  ${airport.displayName.substringBefore(" -").trim()}") },
                    onClick = { onSelect(airport); expanded = false }
                )
            }
        }
    }
}

// ─── Premium date selector row ────────────────────────────────────────────────
@Composable
private fun DateSelectorRow(
    selectedDate: String,
    onClick:      () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = LocalIndication.current,
                onClick           = onClick
            ),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape    = CircleShape,
            color    = Color(0xFF6A1B9A).copy(alpha = 0.10f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector        = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint               = Color(0xFF6A1B9A),
                    modifier           = Modifier.size(22.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = "Travel Date",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text       = selectedDate,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        ) {
            Text(
                text     = "Change",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style    = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color    = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ─── Subtle dashed row divider ────────────────────────────────────────────────
@Composable
private fun RowDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp)   // align with the text column, skip the icon
    ) {
        val dashW = 6.dp.toPx()
        val gapW  = 5.dp.toPx()
        val color = Color.Gray.copy(alpha = 0.20f)
        var x     = 0f
        while (x < size.width) {
            drawRect(
                color   = color,
                topLeft = Offset(x, 0f),
                size    = Size(minOf(dashW, size.width - x), size.height)
            )
            x += dashW + gapW
        }
    }
}
