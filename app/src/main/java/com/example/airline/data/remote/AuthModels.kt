package com.example.airline.data.remote

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

/**
 * POST /authservice/api/v1/signup
 * Backend returns only a message; no token is issued at sign-up.
 */
data class SignUpResponse(
    @SerializedName("message") val message: String
)

/**
 * POST /authservice/api/v1/signin
 * "data" is the raw JWT string (not a nested object).
 *
 * Example response:
 *   { "message": "Login successful", "data": "<jwt_token>" }
 */
data class SignInResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data")    val data: String   // raw JWT token
)
