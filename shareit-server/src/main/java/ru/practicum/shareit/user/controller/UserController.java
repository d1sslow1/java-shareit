package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        // Преобразуем DTO в Entity
        ru.practicum.shareit.user.User userEntity = userMapper.toEntity(userDto);
        // Сохраняем в БД
        ru.practicum.shareit.user.User savedUser = userService.create(userEntity);
        // Преобразуем обратно в DTO
        return userMapper.toDto(savedUser);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        ru.practicum.shareit.user.User user = userService.getById(id);
        return userMapper.toDto(user);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        ru.practicum.shareit.user.User userEntity = userMapper.toEntity(userDto);
        ru.practicum.shareit.user.User updatedUser = userService.update(id, userEntity);
        return userMapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}