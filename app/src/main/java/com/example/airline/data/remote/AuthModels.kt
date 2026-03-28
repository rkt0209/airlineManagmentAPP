package com.example.airline.data.remote

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role")     val role: String      // "Passenger" or "Admin"
)

data class SignInRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role")     val role: String      // "Passenger" or "Admin"
)

/**
 * POST /authservice/api/v1/signup
 * Backend returns only a message; no token issued at sign-up.
 */
data class SignUpResponse(
    @SerializedName("message") val message: String
)

/**
 * POST /authservice/api/v1/signin
 * "data" is the raw JWT string (not a nested object).
 * Example: { "message": "Successful Sign In...", "data": "<jwt>" }
 */
data class SignInResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data")    val data: String   // raw JWT token
)
