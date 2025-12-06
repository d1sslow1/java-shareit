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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item create(Item item, Long ownerId) {
        // Проверяем существование пользователя
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Устанавливаем ownerId (ВАЖНО!)
        item.setOwnerId(ownerId);

        // Сохраняем
        return itemRepository.save(item);
    }

    @Override
    public Item getById(Long id, Long userId) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }

    @Override
    public List<Item> getAllByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerIdOrderById(ownerId);
    }

    @Override
    @Transactional
    public Item update(Long id, Item itemUpdate, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!ownerId.equals(existingItem.getOwnerId())) {
            throw new ItemAccessDeniedException("Only owner can update item");
        }

        if (itemUpdate.getName() != null) {
            existingItem.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            existingItem.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            existingItem.setAvailable(itemUpdate.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text);
    }

    @Override
    @Transactional
    public Comment addComment(Long itemId, String text, Long authorId) {
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
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId);
    }
}