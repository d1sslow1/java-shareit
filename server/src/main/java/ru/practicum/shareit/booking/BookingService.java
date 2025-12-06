package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import java.util.List;

public interface BookingService {
    Booking create(BookingRequestDto bookingRequestDto, Long bookerId); // Изменили на BookingRequestDto

    Booking updateStatus(Long bookingId, Boolean approved, Long ownerId);

    Booking getById(Long bookingId, Long userId);

    List<Booking> getUserBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long ownerId, String state);
}