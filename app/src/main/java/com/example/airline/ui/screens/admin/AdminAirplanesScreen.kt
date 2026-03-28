package com.example.airline.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp

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
            AirplaneUi(1, "Boeing 777",  400),
            AirplaneUi(2, "Airbus A320", 350),
            AirplaneUi(3, "Boeing 747",  320),
            AirplaneUi(4, "Boeing 77",   300),
            AirplaneUi(5, "Airbus 330",  150)
        )
    }

    var showAddDialog   by remember { mutableStateOf(false) }
    var newModelNumber  by remember { mutableStateOf("") }
    var newCapacity     by remember { mutableStateOf("200") }
    var modelError      by remember { mutableStateOf(false) }

    Scaffold(
        modifier            = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Manage Airplanes",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${airplanes.size} aircraft registered",
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
                onClick          = { showAddDialog = true },
                containerColor   = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector        = Icons.Filled.Add,
                    contentDescription = "Add Airplane",
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
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(airplanes) { airplane ->
                    AirplaneCard(
                        airplane  = airplane,
                        onEdit    = {
                            Toast.makeText(context, "Edit ${airplane.modelNumber}", Toast.LENGTH_SHORT).show()
                        },
                        onDelete  = {
                            Toast.makeText(context, "Delete ${airplane.modelNumber}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // ── Add Airplane dialog ────────────────────────────────────────────────────
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                newModelNumber = ""; newCapacity = "200"; modelError = false
                showAddDialog  = false
            },
            shape = RoundedCornerShape(24.dp),
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
                                    imageVector        = Icons.Filled.AirplanemodeActive,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text       = "Add New Airplane",
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
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // ── Section label ────────────────────────────────────────
                    FormSectionLabel("AIRCRAFT DETAILS")

                    // modelNumber — required, e.g. "Boeing 777"
                    OutlinedTextField(
                        value         = newModelNumber,
                        onValueChange = { newModelNumber = it; modelError = false },
                        label         = { Text("Model Number *") },
                        placeholder   = { Text("e.g. Boeing 777") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.AirplanemodeActive,
                                contentDescription = null,
                                tint               = if (modelError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        },
                        isError       = modelError,
                        supportingText = if (modelError) {
                            { Text("Model number cannot be empty") }
                        } else null,
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    // capacity — required, integer, default 200
                    OutlinedTextField(
                        value         = newCapacity,
                        onValueChange = { newCapacity = it.filter { c -> c.isDigit() } },
                        label         = { Text("Capacity (seats) *") },
                        placeholder   = { Text("Default: 200") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Speed,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine     = true,
                        modifier       = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape          = RoundedCornerShape(14.dp)
                    )

                    // Info chip: totalSeats auto-derived from capacity
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text     = "ℹ  totalSeats on flights will auto-match this capacity",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newModelNumber.trim()
                        if (trimmed.isEmpty()) {
                            modelError = true
                            return@TextButton
                        }
                        val cap = newCapacity.toIntOrNull() ?: 200
                        airplanes.add(
                            AirplaneUi(
                                id          = airplanes.size + 1,
                                modelNumber = trimmed,
                                capacity    = cap
                            )
                        )
                        Toast.makeText(context, "${trimmed} added", Toast.LENGTH_SHORT).show()
                        newModelNumber = ""; newCapacity = "200"; modelError = false
                        showAddDialog = false
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newModelNumber = ""; newCapacity = "200"; modelError = false
                        showAddDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium airplane card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AirplaneCard(
    airplane: AirplaneUi,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape           = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
        tonalElevation  = 1.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Airplane icon circle
            Surface(
                shape    = CircleShape,
                color    = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Filled.AirplanemodeActive,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }

            // Model + ID
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = airplane.modelNumber,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Aircraft ID #${airplane.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Capacity pill
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text     = "${airplane.capacity} seats",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    style    = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color    = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Edit / Delete
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

// ─── Shared form helper ───────────────────────────────────────────────────────
@Composable
private fun FormSectionLabel(text: String) {
    Text(
        text          = text,
        style         = MaterialTheme.typography.labelSmall,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.5.sp
    )
}
