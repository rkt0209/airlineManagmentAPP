package com.example.airline.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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

// Airport option: display name → city label
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
            AirplaneOption("Boeing 777", 400),
            AirplaneOption("Airbus A320", 350),
            AirplaneOption("Boeing 747", 320),
            AirplaneOption("Boeing 77", 300),
            AirplaneOption("Airbus 330", 150)
        )
    }

    // Mirrors the Airports seeder data
    val airportOptions = remember {
        listOf(
            AirportOption("Indira Gandhi Intl (DEL)", "DEL"),
            AirportOption("Chhatrapati Shivaji Intl (BOM)", "BOM"),
            AirportOption("Kempegowda Intl (BLR)", "BLR"),
            AirportOption("Rajiv Gandhi Intl (HYD)", "HYD"),
            AirportOption("Netaji Subhas Chandra Bose Intl (CCU)", "CCU")
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
    var showAddDialog by remember { mutableStateOf(false) }
    var newFlightNumber by remember { mutableStateOf("") }
    var selectedAirplane by remember { mutableStateOf(airplaneOptions.first()) }
    var airplaneExpanded by remember { mutableStateOf(false) }
    var selectedDeparture by remember { mutableStateOf(airportOptions.first()) }
    var departureExpanded by remember { mutableStateOf(false) }
    var selectedArrival by remember { mutableStateOf(airportOptions[1]) }
    var arrivalExpanded by remember { mutableStateOf(false) }
    var newDepartureTime by remember { mutableStateOf("") }
    var newArrivalTime by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var newBoardingGate by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Manage Flights") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Flight")
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flights) { flight ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Header row: flight number + actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = flight.flightNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row {
                                    IconButton(onClick = {
                                        Toast.makeText(context, "Edit ${flight.flightNumber}", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Edit Flight",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = {
                                        Toast.makeText(context, "Delete ${flight.flightNumber}", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete Flight",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            // Route: departureAirportId → arrivalAirportId
                            Text(
                                text = "${flight.departureAirport}  →  ${flight.arrivalAirport}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            // Times: departureTime → arrivalTime
                            Text(
                                text = "Dep: ${flight.departureTime}   Arr: ${flight.arrivalTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Detail chips row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // price
                                Text(
                                    text = "₹${"%.0f".format(flight.price.toDouble())}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "· ${flight.totalSeats} seats",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (flight.boardingGate.isNotBlank()) {
                                    Text(
                                        text = "· Gate ${flight.boardingGate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "· ${flight.airplaneModel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Flight Dialog — fields mirror the flight-controller.js create body exactly
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
                newPrice = ""; newBoardingGate = ""
                selectedAirplane = airplaneOptions.first()
                selectedDeparture = airportOptions.first(); selectedArrival = airportOptions[1]
                showAddDialog = false
            },
            title = { Text("Add New Flight") },
            text = {
                // Scrollable because there are many required fields
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // flightNumber — required, unique
                    OutlinedTextField(
                        value = newFlightNumber,
                        onValueChange = { newFlightNumber = it },
                        label = { Text("Flight Number") },
                        placeholder = { Text("e.g. FL-601") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // airplaneId — required FK, dropdown from Airplanes table
                    ExposedDropdownMenuBox(
                        expanded = airplaneExpanded,
                        onExpandedChange = { airplaneExpanded = !airplaneExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = "${selectedAirplane.modelNumber} (${selectedAirplane.capacity} seats)",
                            onValueChange = {},
                            label = { Text("Airplane (airplaneId)") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = airplaneExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = airplaneExpanded,
                            onDismissRequest = { airplaneExpanded = false }
                        ) {
                            airplaneOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text("${opt.modelNumber} · ${opt.capacity} seats") },
                                    onClick = { selectedAirplane = opt; airplaneExpanded = false }
                                )
                            }
                        }
                    }

                    // departureAirportId — required FK, dropdown from Airports table
                    ExposedDropdownMenuBox(
                        expanded = departureExpanded,
                        onExpandedChange = { departureExpanded = !departureExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = selectedDeparture.displayName,
                            onValueChange = {},
                            label = { Text("Departure Airport (departureAirportId)") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = departureExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = departureExpanded,
                            onDismissRequest = { departureExpanded = false }
                        ) {
                            airportOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.displayName) },
                                    onClick = { selectedDeparture = opt; departureExpanded = false }
                                )
                            }
                        }
                    }

                    // arrivalAirportId — required FK, dropdown from Airports table
                    ExposedDropdownMenuBox(
                        expanded = arrivalExpanded,
                        onExpandedChange = { arrivalExpanded = !arrivalExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = selectedArrival.displayName,
                            onValueChange = {},
                            label = { Text("Arrival Airport (arrivalAirportId)") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = arrivalExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = arrivalExpanded,
                            onDismissRequest = { arrivalExpanded = false }
                        ) {
                            airportOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.displayName) },
                                    onClick = { selectedArrival = opt; arrivalExpanded = false }
                                )
                            }
                        }
                    }

                    // departureTime — required DATE field
                    OutlinedTextField(
                        value = newDepartureTime,
                        onValueChange = { newDepartureTime = it },
                        label = { Text("Departure Time (departureTime)") },
                        placeholder = { Text("yyyy-MM-dd HH:mm") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // arrivalTime — required DATE field
                    OutlinedTextField(
                        value = newArrivalTime,
                        onValueChange = { newArrivalTime = it },
                        label = { Text("Arrival Time (arrivalTime)") },
                        placeholder = { Text("yyyy-MM-dd HH:mm") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // price — required INTEGER (in rupees)
                    OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Price ₹ (price)") },
                        placeholder = { Text("e.g. 4999") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // boardingGate — optional STRING
                    OutlinedTextField(
                        value = newBoardingGate,
                        onValueChange = { newBoardingGate = it },
                        label = { Text("Boarding Gate (optional)") },
                        placeholder = { Text("e.g. A3") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // totalSeats — auto-derived from selected airplane capacity
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Total Seats (totalSeats): ${selectedAirplane.capacity}  — auto-set from airplane",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val number = newFlightNumber.trim()
                    val price = newPrice.toIntOrNull() ?: 0
                    if (number.isNotEmpty() && newDepartureTime.isNotBlank()
                        && newArrivalTime.isNotBlank() && price > 0
                    ) {
                        flights.add(
                            AdminFlightUi(
                                id = flights.size + 1,
                                flightNumber = number,
                                airplaneModel = selectedAirplane.modelNumber,
                                departureAirport = selectedDeparture.displayName,
                                arrivalAirport = selectedArrival.displayName,
                                departureTime = newDepartureTime,
                                arrivalTime = newArrivalTime,
                                price = price,
                                boardingGate = newBoardingGate.trim(),
                                totalSeats = selectedAirplane.capacity
                            )
                        )
                        Toast.makeText(context, "Flight ${number} added", Toast.LENGTH_SHORT).show()
                    }
                    newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
                    newPrice = ""; newBoardingGate = ""
                    selectedAirplane = airplaneOptions.first()
                    selectedDeparture = airportOptions.first(); selectedArrival = airportOptions[1]
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
                    newPrice = ""; newBoardingGate = ""
                    selectedAirplane = airplaneOptions.first()
                    selectedDeparture = airportOptions.first(); selectedArrival = airportOptions[1]
                    showAddDialog = false
                }) { Text("Cancel") }
            }
        )
    }
}
