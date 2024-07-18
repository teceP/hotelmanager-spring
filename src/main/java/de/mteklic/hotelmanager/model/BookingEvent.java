package de.mteklic.hotelmanager.model;

import de.mteklic.hotelmanager.model.dto.BookingDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookingEvent extends ApplicationEvent {

    private final BookingDto bookingDto;

    public BookingEvent(Object source, BookingDto bookingDto) {
        super(source);
        this.bookingDto = bookingDto;
    }
}
