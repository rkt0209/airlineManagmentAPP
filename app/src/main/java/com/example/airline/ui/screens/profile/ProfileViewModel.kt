package com.example.airline.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.airline.core.network.JwtPayload
import com.example.airline.core.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profile = MutableStateFlow<JwtPayload?>(null)
    val profile: StateFlow<JwtPayload?> = _profile.asStateFlow()

    init {
        _profile.value = tokenManager.decodePayload()
    }
}
