package com.example.airline.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.AirplaneItem
import com.example.airline.data.remote.AirportItem
import com.example.airline.data.remote.CreateFlightRequest
import com.example.airline.data.remote.FlightItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFlightsViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _flights = MutableStateFlow<List<FlightItem>>(emptyList())
    val flights: StateFlow<List<FlightItem>> = _flights.asStateFlow()

    // Airport and Airplane lists are needed to resolve IDs to display names
    // and to populate the dropdowns in the Add Flight dialog
    private val _airports = MutableStateFlow<List<AirportItem>>(emptyList())
    val airports: StateFlow<List<AirportItem>> = _airports.asStateFlow()

    private val _airplanes = MutableStateFlow<List<AirplaneItem>>(emptyList())
    val airplanes: StateFlow<List<AirplaneItem>> = _airplanes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchFlights()
        fetchAirports()
        fetchAirplanes()
    }

    fun fetchFlights() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getFlights()
                .onSuccess  { _flights.value = it }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchAirports() {
        viewModelScope.launch {
            repository.getAirports()
                .onSuccess { _airports.value = it }
                .onFailure { /* silent; dropdown stays empty */ }
        }
    }

    fun fetchAirplanes() {
        viewModelScope.launch {
            repository.getAirplanes()
                .onSuccess { _airplanes.value = it }
                .onFailure { /* silent; dropdown stays empty */ }
        }
    }

    fun addFlight(
        flightNumber: String,
        airplaneId: Int,
        departureAirportId: Int,
        arrivalAirportId: Int,
        departureTime: String,
        arrivalTime: String,
        price: Int,
        boardingGate: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createFlight(
                CreateFlightRequest(
                    flightNumber       = flightNumber,
                    airplaneId         = airplaneId,
                    departureAirportId = departureAirportId,
                    arrivalAirportId   = arrivalAirportId,
                    departureTime      = departureTime,
                    arrivalTime        = arrivalTime,
                    price              = price,
                    boardingGate       = boardingGate?.ifBlank { null }
                )
            )
                .onSuccess  { fetchFlights() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateFlight(
        id: Int,
        flightNumber: String,
        airplaneId: Int,
        departureAirportId: Int,
        arrivalAirportId: Int,
        departureTime: String,
        arrivalTime: String,
        price: Int,
        boardingGate: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateFlight(
                id,
                CreateFlightRequest(
                    flightNumber       = flightNumber,
                    airplaneId         = airplaneId,
                    departureAirportId = departureAirportId,
                    arrivalAirportId   = arrivalAirportId,
                    departureTime      = departureTime,
                    arrivalTime        = arrivalTime,
                    price              = price,
                    boardingGate       = boardingGate?.ifBlank { null }
                )
            )
                .onSuccess  { fetchFlights() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteFlight(id: Int) {
        viewModelScope.launch {
            repository.deleteFlight(id)
                .onSuccess { fetchFlights() }
                .onFailure { _errorMessage.value = it.message }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
