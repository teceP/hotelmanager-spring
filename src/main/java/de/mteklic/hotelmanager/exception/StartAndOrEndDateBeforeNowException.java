package de.mteklic.hotelmanager.exception;

import java.time.LocalDate;

/**
 * Exception thrown when a start date or end date is before the current date.
 */
public class StartAndOrEndDateBeforeNowException extends Exception {

    /**
     * Constructs a StartAndOrEndDateBeforeNowException with the specified start date and end date.
     *
     * @param startDate the start date that is before the current date
     * @param endDate   the end date that is before the current date
     */
    public StartAndOrEndDateBeforeNowException(LocalDate startDate, LocalDate endDate) {
        super(String.format("Start date %s or end date %s is before now.", startDate, endDate));
    }
}
