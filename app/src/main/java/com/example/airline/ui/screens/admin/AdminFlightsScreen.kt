package com.example.airline.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// Mirrors the Flights Sequelize model exactly:
//   flightNumber (STRING, unique, not null)
//   airplaneId   (INTEGER, not null)  → shown as airplaneModel for readability
//   departureAirportId (INTEGER, not null) → shown as airport name
//   arrivalAirportId   (INTEGER, not null) → shown as airport name
//   departureTime (DATE, not null)
//   arrivalTime   (DATE, not null)
//   price         (INTEGER, not null)
//   boardingGate  (STRING, optional)
//   totalSeats    (INTEGER, not null) → auto-derived from airplane.capacity
data class AdminFlightUi(
    val id: Int,
    val flightNumber: String,
    val airplaneModel: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Int,
    val boardingGate: String,
    val totalSeats: Int
)

// Airplane option: model name → capacity (used to auto-set totalSeats)
private data class AirplaneOption(val modelNumber: String, val capacity: Int)

// Airport option: display name → IATA code
private data class AirportOption(val displayName: String, val code: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlightsScreen(
    outerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    // Mirrors the Airplanes seeder data — model number + capacity
    val airplaneOptions = remember {
        listOf(
            AirplaneOption("Boeing 777",  400),
            AirplaneOption("Airbus A320", 350),
            AirplaneOption("Boeing 747",  320),
            AirplaneOption("Boeing 77",   300),
            AirplaneOption("Airbus 330",  150)
        )
    }

    // Mirrors the Airports seeder data
    val airportOptions = remember {
        listOf(
            AirportOption("Indira Gandhi Intl (DEL)",                  "DEL"),
            AirportOption("Chhatrapati Shivaji Intl (BOM)",            "BOM"),
            AirportOption("Kempegowda Intl (BLR)",                     "BLR"),
            AirportOption("Rajiv Gandhi Intl (HYD)",                   "HYD"),
            AirportOption("Netaji Subhas Chandra Bose Intl (CCU)",     "CCU")
        )
    }

    // Mock flights — all fields match the Flights migration columns
    val flights = remember {
        mutableStateListOf(
            AdminFlightUi(1, "FL-101", "Boeing 777",
                "Indira Gandhi Intl (DEL)", "Chhatrapati Shivaji Intl (BOM)",
                "2024-01-15 06:00", "2024-01-15 08:15", 4999, "A3", 400),
            AdminFlightUi(2, "FL-202", "Airbus A320",
                "Chhatrapati Shivaji Intl (BOM)", "Kempegowda Intl (BLR)",
                "2024-01-15 09:30", "2024-01-15 11:00", 3500, "B1", 350),
            AdminFlightUi(3, "FL-303", "Boeing 747",
                "Rajiv Gandhi Intl (HYD)", "Indira Gandhi Intl (DEL)",
                "2024-01-15 12:00", "2024-01-15 14:30", 5200, "C2", 320),
            AdminFlightUi(4, "FL-404", "Airbus 330",
                "Kempegowda Intl (BLR)", "Netaji Subhas Chandra Bose Intl (CCU)",
                "2024-01-15 07:15", "2024-01-15 10:45", 6100, "D4", 150),
            AdminFlightUi(5, "FL-505", "Boeing 77",
                "Netaji Subhas Chandra Bose Intl (CCU)", "Rajiv Gandhi Intl (HYD)",
                "2024-01-15 15:00", "2024-01-15 17:30", 4200, "A7", 300)
        )
    }

    // Add-dialog field states
    var showAddDialog      by remember { mutableStateOf(false) }
    var newFlightNumber    by remember { mutableStateOf("") }
    var selectedAirplane   by remember { mutableStateOf(airplaneOptions.first()) }
    var airplaneExpanded   by remember { mutableStateOf(false) }
    var selectedDeparture  by remember { mutableStateOf(airportOptions.first()) }
    var departureExpanded  by remember { mutableStateOf(false) }
    var selectedArrival    by remember { mutableStateOf(airportOptions[1]) }
    var arrivalExpanded    by remember { mutableStateOf(false) }
    var newDepartureTime   by remember { mutableStateOf("") }
    var newArrivalTime     by remember { mutableStateOf("") }
    var newPrice           by remember { mutableStateOf("") }
    var newBoardingGate    by remember { mutableStateOf("") }
    var flightNumberError  by remember { mutableStateOf(false) }

    Scaffold(
        modifier            = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Manage Flights",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${flights.size} flights scheduled",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector        = Icons.Filled.Add,
                    contentDescription = "Add Flight",
                    tint               = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flights) { flight ->
                    FlightDashboardCard(
                        flight   = flight,
                        onEdit   = {
                            Toast.makeText(context, "Edit ${flight.flightNumber}", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            Toast.makeText(context, "Delete ${flight.flightNumber}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // ── Add Flight Dialog ──────────────────────────────────────────────────────
    if (showAddDialog) {
        val resetDialog: () -> Unit = {
            newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
            newPrice = ""; newBoardingGate = ""; flightNumberError = false
            selectedAirplane  = airplaneOptions.first()
            selectedDeparture = airportOptions.first()
            selectedArrival   = airportOptions[1]
            showAddDialog     = false
        }

        AlertDialog(
            onDismissRequest = resetDialog,
            shape            = RoundedCornerShape(24.dp),
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape    = CircleShape,
                            color    = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector        = Icons.Filled.FlightTakeoff,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text       = "Add New Flight",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text  = "Fields marked * are required",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier            = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── FLIGHT INFO ─────────────────────────────────────────
                    FlightFormSectionLabel("FLIGHT INFO")

                    // flightNumber — required, unique
                    OutlinedTextField(
                        value         = newFlightNumber,
                        onValueChange = { newFlightNumber = it; flightNumberError = false },
                        label         = { Text("Flight Number *") },
                        placeholder   = { Text("e.g. FL-601") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                tint = if (flightNumberError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        },
                        isError        = flightNumberError,
                        supportingText = if (flightNumberError) {
                            { Text("Flight number cannot be empty") }
                        } else null,
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    // ── AIRCRAFT ────────────────────────────────────────────
                    FlightFormSectionLabel("AIRCRAFT")

                    // airplaneId — required FK dropdown from Airplanes table
                    ExposedDropdownMenuBox(
                        expanded        = airplaneExpanded,
                        onExpandedChange = { airplaneExpanded = !airplaneExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = "${selectedAirplane.modelNumber} (${selectedAirplane.capacity} seats)",
                            onValueChange = {},
                            label         = { Text("Airplane *") },
                            leadingIcon   = {
                                Icon(
                                    Icons.Filled.AirplanemodeActive,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon  = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = airplaneExpanded)
                            },
                            shape         = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded        = airplaneExpanded,
                            onDismissRequest = { airplaneExpanded = false }
                        ) {
                            airplaneOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text("${opt.modelNumber} · ${opt.capacity} seats") },
                                    onClick = { selectedAirplane = opt; airplaneExpanded = false }
                                )
                            }
                        }
                    }

                    // totalSeats info chip — auto-derived from airplane capacity
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text     = "ℹ  totalSeats auto-set to ${selectedAirplane.capacity} from selected airplane",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // ── ROUTE ───────────────────────────────────────────────
                    FlightFormSectionLabel("ROUTE")

                    // departureAirportId — required FK dropdown
                    ExposedDropdownMenuBox(
                        expanded        = departureExpanded,
                        onExpandedChange = { departureExpanded = !departureExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedDeparture.displayName,
                            onValueChange = {},
                            label         = { Text("Departure Airport *") },
                            leadingIcon   = {
                                Icon(
                                    Icons.Filled.FlightTakeoff,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon  = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = departureExpanded)
                            },
                            shape         = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded        = departureExpanded,
                            onDismissRequest = { departureExpanded = false }
                        ) {
                            airportOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text(opt.displayName) },
                                    onClick = { selectedDeparture = opt; departureExpanded = false }
                                )
                            }
                        }
                    }

                    // arrivalAirportId — required FK dropdown
                    ExposedDropdownMenuBox(
                        expanded        = arrivalExpanded,
                        onExpandedChange = { arrivalExpanded = !arrivalExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedArrival.displayName,
                            onValueChange = {},
                            label         = { Text("Arrival Airport *") },
                            leadingIcon   = {
                                Icon(
                                    Icons.Filled.FlightLand,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon  = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = arrivalExpanded)
                            },
                            shape         = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded        = arrivalExpanded,
                            onDismissRequest = { arrivalExpanded = false }
                        ) {
                            airportOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text(opt.displayName) },
                                    onClick = { selectedArrival = opt; arrivalExpanded = false }
                                )
                            }
                        }
                    }

                    // ── SCHEDULE ────────────────────────────────────────────
                    FlightFormSectionLabel("SCHEDULE")

                    // departureTime — required DATE field
                    OutlinedTextField(
                        value         = newDepartureTime,
                        onValueChange = { newDepartureTime = it },
                        label         = { Text("Departure Time *") },
                        placeholder   = { Text("yyyy-MM-dd HH:mm") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    // arrivalTime — required DATE field
                    OutlinedTextField(
                        value         = newArrivalTime,
                        onValueChange = { newArrivalTime = it },
                        label         = { Text("Arrival Time *") },
                        placeholder   = { Text("yyyy-MM-dd HH:mm") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    // ── PRICING & GATE ──────────────────────────────────────
                    FlightFormSectionLabel("PRICING & GATE")

                    // price — required INTEGER (in rupees)
                    OutlinedTextField(
                        value         = newPrice,
                        onValueChange = { newPrice = it.filter { c -> c.isDigit() } },
                        label         = { Text("Price ₹ *") },
                        placeholder   = { Text("e.g. 4999") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Payments,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine      = true,
                        modifier        = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape           = RoundedCornerShape(14.dp)
                    )

                    // boardingGate — optional STRING
                    OutlinedTextField(
                        value         = newBoardingGate,
                        onValueChange = { newBoardingGate = it },
                        label         = { Text("Boarding Gate (optional)") },
                        placeholder   = { Text("e.g. A3") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.MeetingRoom,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val number = newFlightNumber.trim()
                        if (number.isEmpty()) {
                            flightNumberError = true
                            return@TextButton
                        }
                        val price = newPrice.toIntOrNull() ?: 0
                        if (newDepartureTime.isNotBlank() && newArrivalTime.isNotBlank() && price > 0) {
                            flights.add(
                                AdminFlightUi(
                                    id               = flights.size + 1,
                                    flightNumber     = number,
                                    airplaneModel    = selectedAirplane.modelNumber,
                                    departureAirport = selectedDeparture.displayName,
                                    arrivalAirport   = selectedArrival.displayName,
                                    departureTime    = newDepartureTime,
                                    arrivalTime      = newArrivalTime,
                                    price            = price,
                                    boardingGate     = newBoardingGate.trim(),
                                    totalSeats       = selectedAirplane.capacity
                                )
                            )
                            Toast.makeText(context, "Flight $number added", Toast.LENGTH_SHORT).show()
                        }
                        resetDialog()
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = resetDialog) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium flight dashboard card
// Layout:
//   ┌─────────────────────────────────────────────────────────────────────────┐
//   │  [FL-101]  Boeing 777                              [Gate A3]            │
//   │  DEL   ─ ─ ─ ✈ ─ ─ ─   BOM                                            │
//   │  06:00                  08:15                                           │
//   ├─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─  ─┤
//   │  ₹4,999 / seat                    400 seats    [✏]  [🗑]               │
//   └─────────────────────────────────────────────────────────────────────────┘
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FlightDashboardCard(
    flight:   AdminFlightUi,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    val depCode = extractCode(flight.departureAirport)
    val arrCode = extractCode(flight.arrivalAirport)
    val depTime = extractTime(flight.departureTime)
    val arrTime = extractTime(flight.arrivalTime)

    Surface(
        shape           = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
        tonalElevation  = 1.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Header: flight number pill + airplane model + gate badge ──
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text       = flight.flightNumber,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text  = flight.airplaneModel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (flight.boardingGate.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text       = "Gate ${flight.boardingGate}",
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // ── Route: DEL ─── ✈ ─── BOM with times ──
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure code + time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = depCode,
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = depTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Flight path line
                Row(
                    modifier          = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminDashLine(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector        = Icons.Filled.FlightTakeoff,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(16.dp)
                    )
                    AdminDashLine(modifier = Modifier.weight(1f))
                }

                // Arrival code + time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = arrCode,
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = arrTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Dashed divider ──
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
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

            // ── Bottom: price + seats pill + edit/delete ──
            Row(
                modifier              = Modifier.fillMaxWidth(),
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
                        text       = formatFlightCurrency(flight.price),
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text       = "${flight.totalSeats} seats",
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector        = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint               = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector        = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint               = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// ─── Dashed line for flight path inside card ──────────────────────────────────
@Composable
private fun AdminDashLine(modifier: Modifier = Modifier) {
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

// ─── Section label for dialog form ───────────────────────────────────────────
@Composable
private fun FlightFormSectionLabel(text: String) {
    Text(
        text          = text,
        style         = MaterialTheme.typography.labelSmall,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.5.sp
    )
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

/** Extracts IATA code from "Airport Name (XXX)" → "XXX" */
private fun extractCode(airportName: String): String =
    airportName.substringAfterLast("(").trimEnd(')')

/** Extracts time portion from "yyyy-MM-dd HH:mm" → "HH:mm" */
private fun extractTime(dateTime: String): String =
    if (dateTime.contains(" ")) dateTime.substringAfter(" ") else dateTime

private fun formatFlightCurrency(amount: Int): String {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${fmt.format(amount)}"
}
