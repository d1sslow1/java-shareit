package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH = "/{itemId}";
    private static final String COMMENT_PATH = "/comment";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.create(itemDto, ownerId);
    }

    @GetMapping(ITEM_ID_PATH)
    public ItemWithBookingsDto getById(@PathVariable Long itemId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getByIdWithBookings(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getAllByOwnerIdWithBookings(ownerId);
    }

    @PatchMapping(ITEM_ID_PATH)
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping(ITEM_ID_PATH + COMMENT_PATH)
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader(USER_ID_HEADER) Long authorId) {
        return itemService.addComment(itemId, commentDto, authorId);
    }
}