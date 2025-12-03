package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    Item create(Item item, Long ownerId);

    Item getById(Long id, Long userId);

    List<Item> getAllByOwnerId(Long ownerId);

    Item update(Long id, Item item, Long ownerId);

    List<Item> search(String text);

    Comment addComment(Long itemId, String text, Long authorId);

    List<Comment> getCommentsByItemId(Long itemId);
}