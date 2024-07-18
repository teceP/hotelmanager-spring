package de.mteklic.hotelmanager.notification;

import de.mteklic.hotelmanager.model.BookingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Demonstrates a notification listener
 */
@Component
public class SmsNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);

    /**
     * Sends a log message, when a new booking event has been received.
     * Would be replaced with SMS implementation in real world.
     * @param event The event contains the created BookingDto.
     */
    @EventListener
    public void handleBookingEvent(BookingEvent event){
        log.info("A booking has been created! Booking: {} - Send SMS as verification to user.", event.getBookingDto());
    }
}
