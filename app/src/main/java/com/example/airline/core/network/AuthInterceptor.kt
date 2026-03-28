package com.example.airline.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("x-access-token", token)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
