package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User create(User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public User getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User update(Long id, User userUpdate) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userUpdate.getEmail() != null && !userUpdate.getEmail().equals(existingUser.getEmail())) {

            if (userRepository.findByEmail(userUpdate.getEmail()).isPresent()) {
                throw new EmailConflictException("Email already exists");
            }
            existingUser.setEmail(userUpdate.getEmail());
        }

        if (userUpdate.getName() != null) {
            existingUser.setName(userUpdate.getName());
        }

        User updatedUser = userRepository.save(existingUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}