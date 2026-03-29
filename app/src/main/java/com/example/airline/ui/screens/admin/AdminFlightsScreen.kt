package com.example.airline.ui.screens.admin

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.airline.data.remote.AirplaneItem
import com.example.airline.data.remote.AirportItem
import com.example.airline.data.remote.FlightItem
import java.text.NumberFormat
import java.util.Locale

// ─── UI display model ─────────────────────────────────────────────────────────
// Holds resolved names for display (IDs resolved against airports/airplanes lists)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlightsScreen(
    outerPadding: PaddingValues = PaddingValues(),
    viewModel: AdminFlightsViewModel = hiltViewModel()
) {
    val flightItems   by viewModel.flights.collectAsState()
    val airportItems  by viewModel.airports.collectAsState()
    val airplaneItems by viewModel.airplanes.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    val errorMsg      by viewModel.errorMessage.collectAsState()

    // Resolve IDs to display names for each flight
    val flights = remember(flightItems, airportItems, airplaneItems) {
        flightItems.map { f ->
            AdminFlightUi(
                id               = f.id,
                flightNumber     = f.flightNumber,
                airplaneModel    = airplaneItems.find { it.id == f.airplaneId }?.modelNumber
                    ?: "#${f.airplaneId}",
                departureAirport = airportItems.find { it.id == f.departureAirportId }?.name
                    ?: "#${f.departureAirportId}",
                arrivalAirport   = airportItems.find { it.id == f.arrivalAirportId }?.name
                    ?: "#${f.arrivalAirportId}",
                departureTime    = f.departureTime,
                arrivalTime      = f.arrivalTime,
                price            = f.price,
                boardingGate     = f.boardingGate ?: "",
                totalSeats       = f.totalSeats
            )
        }
    }

    // Add / Edit dialog state
    var showDialog          by remember { mutableStateOf(false) }
    var editingFlightId     by remember { mutableStateOf<Int?>(null) }
    var newFlightNumber     by remember { mutableStateOf("") }
    var selectedAirplaneId  by remember { mutableStateOf<Int?>(null) }
    var airplaneExpanded    by remember { mutableStateOf(false) }
    var selectedDepartureId by remember { mutableStateOf<Int?>(null) }
    var departureExpanded   by remember { mutableStateOf(false) }
    var selectedArrivalId   by remember { mutableStateOf<Int?>(null) }
    var arrivalExpanded     by remember { mutableStateOf(false) }
    var newDepartureTime    by remember { mutableStateOf("") }
    var newArrivalTime      by remember { mutableStateOf("") }
    var newPrice            by remember { mutableStateOf("") }
    var newBoardingGate     by remember { mutableStateOf("") }
    var flightNumberError   by remember { mutableStateOf(false) }

    // Resolve selected items (default to first in list)
    val selectedAirplane  = airplaneItems.find { it.id == selectedAirplaneId }
        ?: airplaneItems.firstOrNull()
    val selectedDeparture = airportItems.find { it.id == selectedDepartureId }
        ?: airportItems.firstOrNull()
    val selectedArrival   = airportItems.find { it.id == selectedArrivalId }
        ?: airportItems.drop(1).firstOrNull() ?: airportItems.firstOrNull()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier            = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor   = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
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
                onClick        = {
                    editingFlightId = null
                    newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
                    newPrice = ""; newBoardingGate = ""; flightNumberError = false
                    selectedAirplaneId = null; selectedDepartureId = null; selectedArrivalId = null
                    showDialog = true
                },
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
            if (isLoading && flights.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flights, key = { it.id }) { flight ->
                        FlightDashboardCard(
                            flight   = flight,
                            onEdit   = {
                                val original = flightItems.find { it.id == flight.id }
                                    ?: return@FlightDashboardCard
                                editingFlightId     = original.id
                                newFlightNumber     = original.flightNumber
                                selectedAirplaneId  = original.airplaneId
                                selectedDepartureId = original.departureAirportId
                                selectedArrivalId   = original.arrivalAirportId
                                newDepartureTime    = original.departureTime
                                newArrivalTime      = original.arrivalTime
                                newPrice            = original.price.toString()
                                newBoardingGate     = original.boardingGate ?: ""
                                flightNumberError   = false
                                showDialog          = true
                            },
                            onDelete = { viewModel.deleteFlight(flight.id) }
                        )
                    }
                }
            }
        }
    }

    // ── Add / Edit Flight Dialog ───────────────────────────────────────────────
    if (showDialog) {
        val isEditing = editingFlightId != null
        val resetDialog: () -> Unit = {
            newFlightNumber = ""; newDepartureTime = ""; newArrivalTime = ""
            newPrice = ""; newBoardingGate = ""; flightNumberError = false
            selectedAirplaneId  = null
            selectedDepartureId = null
            selectedArrivalId   = null
            editingFlightId     = null
            showDialog          = false
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
                            text       = if (isEditing) "Edit Flight" else "Add New Flight",
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

                    ExposedDropdownMenuBox(
                        expanded         = airplaneExpanded,
                        onExpandedChange = { airplaneExpanded = !airplaneExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedAirplane
                                ?.let { "${it.modelNumber} (${it.capacity} seats)" }
                                ?: if (airplaneItems.isEmpty()) "Loading…" else "Select airplane",
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
                            expanded         = airplaneExpanded,
                            onDismissRequest = { airplaneExpanded = false }
                        ) {
                            airplaneItems.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text("${opt.modelNumber} · ${opt.capacity} seats") },
                                    onClick = { selectedAirplaneId = opt.id; airplaneExpanded = false }
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text     = "ℹ  totalSeats auto-set to ${selectedAirplane?.capacity ?: "—"} from selected airplane",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // ── ROUTE ───────────────────────────────────────────────
                    FlightFormSectionLabel("ROUTE")

                    ExposedDropdownMenuBox(
                        expanded         = departureExpanded,
                        onExpandedChange = { departureExpanded = !departureExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedDeparture?.name
                                ?: if (airportItems.isEmpty()) "Loading…" else "Select airport",
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
                            expanded         = departureExpanded,
                            onDismissRequest = { departureExpanded = false }
                        ) {
                            airportItems.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text(opt.name) },
                                    onClick = { selectedDepartureId = opt.id; departureExpanded = false }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded         = arrivalExpanded,
                        onExpandedChange = { arrivalExpanded = !arrivalExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedArrival?.name
                                ?: if (airportItems.isEmpty()) "Loading…" else "Select airport",
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
                            expanded         = arrivalExpanded,
                            onDismissRequest = { arrivalExpanded = false }
                        ) {
                            airportItems.forEach { opt ->
                                DropdownMenuItem(
                                    text    = { Text(opt.name) },
                                    onClick = { selectedArrivalId = opt.id; arrivalExpanded = false }
                                )
                            }
                        }
                    }

                    // ── SCHEDULE ────────────────────────────────────────────
                    FlightFormSectionLabel("SCHEDULE")

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
                        if (number.isEmpty()) { flightNumberError = true; return@TextButton }

                        val airplane  = selectedAirplane  ?: return@TextButton
                        val departure = selectedDeparture ?: return@TextButton
                        val arrival   = selectedArrival   ?: return@TextButton
                        val price     = newPrice.toIntOrNull() ?: 0

                        if (newDepartureTime.isBlank() || newArrivalTime.isBlank() || price <= 0) {
                            return@TextButton
                        }

                        if (isEditing) {
                            viewModel.updateFlight(
                                id                 = editingFlightId!!,
                                flightNumber       = number,
                                airplaneId         = airplane.id,
                                departureAirportId = departure.id,
                                arrivalAirportId   = arrival.id,
                                departureTime      = newDepartureTime,
                                arrivalTime        = newArrivalTime,
                                price              = price,
                                boardingGate       = newBoardingGate.trim()
                            )
                        } else {
                            viewModel.addFlight(
                                flightNumber       = number,
                                airplaneId         = airplane.id,
                                departureAirportId = departure.id,
                                arrivalAirportId   = arrival.id,
                                departureTime      = newDepartureTime,
                                arrivalTime        = newArrivalTime,
                                price              = price,
                                boardingGate       = newBoardingGate.trim()
                            )
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
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FlightDashboardCard(
    flight:   AdminFlightUi,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    val depCode = abbreviateAirport(flight.departureAirport)
    val arrCode = abbreviateAirport(flight.arrivalAirport)
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

            // ── Route: DEP ─── ✈ ─── ARR ──
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

/**
 * Produces a 3-letter airport abbreviation for the card route display.
 * Prefers an inline code like "Airport Name (DEL)" → "DEL".
 * Falls back to the first 3 uppercase letters of the name.
 */
private fun abbreviateAirport(name: String): String {
    val inParens = name.substringAfterLast("(", "").trimEnd(')')
    return if (inParens.length in 2..4) inParens.uppercase()
    else name.filter { it.isLetter() }.take(3).uppercase().ifEmpty { "???" }
}

/**
 * Extracts the HH:mm time portion from either:
 *  - ISO-8601  "2024-01-15T06:00:00.000Z" → "06:00"
 *  - Local     "2024-01-15 06:00"          → "06:00"
 *  - Time-only "06:00"                     → "06:00"
 */
private fun extractTime(dateTime: String): String = when {
    dateTime.contains('T') -> dateTime.substring(11, minOf(16, dateTime.length))
    dateTime.contains(' ') -> dateTime.substringAfter(' ').take(5)
    else                   -> dateTime.take(5)
}

private fun formatFlightCurrency(amount: Int): String {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${fmt.format(amount)}"
}
