package com.example.airline.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.core.network.TokenManager
import com.example.airline.data.repository.AdminRepository
import com.example.airline.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

// Shared UI model used by both BookingViewModel and MyBookingsScreen
data class BookingUi(
    val bookingId:        String,
    val route:            String,
    val flightNumber:     String,
    val seatsBooked:      Int,
    val totalCost:        String,
    val status:           String,
    val departureTimeIso: String = ""
)

sealed class BookingCreateState {
    object Idle    : BookingCreateState()
    object Loading : BookingCreateState()
    object Success : BookingCreateState()
    data class Error(val message: String) : BookingCreateState()
}

sealed class MyBookingsState {
    object Loading : MyBookingsState()
    object Empty   : MyBookingsState()
    data class Success(val upcoming: List<BookingUi>, val past: List<BookingUi>) : MyBookingsState()
    data class Error(val message: String) : MyBookingsState()
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val adminRepository:   AdminRepository,
    private val tokenManager:      TokenManager
) : ViewModel() {

    private val _createState = MutableStateFlow<BookingCreateState>(BookingCreateState.Idle)
    val createState: StateFlow<BookingCreateState> = _createState.asStateFlow()

    private val _myBookingsState = MutableStateFlow<MyBookingsState>(MyBookingsState.Loading)
    val myBookingsState: StateFlow<MyBookingsState> = _myBookingsState.asStateFlow()

    // ── Create booking ────────────────────────────────────────────────────────

    fun createBooking(flightId: Int, noOfSeats: Int) {
        val userId = tokenManager.decodePayload()?.id ?: run {
            _createState.value = BookingCreateState.Error("Session expired. Please log in again.")
            return
        }
        viewModelScope.launch {
            _createState.value = BookingCreateState.Loading
            bookingRepository.createBooking(flightId, userId, noOfSeats).fold(
                onSuccess = { _createState.value = BookingCreateState.Success },
                onFailure = { _createState.value = BookingCreateState.Error(it.message ?: "Booking failed") }
            )
        }
    }

    fun resetCreateState() {
        _createState.value = BookingCreateState.Idle
    }

    // ── Fetch user's bookings ─────────────────────────────────────────────────

    fun fetchMyBookings() {
        val userId = tokenManager.decodePayload()?.id ?: run {
            _myBookingsState.value = MyBookingsState.Error("Session expired. Please log in again.")
            return
        }
        viewModelScope.launch {
            _myBookingsState.value = MyBookingsState.Loading

            val bookingsResult = bookingRepository.getBookingsByUser(userId)
            if (bookingsResult.isFailure) {
                _myBookingsState.value = MyBookingsState.Error(
                    bookingsResult.exceptionOrNull()?.message ?: "Could not load bookings"
                )
                return@launch
            }

            val bookings = bookingsResult.getOrThrow()
            if (bookings.isEmpty()) {
                _myBookingsState.value = MyBookingsState.Empty
                return@launch
            }

            // Fetch flights + airports in parallel to enrich the booking display
            val flights  = adminRepository.getFlights().getOrElse  { emptyList() }
            val airports = adminRepository.getAirports().getOrElse { emptyList() }

            val flightMap  = flights.associateBy  { it.id }
            val airportMap = airports.associateBy { it.id }
            val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))

            val uiItems = bookings.map { booking ->
                val flight     = flightMap[booking.flightId]
                val depAirport = flight?.let { airportMap[it.departureAirportId] }
                val arrAirport = flight?.let { airportMap[it.arrivalAirportId] }
                // Use first 3 upper-case chars of airport name as the IATA-style code
                val depCode = depAirport?.name?.take(3)?.uppercase() ?: "???"
                val arrCode = arrAirport?.name?.take(3)?.uppercase() ?: "???"
                BookingUi(
                    bookingId        = "BKG-${booking.id}",
                    route            = "$depCode to $arrCode",
                    flightNumber     = flight?.flightNumber ?: "FL-${booking.flightId}",
                    seatsBooked      = booking.noOfSeats,
                    totalCost        = "₹${fmt.format(booking.totalCost)}",
                    status           = booking.status,
                    departureTimeIso = flight?.departureTime ?: ""
                )
            }

            val now = Instant.now()
            val upcoming = uiItems
                .filter { b ->
                    try { Instant.parse(b.departureTimeIso).isAfter(now) } catch (_: Exception) { false }
                }
                .sortedBy { b ->
                    try { Instant.parse(b.departureTimeIso) } catch (_: Exception) { Instant.MAX }
                }
            val past = uiItems
                .filter { b ->
                    try { !Instant.parse(b.departureTimeIso).isAfter(now) } catch (_: Exception) { true }
                }
                .sortedByDescending { b ->
                    try { Instant.parse(b.departureTimeIso) } catch (_: Exception) { Instant.MIN }
                }

            _myBookingsState.value = if (upcoming.isEmpty() && past.isEmpty())
                MyBookingsState.Empty
            else
                MyBookingsState.Success(upcoming, past)
        }
    }
}
