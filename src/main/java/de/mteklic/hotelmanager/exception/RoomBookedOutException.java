package de.mteklic.hotelmanager.exception;

import java.time.LocalDate;

/**
 * Exception thrown when a room is already booked within the specified start and end dates.
 */
public class RoomBookedOutException extends Exception {

    /**
     * Constructs a RoomBookedOutException with the specified room ID, start date, and end date.
     *
     * @param id        the ID of the room that is booked out
     * @param startDate the start date of the booking
     * @param endDate   the end date of the booking
     */
    public RoomBookedOutException(Long id, LocalDate startDate, LocalDate endDate) {
        super(String.format("Room with id %d is booked out of start date %s and end date %s.", id, startDate, endDate));
    }
}
