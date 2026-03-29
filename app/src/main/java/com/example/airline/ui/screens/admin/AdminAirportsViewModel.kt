package com.example.airline.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.AirportItem
import com.example.airline.data.remote.CityItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAirportsViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _airports = MutableStateFlow<List<AirportItem>>(emptyList())
    val airports: StateFlow<List<AirportItem>> = _airports.asStateFlow()

    // Cities list is needed to populate the city dropdown and resolve display names
    private val _cities = MutableStateFlow<List<CityItem>>(emptyList())
    val cities: StateFlow<List<CityItem>> = _cities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchAirports()
        fetchCities()
    }

    fun fetchAirports() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAirports()
                .onSuccess  { _airports.value = it }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun fetchCities() {
        viewModelScope.launch {
            repository.getCities()
                .onSuccess { _cities.value = it }
                .onFailure { _errorMessage.value = "Failed to load cities: ${it.message}" }
        }
    }

    fun addAirport(name: String, address: String, cityId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createAirport(name, address.ifBlank { null }, cityId)
                .onSuccess  { fetchAirports() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteAirport(id: Int) {
        viewModelScope.launch {
            repository.deleteAirport(id)
                .onSuccess { fetchAirports() }
                .onFailure { _errorMessage.value = it.message }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
