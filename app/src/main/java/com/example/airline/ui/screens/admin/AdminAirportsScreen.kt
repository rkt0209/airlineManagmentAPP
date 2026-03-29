package com.example.airline.ui.screens.admin

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.airline.data.remote.CityItem

// UI display model — resolved from AirportItem + CityItem lookup
data class AirportUi(
    val id:       Int,
    val name:     String,
    val address:  String,
    val cityName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAirportsScreen(
    outerPadding: PaddingValues = PaddingValues(),
    viewModel: AdminAirportsViewModel = hiltViewModel()
) {
    val airportItems  by viewModel.airports.collectAsState()
    val cityItems     by viewModel.cities.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    val errorMsg      by viewModel.errorMessage.collectAsState()

    // Resolve airport display models: join AirportItem with CityItem for city name
    val airports = remember(airportItems, cityItems) {
        airportItems.map { item ->
            AirportUi(
                id       = item.id,
                name     = item.name,
                address  = item.address ?: "",
                cityName = cityItems.find { it.id == item.cityId }?.name
                    ?: "City #${item.cityId}"
            )
        }
    }

    var showAddDialog        by remember { mutableStateOf(false) }
    var newName              by remember { mutableStateOf("") }
    var newAddress           by remember { mutableStateOf("") }
    var selectedCity         by remember { mutableStateOf<CityItem?>(null) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }
    var nameError            by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Default to first city when list loads
    LaunchedEffect(cityItems) {
        if (selectedCity == null && cityItems.isNotEmpty()) {
            selectedCity = cityItems.first()
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
                            text       = "Manage Airports",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${airports.size} airports registered",
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
                    contentDescription = "Add Airport",
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
            if (isLoading && airports.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(airports, key = { it.id }) { airport ->
                        AirportCard(
                            airport  = airport,
                            onEdit   = { /* Edit not yet implemented */ },
                            onDelete = { viewModel.deleteAirport(airport.id) }
                        )
                    }
                }
            }
        }
    }

    // ── Add Airport dialog ─────────────────────────────────────────────────
    if (showAddDialog) {
        val resetDialog: () -> Unit = {
            newName = ""; newAddress = ""; nameError = false
            selectedCity = cityItems.firstOrNull(); showAddDialog = false
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
                                    imageVector        = Icons.Filled.Place,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary,
                                    modifier           = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text(
                            text       = "Add New Airport",
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

                    AirportFormSectionLabel("AIRPORT DETAILS")

                    OutlinedTextField(
                        value         = newName,
                        onValueChange = { newName = it; nameError = false },
                        label         = { Text("Airport Name *") },
                        placeholder   = { Text("e.g. Indira Gandhi Intl Airport") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Place,
                                contentDescription = null,
                                tint = if (nameError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        },
                        isError        = nameError,
                        supportingText = if (nameError) {
                            { Text("Airport name cannot be empty") }
                        } else null,
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    AirportFormSectionLabel("LOCATION")

                    OutlinedTextField(
                        value         = newAddress,
                        onValueChange = { newAddress = it },
                        label         = { Text("Address (optional)") },
                        placeholder   = { Text("e.g. New Delhi, Delhi") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp)
                    )

                    // City dropdown — populated from ViewModel's cities list
                    ExposedDropdownMenuBox(
                        expanded         = cityDropdownExpanded,
                        onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            readOnly      = true,
                            value         = selectedCity?.name ?: if (cityItems.isEmpty()) "Loading…" else "Select city",
                            onValueChange = {},
                            label         = { Text("City (cityId) *") },
                            leadingIcon   = {
                                Icon(
                                    Icons.Filled.LocationCity,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon  = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded)
                            },
                            shape         = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded         = cityDropdownExpanded,
                            onDismissRequest = { cityDropdownExpanded = false }
                        ) {
                            cityItems.forEach { city ->
                                DropdownMenuItem(
                                    text    = { Text(city.name) },
                                    onClick = { selectedCity = city; cityDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newName.trim()
                        if (trimmed.isEmpty()) { nameError = true; return@TextButton }
                        val city = selectedCity ?: return@TextButton
                        viewModel.addAirport(trimmed, newAddress.trim(), city.id)
                        resetDialog()
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = resetDialog) { Text("Cancel") }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium airport card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AirportCard(
    airport:  AirportUi,
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
            Surface(
                shape    = CircleShape,
                color    = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Filled.Place,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text       = airport.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Airport ID #${airport.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text       = airport.cityName,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    if (airport.address.isNotBlank()) {
                        Text(
                            text  = airport.address,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column {
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

@Composable
private fun AirportFormSectionLabel(text: String) {
    Text(
        text          = text,
        style         = MaterialTheme.typography.labelSmall,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.5.sp
    )
}
