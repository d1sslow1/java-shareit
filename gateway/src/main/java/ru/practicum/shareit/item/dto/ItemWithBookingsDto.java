package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingInfo lastBooking;
    BookingInfo nextBooking;
    List<CommentDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookingInfo {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }
}