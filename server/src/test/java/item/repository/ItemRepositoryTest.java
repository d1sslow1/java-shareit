package item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(String name, String description, boolean available, Long ownerId, Long requestId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        item.setRequestId(requestId);
        return item;
    }

    @Test
    void findByOwnerIdOrderById_shouldReturnUserItems() {

        User owner = createUser("owner", "owner@email.com");
        em.persist(owner);

        Item item1 = createItem("item1", "desc1", true, owner.getId(), null);
        Item item2 = createItem("item2", "desc2", true, owner.getId(), null);

        em.persist(item1);
        em.persist(item2);
        em.flush();

        List<Item> result = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertEquals(2, result.size());
        assertEquals("item1", result.get(0).getName());
        assertEquals("item2", result.get(1).getName());
    }

    @Test
    void searchAvailableItems_whenTextMatches_shouldReturnMatchingItems() {

        User owner = createUser("owner", "owner@email.com");
        em.persist(owner);

        Item item1 = createItem("Power Drill", "Electric power drill", true, owner.getId(), null);
        Item item2 = createItem("Hammer", "Heavy hammer", true, owner.getId(), null);
        Item item3 = createItem("Broken Drill", "Doesn't work", false, owner.getId(), null);

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }
}