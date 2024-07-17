package de.mteklic.hotelmanager.service.impl;

import de.mteklic.hotelmanager.exception.EndDateBeforeStartDateException;
import de.mteklic.hotelmanager.exception.RoomBookedOutException;
import de.mteklic.hotelmanager.exception.StartAndOrEndDateBeforeNowException;
import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.service.BookingService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingServiceMockImpl implements BookingService {
    private List<Booking> bookings = new ArrayList<>();
    private Long nextBookingId = 1L;

    @Override
    public BookingDto addBooking(Long roomId, BookingDto bookingDto) throws RoomBookedOutException, EndDateBeforeStartDateException, StartAndOrEndDateBeforeNowException {
        // Simulate validation checks
        if (bookingDto.startDate().isBefore(LocalDate.now())) {
            throw new StartAndOrEndDateBeforeNowException(bookingDto.startDate(), bookingDto.endDate());
        }
        if (bookingDto.endDate().isBefore(bookingDto.startDate())) {
            throw new EndDateBeforeStartDateException(bookingDto.startDate(), bookingDto.endDate());
        }

        // Check if room is already booked for the given date range
        List<Booking> conflictingBookings = bookings.stream()
                .filter(b -> b.getRoom().getId().equals(roomId))
                .filter(b -> !(b.getEndDate().isBefore(bookingDto.startDate()) || b.getStartDate().isAfter(bookingDto.endDate())))
                .toList();

        if (!conflictingBookings.isEmpty()) {
            throw new RoomBookedOutException(roomId, bookingDto.startDate(), bookingDto.endDate());
        }

        // Create new booking
        Booking newBooking = Booking.builder()
                .id(nextBookingId++)
                .room(Room.builder().id(roomId).build())
                .startDate(bookingDto.startDate())
                .endDate(bookingDto.endDate())
                .build();

        bookings.add(newBooking);

        return convertToDto(newBooking);
    }

    @Override
    public RoomDto retrieveRoom(Long roomId) {
        // This method is for demonstration, not implemented in mock
        return null;
    }

    @Override
    public List<BookingDto> getBookingsByRoomId(Long roomId) {
        // This method is for demonstration, not implemented in mock
        return null;
    }

    @Override
    public BookingDto getBookingById(Long id) {
        // This method is for demonstration, not implemented in mock
        return null;
    }

    @Override
    public List<Booking> getAvailableBookings(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        // This method is for demonstration, not implemented in mock
        return null;
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) throws StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, RoomBookedOutException {
        // This method is for demonstration, not implemented in mock
        return null;
    }

    @Override
    public void deleteBooking(Long id) {
        // This method is for demonstration, not implemented in mock
    }

    @Override
    public BookingDto convertToDto(Booking booking) {
        // This method is for demonstration, not implemented in mock
        return null;
    }
}