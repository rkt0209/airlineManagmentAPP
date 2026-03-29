package com.example.airline.data.remote

import com.google.gson.annotations.SerializedName

data class CreateBookingRequest(
    @SerializedName("flightId")  val flightId:  Int,
    @SerializedName("userId")    val userId:    Int,
    @SerializedName("noOfSeats") val noOfSeats: Int
)

data class BookingItem(
    @SerializedName("id")        val id:        Int,
    @SerializedName("flightId")  val flightId:  Int,
    @SerializedName("userId")    val userId:    Int,
    @SerializedName("status")    val status:    String,
    @SerializedName("noOfSeats") val noOfSeats: Int,
    @SerializedName("totalCost") val totalCost: Int
)
