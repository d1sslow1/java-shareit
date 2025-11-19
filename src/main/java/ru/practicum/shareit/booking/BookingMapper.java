package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
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

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto.Booker booker = new BookingResponseDto.Booker(
                booking.getBooker().getId(),
                booking.getBooker().getName(),
                booking.getBooker().getEmail()
        );

        BookingResponseDto.Item item = new BookingResponseDto.Item(
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getItem().getDescription(),
                booking.getItem().getAvailable(),
                booking.getItem().getOwnerId(),
                booking.getItem().getRequestId()
        );

        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booker,
                item
        );
    }
}