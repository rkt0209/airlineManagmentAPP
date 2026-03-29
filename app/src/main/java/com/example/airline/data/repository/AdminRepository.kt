package com.example.airline.data.repository

import com.example.airline.data.remote.AdminApi
import com.example.airline.data.remote.AirplaneItem
import com.example.airline.data.remote.AirportItem
import com.example.airline.data.remote.CityItem
import com.example.airline.data.remote.CreateAirplaneRequest
import com.example.airline.data.remote.CreateAirportRequest
import com.example.airline.data.remote.CreateCityRequest
import com.example.airline.data.remote.CreateFlightRequest
import com.example.airline.data.remote.FlightItem
import com.google.gson.JsonParser
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(private val adminApi: AdminApi) {

    // ── Cities ────────────────────────────────────────────────────────────────

    suspend fun getCities(): Result<List<CityItem>> =
        runCatching { adminApi.getCities().data }.mapHttpError()

    suspend fun createCity(name: String): Result<CityItem> =
        runCatching { adminApi.createCity(CreateCityRequest(name)).data }.mapHttpError()

    suspend fun updateCity(id: Int, name: String): Result<Unit> =
        runCatching { adminApi.updateCity(id, CreateCityRequest(name)); Unit }.mapHttpError()

    suspend fun deleteCity(id: Int): Result<Unit> =
        runCatching { adminApi.deleteCity(id); Unit }.mapHttpError()

    // ── Airports ──────────────────────────────────────────────────────────────

    suspend fun getAirports(): Result<List<AirportItem>> =
        runCatching { adminApi.getAirports().data }.mapHttpError()

    suspend fun createAirport(name: String, address: String?, cityId: Int): Result<AirportItem> =
        runCatching {
            adminApi.createAirport(CreateAirportRequest(name, address, cityId)).data
        }.mapHttpError()

    suspend fun updateAirport(id: Int, name: String, address: String?, cityId: Int): Result<Unit> =
        runCatching {
            adminApi.updateAirport(id, CreateAirportRequest(name, address, cityId)); Unit
        }.mapHttpError()

    suspend fun deleteAirport(id: Int): Result<Unit> =
        runCatching { adminApi.deleteAirport(id); Unit }.mapHttpError()

    // ── Airplanes ─────────────────────────────────────────────────────────────

    suspend fun getAirplanes(): Result<List<AirplaneItem>> =
        runCatching { adminApi.getAirplanes().data }.mapHttpError()

    suspend fun createAirplane(modelNumber: String, capacity: Int): Result<AirplaneItem> =
        runCatching {
            adminApi.createAirplane(CreateAirplaneRequest(modelNumber, capacity)).data
        }.mapHttpError()

    suspend fun updateAirplane(id: Int, modelNumber: String, capacity: Int): Result<Unit> =
        runCatching {
            adminApi.updateAirplane(id, CreateAirplaneRequest(modelNumber, capacity)); Unit
        }.mapHttpError()

    suspend fun deleteAirplane(id: Int): Result<Unit> =
        runCatching { adminApi.deleteAirplane(id); Unit }.mapHttpError()

    // ── Flights ───────────────────────────────────────────────────────────────

    suspend fun getFlights(): Result<List<FlightItem>> =
        runCatching { adminApi.getFlights().data }.mapHttpError()

    suspend fun createFlight(req: CreateFlightRequest): Result<FlightItem> =
        runCatching { adminApi.createFlight(req).data }.mapHttpError()

    suspend fun updateFlight(id: Int, req: CreateFlightRequest): Result<Unit> =
        runCatching { adminApi.updateFlight(id, req); Unit }.mapHttpError()

    suspend fun deleteFlight(id: Int): Result<Unit> =
        runCatching { adminApi.deleteFlight(id); Unit }.mapHttpError()

    // ── Error extraction (mirrors AuthRepository pattern) ─────────────────────

    private fun <T> Result<T>.mapHttpError(): Result<T> = recoverCatching { e ->
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
