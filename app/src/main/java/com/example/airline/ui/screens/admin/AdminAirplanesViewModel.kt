package com.example.airline.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.AirplaneItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAirplanesViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _airplanes = MutableStateFlow<List<AirplaneItem>>(emptyList())
    val airplanes: StateFlow<List<AirplaneItem>> = _airplanes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init { fetchAirplanes() }

    fun fetchAirplanes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAirplanes()
                .onSuccess  { _airplanes.value = it }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun addAirplane(modelNumber: String, capacity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createAirplane(modelNumber, capacity)
                .onSuccess  { fetchAirplanes() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateAirplane(id: Int, modelNumber: String, capacity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateAirplane(id, modelNumber, capacity)
                .onSuccess  { fetchAirplanes() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteAirplane(id: Int) {
        viewModelScope.launch {
            repository.deleteAirplane(id)
                .onSuccess { fetchAirplanes() }
                .onFailure { _errorMessage.value = it.message }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
