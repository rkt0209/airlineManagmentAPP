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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationCity
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCitiesScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = true,
    outerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val cities = remember {
        mutableStateListOf("Delhi", "Mumbai", "Bangalore", "Hyderabad", "Kolkata")
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newCity       by remember { mutableStateOf("") }
    var cityError     by remember { mutableStateOf(false) }

    Scaffold(
        modifier            = Modifier.padding(bottom = outerPadding.calculateBottomPadding()),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Manage Cities",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${cities.size} cities registered",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        TextButton(onClick = onBack) { Text("Back") }
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
                    contentDescription = "Add City",
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
                itemsIndexed(cities) { index, city ->
                    CityCard(
                        city   = city,
                        cityId = index + 1,
                        onEdit = {
                            Toast.makeText(context, "Edit $city", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            Toast.makeText(context, "Delete $city", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // ── Add City dialog ────────────────────────────────────────────────────
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                newCity = ""; cityError = false; showAddDialog = false
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
                                    imageVector        = Icons.Filled.LocationCity,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text       = "Add New City",
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
                    CityFormSectionLabel("CITY DETAILS")

                    // name — required, mirrors City.name in DB schema
                    OutlinedTextField(
                        value         = newCity,
                        onValueChange = { newCity = it; cityError = false },
                        label         = { Text("City Name *") },
                        placeholder   = { Text("e.g. Chennai") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.LocationCity,
                                contentDescription = null,
                                tint = if (cityError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        },
                        isError        = cityError,
                        supportingText = if (cityError) {
                            { Text("City name cannot be empty") }
                        } else null,
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newCity.trim()
                        if (trimmed.isEmpty()) {
                            cityError = true
                            return@TextButton
                        }
                        cities.add(trimmed)
                        Toast.makeText(context, "$trimmed added", Toast.LENGTH_SHORT).show()
                        newCity = ""; cityError = false; showAddDialog = false
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { newCity = ""; cityError = false; showAddDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium city card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CityCard(
    city:     String,
    cityId:   Int,
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
            // City icon circle
            Surface(
                shape    = CircleShape,
                color    = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Filled.LocationCity,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }

            // City name + ID
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = city,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "City ID #$cityId",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

// ─── Form section label ───────────────────────────────────────────────────────
@Composable
private fun CityFormSectionLabel(text: String) {
    Text(
        text          = text,
        style         = MaterialTheme.typography.labelSmall,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.5.sp
    )
}
