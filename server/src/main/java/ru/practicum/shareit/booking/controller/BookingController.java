package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        Booking booking = bookingService.create(bookingRequestDto, bookerId);
        return toDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatus(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Booking booking = bookingService.updateStatus(bookingId, approved, ownerId);
        return toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.getById(bookingId, userId);
        return toDto(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BookingResponseDto toDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingResponseDto.Booker booker = new BookingResponseDto.Booker();
        booker.setId(booking.getBooker().getId());
        booker.setName(booking.getBooker().getName());
        booker.setEmail(booking.getBooker().getEmail());
        dto.setBooker(booker);

        BookingResponseDto.Item item = new BookingResponseDto.Item();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        item.setDescription(booking.getItem().getDescription());
        item.setAvailable(booking.getItem().getAvailable());
        item.setOwnerId(booking.getItem().getOwnerId());
        item.setRequestId(booking.getItem().getRequestId());
        dto.setItem(item);

        return dto;
    }
}