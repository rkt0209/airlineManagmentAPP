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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Mirrors the Airplane Sequelize model:
//   modelNumber (STRING, not null), capacity (INTEGER, not null, default 200)
data class AirplaneUi(
    val id: Int,
    val modelNumber: String,
    val capacity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAirplanesScreen(
    outerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    // Mock data sourced directly from the Airplanes seeder file
    val airplanes = remember {
        mutableStateListOf(
            AirplaneUi(1, "Boeing 777", 400),
            AirplaneUi(2, "Airbus A320", 350),
            AirplaneUi(3, "Boeing 747", 320),
            AirplaneUi(4, "Boeing 77", 300),
            AirplaneUi(5, "Airbus 330", 150)
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newModelNumber by remember { mutableStateOf("") }
    var newCapacity by remember { mutableStateOf("200") }

    Scaffold(
        modifier = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Manage Airplanes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Airplane")
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
                items(airplanes) { airplane ->
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
                            // Model info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = airplane.modelNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Capacity: ${airplane.capacity} seats",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Capacity pill badge
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${airplane.capacity}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    Toast.makeText(context, "Edit Airplane clicked", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Airplane",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    Toast.makeText(context, "Delete Airplane clicked", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete Airplane",
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
                newModelNumber = ""; newCapacity = "200"
                showAddDialog = false
            },
            title = { Text("Add New Airplane") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // modelNumber — required, e.g. "Boeing 777"
                    OutlinedTextField(
                        value = newModelNumber,
                        onValueChange = { newModelNumber = it },
                        label = { Text("Model Number") },
                        placeholder = { Text("e.g. Boeing 777") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // capacity — required, integer, default 200
                    OutlinedTextField(
                        value = newCapacity,
                        onValueChange = { newCapacity = it.filter { c -> c.isDigit() } },
                        label = { Text("Capacity (seats)") },
                        placeholder = { Text("Default: 200") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = newModelNumber.trim()
                    val cap = newCapacity.toIntOrNull() ?: 200
                    if (trimmed.isNotEmpty()) {
                        airplanes.add(AirplaneUi(id = airplanes.size + 1, modelNumber = trimmed, capacity = cap))
                        Toast.makeText(context, "Airplane added", Toast.LENGTH_SHORT).show()
                    }
                    newModelNumber = ""; newCapacity = "200"
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    newModelNumber = ""; newCapacity = "200"
                    showAddDialog = false
                }) { Text("Cancel") }
            }
        )
    }
}
