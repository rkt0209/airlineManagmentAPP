package com.example.airline.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("authservice/api/v1/signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @POST("authservice/api/v1/signin")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse
}
