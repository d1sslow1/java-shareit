package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    @NotNull(message = "Item ID cannot be null")
    Long itemId;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in present or future")
    LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in future")
    LocalDateTime end;
}