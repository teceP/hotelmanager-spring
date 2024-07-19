package de.mteklic.hotelmanager.exception;

import java.time.LocalDate;

/**
 * Exception thrown when a start date or end date is before the current date.
 */
public class StartAndOrEndDateNullException extends Exception {

    /**
     * Constructs a StartAndOrEndDateBeforeNowException with the specified start date and end date.
     *
     * @param startDate the start date that is before the current date
     * @param endDate   the end date that is before the current date
     */
    public StartAndOrEndDateNullException(LocalDate startDate, LocalDate endDate) {
        super(String.format("Start date or end date is null. Start: %s, End: %s", startDate, endDate));
    }
}
