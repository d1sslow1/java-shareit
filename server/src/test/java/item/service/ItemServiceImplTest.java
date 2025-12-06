package item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_whenUserNotFound_shouldThrowException() {

        Item item = new Item(null, "item", "desc", true, null, null);
        Long ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.create(item, ownerId));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_whenUserExists_shouldSaveItem() {
        Item item = new Item(null, "item", "desc", true, null, null);
        Item itemWithOwner = new Item(null, "item", "desc", true, 1L, null);
        Item savedItem = new Item(1L, "item", "desc", true, 1L, null);
        Long ownerId = 1L;
        User owner = new User(ownerId, "owner", "owner@email.com");

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(itemWithOwner)).thenReturn(savedItem);
        Item result = itemService.create(itemWithOwner, ownerId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ownerId, result.getOwnerId());
        verify(itemRepository).save(itemWithOwner);
    }

    @Test
    void create_whenItemHasNoOwnerId_shouldSetOwnerIdAndSave() {
        Item itemWithoutOwner = new Item(null, "item", "desc", true, null, null);
        Item itemWithOwner = new Item(null, "item", "desc", true, 1L, null);
        Item savedItem = new Item(1L, "item", "desc", true, 1L, null);
        Long ownerId = 1L;
        User owner = new User(ownerId, "owner", "owner@email.com");

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item itemToSave = invocation.getArgument(0);
            itemToSave.setId(1L);
            return itemToSave;
        });

        Item result = itemService.create(itemWithoutOwner, ownerId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getOwnerId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getById_whenItemNotFound_shouldThrowException() {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> itemService.getById(itemId, userId));
    }
}