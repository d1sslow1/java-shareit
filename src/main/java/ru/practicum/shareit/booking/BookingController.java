package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_ID_PATH = "/{bookingId}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping(BOOKING_ID_PATH)
    public BookingResponseDto updateStatus(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping(BOOKING_ID_PATH)
    public BookingResponseDto getById(@PathVariable Long bookingId,
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}