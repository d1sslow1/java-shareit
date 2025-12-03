package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());

        Item savedItem = itemService.create(item, ownerId);
        return toDto(savedItem);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.getById(itemId, userId);

        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());

        if (!item.getOwnerId().equals(userId)) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        } else {
            List<Booking> itemBookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
            LocalDateTime now = LocalDateTime.now();

            Booking lastBooking = itemBookings.stream()
                    .filter(b -> b.getEnd().isBefore(now) ||
                            (b.getStart().isBefore(now) && b.getEnd().isAfter(now)))
                    .findFirst()
                    .orElse(null);

            if (lastBooking != null) {
                ItemWithBookingsDto.BookingInfo lastBookingInfo = new ItemWithBookingsDto.BookingInfo();
                lastBookingInfo.setId(lastBooking.getId());
                lastBookingInfo.setBookerId(lastBooking.getBooker().getId());
                lastBookingInfo.setStart(lastBooking.getStart());
                lastBookingInfo.setEnd(lastBooking.getEnd());
                dto.setLastBooking(lastBookingInfo);
            } else {
                dto.setLastBooking(null);
            }

            Booking nextBooking = itemBookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .findFirst()
                    .orElse(null);

            if (nextBooking != null) {
                ItemWithBookingsDto.BookingInfo nextBookingInfo = new ItemWithBookingsDto.BookingInfo();
                nextBookingInfo.setId(nextBooking.getId());
                nextBookingInfo.setBookerId(nextBooking.getBooker().getId());
                nextBookingInfo.setStart(nextBooking.getStart());
                nextBookingInfo.setEnd(nextBooking.getEnd());
                dto.setNextBooking(nextBookingInfo);
            } else {
                dto.setNextBooking(null);
            }
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
        dto.setComments(commentDtos);

        return dto;
    }
    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwnerId(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item itemUpdate = new Item();
        itemUpdate.setName(itemDto.getName());
        itemUpdate.setDescription(itemDto.getDescription());
        itemUpdate.setAvailable(itemDto.getAvailable());
        itemUpdate.setRequestId(itemDto.getRequestId());

        Item updatedItem = itemService.update(itemId, itemUpdate, ownerId);
        return toDto(updatedItem);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        Comment comment = itemService.addComment(itemId, commentDto.getText(), authorId);
        return toCommentDto(comment);
    }

    @GetMapping("/{itemId}/comments")
    public List<CommentDto> getComments(@PathVariable Long itemId) {
        return itemService.getCommentsByItemId(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);
        dto.setCreated(comment.getCreated());
        return dto;
    }
}