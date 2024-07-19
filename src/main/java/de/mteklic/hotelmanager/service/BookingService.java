package de.mteklic.hotelmanager.service;


import de.mteklic.hotelmanager.exception.EndDateBeforeStartDateException;
import de.mteklic.hotelmanager.exception.RoomBookedOutException;
import de.mteklic.hotelmanager.exception.StartAndOrEndDateBeforeNowException;
import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    /**
     * Adds a new booking for a room.
     *
     * @param roomId     ID of the room to be booked.
     * @param bookingDto contains start & endDate, id should be null
     * @return bookingDto representing the booking
     * @throws RoomBookedOutException              If the room is already booked for the given date range.
     * @throws EndDateBeforeStartDateException     If the end date is before the start date.
     * @throws StartAndOrEndDateBeforeNowException If either the start date or end date is before the current date.
     */
    BookingDto createBooking(Long roomId, BookingDto bookingDto) throws RoomBookedOutException, EndDateBeforeStartDateException, StartAndOrEndDateBeforeNowException;

    /**
     * Retrieves room details by its ID.
     *
     * @param roomId ID of the room to retrieve.
     * @return RoomDto representing the retrieved room.
     * @throws ResponseStatusException If the room with the specified ID is not found.
     */
    RoomDto retrieveRoom(Long roomId);

    /**
     * Retrieves all bookings for a specified room.
     *
     * @param roomId ID of the room.
     * @return List of BookingDto representing bookings for the room.
     */
    List<BookingDto> getBookingsByRoomId(Long roomId);

    /**
     * Retrieves a booking by its ID.
     *
     * @param id ID of the booking.
     * @return BookingDto representing the retrieved booking.
     * @throws ResponseStatusException If the booking with the specified ID is not found.
     */
    BookingDto getBookingById(Long id);

    /**
     * Retrieves all overlapping bookings based on optional parameters.
     *
     * @param roomIds   Optional list of room IDs to filter bookings.
     * @param startDate Optional start date of the booking period.
     * @param endDate   Optional end date of the booking period.
     * @return List of Booking representing available bookings that match the specified criteria.
     */
    List<Booking> getUnavailableBookings(List<Long> roomIds, LocalDate startDate, LocalDate endDate);

    public BookingDto updateBooking(BookingDto bookingDto) throws StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, RoomBookedOutException;

    /**
     * Deletes a booking by its ID.
     *
     * @param id ID of the booking to delete.
     * @throws ResponseStatusException If the booking with the specified ID is not found.
     */
    void deleteBooking(Long id);

    /**
     * Converts a Booking entity to BookingDto.
     *
     * @param booking Booking entity to convert.
     * @return BookingDto representing the converted booking.
     */
    BookingDto convertToDto(Booking booking);
}
