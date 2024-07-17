package de.mteklic.hotelmanager.controller;

import de.mteklic.hotelmanager.exception.EndDateBeforeStartDateException;
import de.mteklic.hotelmanager.exception.RoomBookedOutException;
import de.mteklic.hotelmanager.exception.StartAndOrEndDateBeforeNowException;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.service.impl.BookingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller interface for managing bookings in a hotel management system.
 * Interface makes multiple api-Version of controllers possible. Also, documentation in an interface looks way prettier.
 */
public interface BookingController {

    /**
     * Endpoint to add a booking for a specific room.
     *
     * @param roomId    ID of the room to book.
     * @param bookingDto bookingDto which contains start & endDate
     * @return ResponseEntity containing the updated BookingDto after adding the booking.
     * @throws RoomBookedOutException              If the room is already booked for the specified dates.
     * @throws StartAndOrEndDateBeforeNowException If start or end date is before the current date.
     * @throws EndDateBeforeStartDateException     If the end date is before the start date.
     */
    @PostMapping("/{roomId}")
    ResponseEntity<BookingDto> addBooking(@PathVariable("roomId") Long roomId, @RequestBody BookingDto bookingDto) throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException;

    /**
     * Endpoint to update a booking for a specific room.
     *
     * @param bookingDto BookingDto with changed start AND end date. Both should be set, even if only one changes.
     * @return ResponseEntity containing the updated BookingDto after adding the booking.
     * @throws RoomBookedOutException              If the room is already booked for the specified dates.
     * @throws StartAndOrEndDateBeforeNowException If start or end date is before the current date.
     * @throws EndDateBeforeStartDateException     If the end date is before the start date.
     */
    @PutMapping
    ResponseEntity<BookingDto> updateBooking(@RequestBody BookingDto bookingDto) throws StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, RoomBookedOutException;

    /**
     * Endpoint to retrieve all bookings for a specific room.
     *
     * @param roomId ID of the room.
     * @return ResponseEntity containing a list of BookingDto for the specified room.
     */
    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingsByRoomId(@RequestParam("roomId") Long roomId);

    /**
     * Endpoint to retrieve a specific booking by its ID.
     *
     * @param id ID of the booking.
     * @return ResponseEntity containing the BookingDto for the specified ID.
     */
    @GetMapping("/{id}")
    ResponseEntity<BookingDto> getBookingsById(@PathVariable("id") Long id);

    /**
     * Endpoint to delete a booking by its ID.
     *
     * @param id ID of the booking to delete.
     * @return ResponseEntity indicating success or failure of the deletion operation.
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable("id") Long id);
}
