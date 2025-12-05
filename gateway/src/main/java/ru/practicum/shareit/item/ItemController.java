package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH = "/{itemId}";
    private static final String COMMENT_PATH = "/comment";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.create(itemDto, ownerId);
    }

    @GetMapping(ITEM_ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable Long itemId,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.getAllByOwnerId(ownerId);
    }

    @PatchMapping(ITEM_ID_PATH)
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping(ITEM_ID_PATH + COMMENT_PATH)
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto,
                                             @RequestHeader(USER_ID_HEADER) Long authorId) {
        return itemClient.addComment(itemId, commentDto, authorId);
    }
}