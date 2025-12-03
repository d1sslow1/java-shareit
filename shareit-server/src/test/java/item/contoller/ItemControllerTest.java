package item.contoller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ru.practicum.shareit.item.service.ItemService itemService;

    @MockBean
    private ru.practicum.shareit.item.mapper.ItemMapper itemMapper;

    @Test
    void createItem_shouldReturnCreated() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        ru.practicum.shareit.item.model.Item item = new ru.practicum.shareit.item.model.Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(1L);

        when(itemMapper.toEntity(any(ItemDto.class))).thenReturn(item);
        when(itemService.create(any(), eq(1L))).thenReturn(item);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Item");
        responseDto.setDescription("Description");
        responseDto.setAvailable(true);
        responseDto.setOwnerId(1L);
        when(itemMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated());
    }
}