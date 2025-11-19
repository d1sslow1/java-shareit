package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingDto updateStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);
}