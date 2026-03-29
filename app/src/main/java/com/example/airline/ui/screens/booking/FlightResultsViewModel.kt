package com.example.airline.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.FlightItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FlightSearchState {
    object Idle      : FlightSearchState()
    object Searching : FlightSearchState()
    object Empty     : FlightSearchState()
    data class Success(val flights: List<FlightItem>) : FlightSearchState()
    data class Error(val message: String)             : FlightSearchState()
}

@HiltViewModel
class FlightResultsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<FlightSearchState>(FlightSearchState.Idle)
    val searchState: StateFlow<FlightSearchState> = _searchState.asStateFlow()

    fun searchFlights(departureAirportId: Int, arrivalAirportId: Int) {
        if (_searchState.value is FlightSearchState.Searching) return
        viewModelScope.launch {
            _searchState.value = FlightSearchState.Searching
            adminRepository.getFlights().fold(
                onSuccess = { all ->
                    val filtered = all.filter {
                        it.departureAirportId == departureAirportId &&
                        it.arrivalAirportId   == arrivalAirportId
                    }
                    _searchState.value = if (filtered.isEmpty()) FlightSearchState.Empty
                                         else FlightSearchState.Success(filtered)
                },
                onFailure = {
                    _searchState.value = FlightSearchState.Error(it.message ?: "Search failed")
                }
            )
        }
    }
}
