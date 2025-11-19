package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemMapper.toEntity(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemWithBookingsDto getByIdWithBookings(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        ItemWithBookingsDto itemWithBookings = itemMapper.toItemWithBookingsDto(item);

        if (item.getOwnerId().equals(userId)) {
            addBookingInfo(itemWithBookings, item.getId());
        }

        addCommentsInfo(itemWithBookings, item.getId());

        return itemWithBookings;
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwnerIdWithBookings(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemWithBookingsDto itemWithBookings = itemMapper.toItemWithBookingsDto(item);
                    addBookingInfo(itemWithBookings, item.getId());

                    List<Comment> comments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    List<CommentDto> commentDtos = comments.stream()
                            .map(commentMapper::toDto)
                            .collect(Collectors.toList());
                    itemWithBookings.setComments(commentDtos);

                    return itemWithBookings;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!ownerId.equals(existingItem.getOwnerId())) {
            throw new ItemAccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new RuntimeException("Name cannot be blank");
            }
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new RuntimeException("Description cannot be blank");
            }
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        List<Booking> userBookings = bookingRepository.findByBookerIdOrderByStartDesc(authorId);
        boolean hasBooked = userBookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()));

        if (!hasBooked) {
            throw new CommentValidationException("You can only comment on items you have booked in the past");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    private void addBookingInfo(ItemWithBookingsDto itemWithBookings, Long itemId) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> pastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByStartDesc(itemId, now);
        if (!pastBookings.isEmpty()) {
            Booking lastBooking = pastBookings.get(0);
            itemWithBookings.setLastBooking(new ItemWithBookingsDto.BookingInfo(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            ));
        }

        List<Booking> futureBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartDesc(itemId, now);
        if (!futureBookings.isEmpty()) {
            Booking nextBooking = futureBookings.get(0);
            itemWithBookings.setNextBooking(new ItemWithBookingsDto.BookingInfo(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd()
            ));
        }
    }

    private void addCommentsInfo(ItemWithBookingsDto itemWithBookings, Long itemId) {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        itemWithBookings.setComments(commentDtos);
    }
}