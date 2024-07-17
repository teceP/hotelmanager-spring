package de.mteklic.hotelmanager.exception;

import java.time.LocalDate;

/**
 * Exception thrown when the end date is before the start date in a booking operation.
 */
public class EndDateBeforeStartDateException extends Exception {

    /**
     * Constructs an EndDateBeforeStartDateException with the specified start date and end date.
     *
     * @param startDate the start date of the booking
     * @param endDate   the end date of the booking
     */
    public EndDateBeforeStartDateException(LocalDate startDate, LocalDate endDate) {
        super(String.format("End date %s is before end date %s.", endDate, startDate));
    }
}
