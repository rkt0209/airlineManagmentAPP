package com.example.airline.data.repository

import com.example.airline.data.remote.BookingApi
import com.example.airline.data.remote.BookingItem
import com.example.airline.data.remote.CreateBookingRequest
import com.google.gson.JsonParser
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingApi: BookingApi
) {

    suspend fun createBooking(
        flightId:  Int,
        userId:    Int,
        noOfSeats: Int
    ): Result<BookingItem> =
        runCatching {
            bookingApi.createBooking(CreateBookingRequest(flightId, userId, noOfSeats)).data
        }.mapHttpError()

    suspend fun getBookingsByUser(userId: Int): Result<List<BookingItem>> =
        runCatching {
            bookingApi.getBookingsByUser(userId).data
        }.mapHttpError()

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
