package com.example.airline.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.AirportItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AirportsState {
    object Loading : AirportsState()
    data class Success(val airports: List<AirportItem>) : AirportsState()
    data class Error(val message: String) : AirportsState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _airportsState = MutableStateFlow<AirportsState>(AirportsState.Loading)
    val airportsState: StateFlow<AirportsState> = _airportsState.asStateFlow()

    init {
        loadAirports()
    }

    fun loadAirports() {
        viewModelScope.launch {
            _airportsState.value = AirportsState.Loading
            adminRepository.getAirports().fold(
                onSuccess = { _airportsState.value = AirportsState.Success(it) },
                onFailure = { _airportsState.value = AirportsState.Error(it.message ?: "Failed to load airports") }
            )
        }
    }
}
