package com.example.airline.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String)  : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signIn(email, password, role)
                .onSuccess { _uiState.value = AuthUiState.Success(role) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Sign in failed") }
        }
    }

    /**
     * Registers the user then immediately signs them in — no manual second step.
     * Both calls receive the same role so the DB stores it and the token check passes.
     */
    fun signUp(email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signUp(email, password, role)
                .onSuccess {
                    repository.signIn(email, password, role)
                        .onSuccess { _uiState.value = AuthUiState.Success(role) }
                        .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Sign in failed after registration") }
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Sign up failed") }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
