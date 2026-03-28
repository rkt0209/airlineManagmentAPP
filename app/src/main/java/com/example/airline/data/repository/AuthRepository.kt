package com.example.airline.data.repository

import com.example.airline.core.network.TokenManager
import com.example.airline.data.remote.AuthApi
import com.example.airline.data.remote.SignInRequest
import com.example.airline.data.remote.SignInResponse
import com.example.airline.data.remote.SignUpRequest
import com.example.airline.data.remote.SignUpResponse
import com.google.gson.JsonParser
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun signUp(email: String, password: String, role: String): Result<SignUpResponse> =
        runCatching {
            authApi.signUp(SignUpRequest(email, password, role))
        }.mapHttpError()

    suspend fun signIn(email: String, password: String, role: String): Result<SignInResponse> =
        runCatching {
            val response = authApi.signIn(SignInRequest(email, password, role))
            tokenManager.saveToken(response.data)
            response
        }.mapHttpError()

    /**
     * Extracts the server's JSON "message" field from non-2xx responses so the
     * ViewModel (and ultimately the UI) shows the real reason instead of
     * a generic "HTTP 4xx" string.
     */
    private fun <T> Result<T>.mapHttpError(): Result<T> = this.recoverCatching { e ->
        if (e is HttpException) {
            val serverMessage = e.response()?.errorBody()?.string()?.let { body ->
                runCatching {
                    JsonParser.parseString(body).asJsonObject.get("message")?.asString
                }.getOrNull()
            }
            throw Exception(serverMessage ?: "HTTP ${e.code()}: ${e.message()}")
        }
        throw e
    }
}
