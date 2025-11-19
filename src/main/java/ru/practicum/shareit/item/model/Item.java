package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
}