package de.mteklic.hotelmanager.service.impl;

import de.mteklic.hotelmanager.exception.*;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.model.*;
import de.mteklic.hotelmanager.model.BookingEvent;
import de.mteklic.hotelmanager.repository.BookingRepository;
import de.mteklic.hotelmanager.service.BookingService;
import de.mteklic.hotelmanager.service.RoomService;
import de.mteklic.hotelmanager.specification.BookingSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing bookings in a hotel management system.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Prevent Circular dependency injection - Booking relies more on RoomService than otherwhise, that's the reason for setting @Lazy here.
     * Another solution would be to extract methods from both services and create another indepedent one, which both could @Autowire.
     * Since there is not a need for more methods which the indedepdent Service could implement, this might be an overkill and would (kind of) violate against YAGNI principles if
     * someone decides to fill up the class with methods which might be needful in the future.
     */
    private RoomService roomService;

    private final RoomReadOnlyRepository roomReadOnlyRepository;

    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository, RoomService roomService, ApplicationEventPublisher eventPublisher, RoomReadOnlyRepository roomReadOnlyRepository){
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.eventPublisher = eventPublisher;
        this.roomReadOnlyRepository = roomReadOnlyRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long roomId, BookingDto bookingDto) throws RoomBookedOutException, EndDateBeforeStartDateException, StartAndOrEndDateBeforeNowException {
        // Validate booking dates
        isDatesLegal(bookingDto.startDate(), bookingDto.endDate());

        // Retrieve room details without directly accessing RoomService
        RoomDto roomDto = retrieveRoom(roomId);

         // Check if the room is available for booking
         isBookingAvailable(roomDto.id(), bookingDto.startDate(), bookingDto.endDate());

        // Convert RoomDto to Room entity, so its passable into the booking object
        Room room = Room.builder().id(roomDto.id())
                .name(roomDto.name())
                .description(roomDto.description())
                .hasMinibar(roomDto.hasMinibar())
                .roomSize(roomDto.roomSize())
                //.bookings(new ArrayList<>())
                .build();

        // Create booking entity
        Booking booking = Booking.builder()
                .room(room)
                .startDate(bookingDto.startDate())
                .endDate( bookingDto.endDate())
                .build();

        // Save the booking
        this.bookingRepository.save(booking);

        // Publish booking event
        BookingDto savedBookingDto = convertToDto(booking);
        eventPublisher.publishEvent(new BookingEvent(this, savedBookingDto));

        return savedBookingDto;
    }

    public Room retrieveRoomR(Long roomId) {
        return roomReadOnlyRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
    }

    @Override
    public RoomDto retrieveRoom(Long roomId) {
        return roomService.getRoom(roomId);
    }

    /**
     * Checks if the booking dates are legal.
     *
     * @param startDate Start date of the booking.
     * @param endDate   End date of the booking.
     * @throws EndDateBeforeStartDateException     If the end date is before the start date.
     * @throws StartAndOrEndDateBeforeNowException If either the start date or end date is before the current date.
     */
    private void isDatesLegal(LocalDate startDate, LocalDate endDate) throws EndDateBeforeStartDateException, StartAndOrEndDateBeforeNowException {
        LocalDate now = LocalDate.now();

        // Check if start or end dates are before the current date
        if (startDate.isBefore(now) || endDate.isBefore(now)) {
            throw new StartAndOrEndDateBeforeNowException(startDate, endDate);
        }

        // Check if end date is before start date
        if (endDate.isBefore(startDate)) {
            throw new EndDateBeforeStartDateException(startDate, endDate);
        }
    }

    /**
     * Checks if the room is available for booking in the specified date range.
     *
     * @param roomId   Room ID of the object representing the room to be checked.
     * @param startDate Start date of the booking.
     * @param endDate   End date of the booking.
     * @throws RoomBookedOutException If the room is already booked for the given date range.
     */
    private void isBookingAvailable(Long roomId, LocalDate startDate, LocalDate endDate) throws RoomBookedOutException {
        List<Booking> bookings = this.bookingRepository.findAllByRoomId(roomId);

        // Check fir overlapping bookings
        if (!bookings.isEmpty() && hasAnyOverlap(bookings, startDate, endDate)) {
            throw new RoomBookedOutException(roomId, startDate, endDate);
        }
    }

    /**
     * Returns all bookings, which overlapp with the provided dates.
     * @param bookings BookingDtos
     * @param startDate startDate
     * @param endDate endDate
     * @return  Filtered list of bookings which only overlap with provided dates.
     */
    private List<BookingDto> getAllOverlaps(List<BookingDto> bookings, LocalDate startDate, LocalDate endDate) {
        return bookings
                .stream()
                .filter(b -> ((startDate.isBefore(b.endDate()) || startDate.isEqual(b.endDate())) &&
                        ((endDate.isAfter(b.startDate()) || endDate.isEqual(b.startDate()))))).toList();
    }

    /**
     * Checks if there is any overlap between existing bookings and the specified date range.
     *
     * @param bookings  List of existing bookings for the room.
     * @param startDate Start date of the booking.
     * @param endDate   End date of the booking.
     * @return true if there is any overlap, false otherwise.
     */
    private boolean hasAnyOverlap(List<Booking> bookings, LocalDate startDate, LocalDate endDate) {
        return bookings
                .stream()
                .anyMatch(b -> ((startDate.isBefore(b.getEndDate()) || startDate.isEqual(b.getEndDate())) &&
                        ((endDate.isAfter(b.getStartDate()) || endDate.isEqual(b.getStartDate()))))
                );
    }

    @Override
    public List<BookingDto> getBookingsByRoomId(Long roomId) {
        return this.bookingRepository
                .findAllByRoomId(roomId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Long id) {
        return convertToDto(this.bookingRepository
                .findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found.")));
    }

    @Override
    public List<Booking> getUnavailableBookings(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        Specification<Booking> specification = Specification.where(null);

        // Create spec for specific ids if specified
        if (!roomIds.isEmpty()) {
            specification.and(BookingSpecifications.hasRoomIds(roomIds));
        }

        // Create spec for specific time range, which gets all unavailable bookings
        if (startDate != null && endDate != null) {
            specification.and(BookingSpecifications.hasOverlap(startDate, endDate));
        }

        // Fetch unavailable bookings based on the criteria
        return this.bookingRepository.findAll(specification);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(BookingDto bookingDto) throws StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException, RoomBookedOutException {
        Booking booking = this.bookingRepository.findById(bookingDto.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));

        // Validate booking dates
        isDatesLegal(bookingDto.startDate(), bookingDto.endDate());

        //Retrieve room details
        RoomDto roomDto = roomService.getRoom(booking.getRoom().getId());

        // Get all overlaps in new mutable list
        List<BookingDto> allOverlaps = new ArrayList<>(getAllOverlaps(roomDto.bookings(), bookingDto.startDate(), bookingDto.endDate()));

        // Remove old booking by id, but not if booking id matches && dates are same
        allOverlaps.removeIf(b -> (b.id().longValue() == bookingDto.id().longValue() && (!b.startDate().isEqual(bookingDto.startDate()) && b.endDate().isEqual(bookingDto.endDate()))));

        // Check if more overlaps contained
        if (allOverlaps.isEmpty()) {

            // Update start & end date
            booking.setStartDate(bookingDto.startDate());
            booking.setEndDate(bookingDto.endDate());
            return convertToDto(booking);
        } else {
            throw new RoomBookedOutException(booking.getRoom().getId(), bookingDto.startDate(), bookingDto.endDate());
        }
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = this.bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));
        this.bookingRepository.delete(booking);
    }

    @Override
    public BookingDto convertToDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStartDate(), booking.getEndDate());
    }
}
