package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}