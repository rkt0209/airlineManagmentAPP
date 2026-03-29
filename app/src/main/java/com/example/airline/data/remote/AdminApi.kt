package com.example.airline.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AdminApi {

    // ── Cities ────────────────────────────────────────────────────────────────
    @GET("flightservice/api/v1/city")
    suspend fun getCities(): ApiListResponse<CityItem>

    @POST("flightservice/api/v1/city")
    suspend fun createCity(@Body request: CreateCityRequest): ApiObjectResponse<CityItem>

    @PATCH("flightservice/api/v1/city/{id}")
    suspend fun updateCity(@Path("id") id: Int, @Body request: CreateCityRequest): ApiObjectResponse<Any>

    @DELETE("flightservice/api/v1/city/{id}")
    suspend fun deleteCity(@Path("id") id: Int): ApiObjectResponse<Any>

    // ── Airports ──────────────────────────────────────────────────────────────
    @GET("flightservice/api/v1/airport")
    suspend fun getAirports(): ApiListResponse<AirportItem>

    @POST("flightservice/api/v1/airport")
    suspend fun createAirport(@Body request: CreateAirportRequest): ApiObjectResponse<AirportItem>

    @PATCH("flightservice/api/v1/airport/{id}")
    suspend fun updateAirport(@Path("id") id: Int, @Body request: CreateAirportRequest): ApiObjectResponse<Any>

    @DELETE("flightservice/api/v1/airport/{id}")
    suspend fun deleteAirport(@Path("id") id: Int): ApiObjectResponse<Any>

    // ── Airplanes ─────────────────────────────────────────────────────────────
    @GET("flightservice/api/v1/airplane")
    suspend fun getAirplanes(): ApiListResponse<AirplaneItem>

    @POST("flightservice/api/v1/airplane")
    suspend fun createAirplane(@Body request: CreateAirplaneRequest): ApiObjectResponse<AirplaneItem>

    @PATCH("flightservice/api/v1/airplane/{id}")
    suspend fun updateAirplane(@Path("id") id: Int, @Body request: CreateAirplaneRequest): ApiObjectResponse<Any>

    @DELETE("flightservice/api/v1/airplane/{id}")
    suspend fun deleteAirplane(@Path("id") id: Int): ApiObjectResponse<Any>

    // ── Flights ───────────────────────────────────────────────────────────────
    @GET("flightservice/api/v1/flights")
    suspend fun getFlights(): ApiListResponse<FlightItem>

    @POST("flightservice/api/v1/flights")
    suspend fun createFlight(@Body request: CreateFlightRequest): ApiObjectResponse<FlightItem>

    @PATCH("flightservice/api/v1/flights/{id}")
    suspend fun updateFlight(@Path("id") id: Int, @Body request: CreateFlightRequest): ApiObjectResponse<Any>

    @DELETE("flightservice/api/v1/flights/{id}")
    suspend fun deleteFlight(@Path("id") id: Int): ApiObjectResponse<Any>
}
