package com.example.airline.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApi {

    @POST("bookingservice/api/v1/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): ApiObjectResponse<BookingItem>

    @GET("bookingservice/api/v1/bookings/user/{userId}")
    suspend fun getBookingsByUser(@Path("userId") userId: Int): ApiListResponse<BookingItem>
}
