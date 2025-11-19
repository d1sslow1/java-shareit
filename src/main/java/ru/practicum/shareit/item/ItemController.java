package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.create(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByIdWithBookings(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwnerIdWithBookings(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemService.addComment(itemId, commentDto, authorId);
    }
}