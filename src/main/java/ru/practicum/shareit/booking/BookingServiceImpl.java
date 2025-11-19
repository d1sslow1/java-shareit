package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingRequestDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));


        if (!item.getAvailable()) {
            throw new RuntimeException("Item is not available");
        }


        if (item.getOwnerId().equals(bookerId)) {
            throw new RuntimeException("You cannot book your own item");
        }

        Booking booking = bookingMapper.toEntity(bookingRequestDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Only item owner can approve booking");
        }


        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwnerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        return filterBookingsByState(bookings, state).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        return filterBookingsByState(bookings, state).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case "ALL":
            default:
                return bookings;
        }
    }
}