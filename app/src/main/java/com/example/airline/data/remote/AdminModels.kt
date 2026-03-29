package com.example.airline.data.remote

import com.google.gson.annotations.SerializedName

// ── Generic API envelopes ─────────────────────────────────────────────────────

data class ApiListResponse<T>(
    @SerializedName("data")    val data: List<T>,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class ApiObjectResponse<T>(
    @SerializedName("data")    val data: T,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

// ── City ──────────────────────────────────────────────────────────────────────

data class CityItem(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String
)

data class CreateCityRequest(
    @SerializedName("name") val name: String
)

// ── Airport ───────────────────────────────────────────────────────────────────

data class AirportItem(
    @SerializedName("id")      val id: Int,
    @SerializedName("name")    val name: String,
    @SerializedName("address") val address: String?,
    @SerializedName("cityId")  val cityId: Int
)

data class CreateAirportRequest(
    @SerializedName("name")    val name: String,
    @SerializedName("address") val address: String?,
    @SerializedName("cityId")  val cityId: Int
)

// ── Airplane ──────────────────────────────────────────────────────────────────

data class AirplaneItem(
    @SerializedName("id")          val id: Int,
    @SerializedName("modelNumber") val modelNumber: String,
    @SerializedName("capacity")    val capacity: Int
)

data class CreateAirplaneRequest(
    @SerializedName("modelNumber") val modelNumber: String,
    @SerializedName("capacity")    val capacity: Int
)

// ── Flight ────────────────────────────────────────────────────────────────────

data class FlightItem(
    @SerializedName("id")                 val id: Int,
    @SerializedName("flightNumber")       val flightNumber: String,
    @SerializedName("airplaneId")         val airplaneId: Int,
    @SerializedName("departureAirportId") val departureAirportId: Int,
    @SerializedName("arrivalAirportId")   val arrivalAirportId: Int,
    @SerializedName("departureTime")      val departureTime: String,
    @SerializedName("arrivalTime")        val arrivalTime: String,
    @SerializedName("price")              val price: Int,
    @SerializedName("boardingGate")       val boardingGate: String?,
    @SerializedName("totalSeats")         val totalSeats: Int
)

data class CreateFlightRequest(
    @SerializedName("flightNumber")       val flightNumber: String,
    @SerializedName("airplaneId")         val airplaneId: Int,
    @SerializedName("departureAirportId") val departureAirportId: Int,
    @SerializedName("arrivalAirportId")   val arrivalAirportId: Int,
    @SerializedName("departureTime")      val departureTime: String,
    @SerializedName("arrivalTime")        val arrivalTime: String,
    @SerializedName("price")              val price: Int,
    @SerializedName("boardingGate")       val boardingGate: String? = null
)
