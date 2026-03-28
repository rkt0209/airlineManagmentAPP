package com.example.airline.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel

private val AuthNavyTop     = Color(0xFF0B1B3A)
private val AuthNavyMid     = Color(0xFF0D2247)
private val AuthNavyBottom  = Color(0xFF071226)
private val AuthOnNavy      = Color(0xFFEAF2FF)
private val AuthOnNavyMuted = Color(0xFF8BAFD4)
private val AuthAccent      = Color(0xFF1E88E5)

@Composable
fun SignupScreen(
    onSignupComplete: (role: String) -> Unit,
    initialRole: String,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email           by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var emailError      by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError   by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmError    by rememberSaveable { mutableStateOf<String?>(null) }
    var showPassword    by rememberSaveable { mutableStateOf(false) }
    var selectedRole    by rememberSaveable { mutableStateOf(initialRole) }

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading
    val isSuccess = uiState is AuthUiState.Success

    // Navigate on success; surface API errors inline
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onSignupComplete(state.role)
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                emailError = state.message
            }
            else -> {}
        }
    }

    // Pin status bar to navy regardless of dynamic color / current theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AuthNavyTop.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AuthNavyTop, AuthNavyMid, AuthNavyBottom))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Hero / Branding ───────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.10f),
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Flight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "AIRLINE",
                color = AuthAccent,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Create Account",
                color = AuthOnNavy,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Your adventure starts here",
                color = AuthOnNavyMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(40.dp))

            // ── Form Card ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email address") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = emailError != null,
                        supportingText = { if (emailError != null) Text(emailError!!) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.VisibilityOff
                                                  else Icons.Filled.Visibility,
                                    contentDescription = if (showPassword) "Hide password"
                                                         else "Show password"
                                )
                            }
                        },
                        isError = passwordError != null,
                        supportingText = { if (passwordError != null) Text(passwordError!!) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmError = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm password") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = confirmError != null,
                        supportingText = { if (confirmError != null) Text(confirmError!!) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    SignupRoleSelector(
                        selectedRole = selectedRole,
                        onRoleSelected = { selectedRole = it }
                    )

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (isLoading) return@Button
                            var valid = true
                            if (email.isBlank()) {
                                emailError = "Email is required"
                                valid = false
                            }
                            if (password.isBlank()) {
                                passwordError = "Password is required"
                                valid = false
                            }
                            if (password != confirmPassword) {
                                confirmError = "Passwords do not match"
                                valid = false
                            }
                            if (valid) viewModel.signUp(email.trim(), password, selectedRole)
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Creating account…",
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else {
                            Text(
                                text = "Create Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (isSuccess) {
                        Text(
                            text = "Account created. Redirecting…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Back to Login hint ────────────────────────────────────────
            Text(
                text = "Already have an account? Use the back button to sign in.",
                color = AuthOnNavyMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SignupRoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "I am a",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Passenger", "Admin").forEach { role ->
                    val isSelected = role == selectedRole
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) { onRoleSelected(role) },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.surface
                                else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        tonalElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Text(
                            text = role,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
