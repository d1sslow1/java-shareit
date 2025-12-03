package booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserialize() throws Exception {

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 12, 0);
        BookingRequestDto dto = new BookingRequestDto(1L, start, end);

        String json = objectMapper.writeValueAsString(dto);
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(deserialized.getItemId()).isEqualTo(1L);
        assertThat(deserialized.getStart()).isEqualTo(start);
        assertThat(deserialized.getEnd()).isEqualTo(end);
    }
}