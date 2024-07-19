package de.mteklic.hotelmanager.controller.impl;

import de.mteklic.hotelmanager.controller.BookingController;
import de.mteklic.hotelmanager.exception.*;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.service.impl.BookingServiceImpl;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller implementation for managing bookings in a hotel management system.
 */
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingControllerImpl implements BookingController {

    private final BookingServiceImpl bookingServiceImpl;

    public BookingControllerImpl(BookingServiceImpl bookingServiceImpl){
        this.bookingServiceImpl = bookingServiceImpl;
    }

    @Override
    public ResponseEntity<BookingDto> createBooking(@PathVariable("roomId") Long roomId, @RequestBody BookingDto bookingDto) throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, StartAndOrEndDateNullException {
        return ResponseEntity.ok(this.bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Override
    public ResponseEntity<List<BookingDto>> getBookingsByRoomId(@RequestParam("roomId") Long roomId) {
        return ResponseEntity.ok(this.bookingServiceImpl.getBookingsByRoomId(roomId));
    }

    @Override
    public ResponseEntity<BookingDto> getBookingsById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.bookingServiceImpl.getBookingById(id));
    }

    @Override
    public ResponseEntity<BookingDto> updateBooking(@RequestBody BookingDto bookingDto) throws StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, RoomBookedOutException {
        return ResponseEntity.ok(this.bookingServiceImpl.updateBooking(bookingDto));
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        this.bookingServiceImpl.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
