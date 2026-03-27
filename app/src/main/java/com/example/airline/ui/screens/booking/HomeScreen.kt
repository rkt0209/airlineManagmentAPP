package com.example.airline.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class AirportOption(
    val code: String,
    val displayName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchFlights: (departureCode: String, arrivalCode: String, selectedDate: String) -> Unit,
    outerPadding: PaddingValues = PaddingValues()
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

    // Use remember for custom objects; rememberSaveable with custom data classes can crash
    // unless a custom Saver/Parcelable is provided.
    var departure by remember { mutableStateOf(airports.first()) }
    var arrival by remember { mutableStateOf(airports[1]) }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Search Flights") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Where are you flying today?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Find the best fares and premium routes in seconds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        AirportDropdown(
                            label = "Departure Airport",
                            selected = departure,
                            options = airports,
                            onSelect = { departure = it }
                        )

                        AirportDropdown(
                            label = "Arrival Airport",
                            selected = arrival,
                            options = airports.filter { it.code != departure.code },
                            onSelect = { arrival = it }
                        )

                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = { showDatePicker = true })
                                },
                            label = { Text("Travel Date") },
                            readOnly = true,
                            trailingIcon = {
                                Text(
                                    text = "Pick",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onSearchFlights(departure.code, arrival.code, selectedDate)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Search Flights", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
                // Ensure last content clears the outer bottom nav bar
                Spacer(modifier = Modifier.height(outerPadding.calculateBottomPadding()))
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
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
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AirportDropdown(
    label: String,
    selected: AirportOption,
    options: List<AirportOption>,
    onSelect: (AirportOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = "${selected.code} - ${selected.displayName}",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { airport ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("${airport.code} - ${airport.displayName}") },
                    onClick = {
                        onSelect(airport)
                        expanded = false
                    }
                )
            }
        }
    }
}

