package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_ID_PATH = "/{bookingId}";
    private static final String STATE_PARAM = "state";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.create(bookingRequestDto, userId);
    }

    @PatchMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.updateStatus(bookingId, approved, userId);
    }

    @GetMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable Long bookingId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}