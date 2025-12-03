package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto bookingRequestDto, Item item, User booker) {
        if (bookingRequestDto == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public BookingResponseDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto.Item item = new BookingResponseDto.Item();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        item.setDescription(booking.getItem().getDescription());
        item.setAvailable(booking.getItem().getAvailable());
        item.setOwnerId(booking.getItem().getOwnerId());
        item.setRequestId(booking.getItem().getRequestId());

        BookingResponseDto.Booker booker = new BookingResponseDto.Booker();
        booker.setId(booking.getBooker().getId());
        booker.setName(booking.getBooker().getName());
        booker.setEmail(booking.getBooker().getEmail());

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(item);
        dto.setBooker(booker);

        return dto;
    }
}