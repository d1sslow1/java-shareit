package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;

    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotBlank(message = "Description cannot be blank")
    String description;

    @NotNull(message = "Available status cannot be null")
    Boolean available;

    Long requestId;
    Long ownerId;
}