package com.example.airline.data.repository

import com.example.airline.core.network.TokenManager
import com.example.airline.data.remote.AuthApi
import com.example.airline.data.remote.AuthRequest
import com.example.airline.data.remote.SignInResponse
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

    suspend fun signUp(email: String, password: String): Result<SignUpResponse> =
        runCatching {
            authApi.signUp(AuthRequest(email, password))
        }.mapHttpError()

    suspend fun signIn(email: String, password: String): Result<SignInResponse> =
        runCatching {
            val response = authApi.signIn(AuthRequest(email, password))
            tokenManager.saveToken(response.data)
            response
        }.mapHttpError()

    /**
     * For non-2xx responses, Retrofit throws HttpException.
     * This unwraps the server's JSON error body (e.g. {"message":"..."})
     * so the ViewModel receives the actual server message instead of
     * the generic "HTTP 500 Internal Server Error" string.
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
