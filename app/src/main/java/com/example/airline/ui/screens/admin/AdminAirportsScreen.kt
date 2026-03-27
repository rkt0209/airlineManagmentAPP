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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp

// Mirrors the Airport Sequelize model:
//   name (STRING, not null), address (STRING, optional), cityId (INTEGER FK→Cities)
data class AirportUi(
    val id: Int,
    val name: String,
    val address: String,
    val cityName: String   // denormalized from City for display
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAirportsScreen(
    outerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    // Mock data matching the airport seeder + realistic Indian airports
    val airports = remember {
        mutableStateListOf(
            AirportUi(1, "Indira Gandhi International Airport", "New Delhi, Delhi", "Delhi"),
            AirportUi(2, "Chhatrapati Shivaji Maharaj International Airport", "Andheri East, Mumbai", "Mumbai"),
            AirportUi(3, "Kempegowda International Airport", "Devanahalli, Bengaluru", "Bangalore"),
            AirportUi(4, "Rajiv Gandhi International Airport", "Shamshabad, Hyderabad", "Hyderabad"),
            AirportUi(5, "Netaji Subhas Chandra Bose International Airport", "Dum Dum, Kolkata", "Kolkata")
        )
    }

    // Cities list mirrors the Cities table (seeded in AdminCitiesScreen)
    val cities = listOf("Delhi", "Mumbai", "Bangalore", "Hyderabad", "Kolkata")

    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf(cities.first()) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Manage Airports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Airport")
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(airports) { airport ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = airport.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = buildString {
                                        append(airport.cityName)
                                        if (airport.address.isNotBlank()) append(" · ${airport.address}")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    Toast.makeText(context, "Edit Airport clicked", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Airport",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    Toast.makeText(context, "Delete Airport clicked", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete Airport",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                newName = ""; newAddress = ""; selectedCity = cities.first()
                showAddDialog = false
            },
            title = { Text("Add New Airport") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // name — required, maps to Airport.name
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Airport Name") },
                        placeholder = { Text("e.g. Indira Gandhi Intl Airport") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // address — optional, maps to Airport.address
                    OutlinedTextField(
                        value = newAddress,
                        onValueChange = { newAddress = it },
                        label = { Text("Address (optional)") },
                        placeholder = { Text("e.g. New Delhi, Delhi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // cityId — required FK, shown as city name dropdown
                    ExposedDropdownMenuBox(
                        expanded = cityDropdownExpanded,
                        onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            value = selectedCity,
                            onValueChange = {},
                            label = { Text("City (cityId)") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = cityDropdownExpanded,
                            onDismissRequest = { cityDropdownExpanded = false }
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = { selectedCity = city; cityDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = newName.trim()
                    if (trimmed.isNotEmpty()) {
                        airports.add(
                            AirportUi(
                                id = airports.size + 1,
                                name = trimmed,
                                address = newAddress.trim(),
                                cityName = selectedCity
                            )
                        )
                        Toast.makeText(context, "Airport added", Toast.LENGTH_SHORT).show()
                    }
                    newName = ""; newAddress = ""; selectedCity = cities.first()
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    newName = ""; newAddress = ""; selectedCity = cities.first()
                    showAddDialog = false
                }) { Text("Cancel") }
            }
        )
    }
}
