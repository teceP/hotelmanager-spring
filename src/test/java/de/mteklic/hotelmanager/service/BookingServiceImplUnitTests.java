package de.mteklic.hotelmanager.service;

import de.mteklic.hotelmanager.exception.*;
import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.repository.BookingRepository;
import de.mteklic.hotelmanager.service.impl.BookingServiceImpl;
import de.mteklic.hotelmanager.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceImplUnitTests {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImplUnitTests.class);

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomServiceImpl roomServiceImpl;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testCreateBooking_ValidBooking() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        BookingDto bookingDto = new BookingDto(null, startDate, endDate);
        RoomDto roomDto = new RoomDto(roomId, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());

        when(roomServiceImpl.getRoom(roomId)).thenReturn(roomDto);

        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L); // Simulating the saved entity with ID
            return booking;
        });

        BookingDto result = bookingServiceImpl.createBooking(roomId, bookingDto);

        verify(roomServiceImpl).getRoom(roomId); // Verify that getRoom was called with roomId
        verify(bookingRepository).save(any()); // Verify that save was called with any booking
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
        assertNotNull(result.id()); // Ensure ID is set in the result
    }

    @Test
    void testCreateBooking_RoomBookedOutException() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);

        when(roomServiceImpl.getRoom(roomId)).thenReturn(new RoomDto(roomId, "Test Room", "", true, RoomSize.SINGLE, Collections.emptyList()));

        List<Booking> existingBookings = someBookings();
        when(bookingRepository.findAllByRoomId(roomId)).thenReturn(existingBookings);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);
        assertThrows(RoomBookedOutException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_EndDateBeforeStartDateException() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(1);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(EndDateBeforeStartDateException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_StartAndOrEndDateBeforeNowException() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(StartAndOrEndDateBeforeNowException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_StartDateEqualsEndDate() {
        Long roomId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        RoomDto roomDto = new RoomDto(roomId, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        roomServiceImpl.createRoom(roomDto);
        when(roomServiceImpl.getRoom(roomId)).thenReturn(roomDto);

        BookingDto bookingDto = new BookingDto(null, date, date);

        assertDoesNotThrow(() -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_EndDateBeforeStartDateOneDay() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.minusDays(1);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(EndDateBeforeStartDateException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_EndDateBeforeStartDateLongPeriod() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(1);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(EndDateBeforeStartDateException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_PastStartDateAndEndDate() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(1);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(StartAndOrEndDateBeforeNowException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_StartDateInThePast() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(StartAndOrEndDateBeforeNowException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_EndDateInThePast() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().minusDays(1);

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(StartAndOrEndDateBeforeNowException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_BookingInLeapYear() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        Long roomId = 1L;
        int currentYear = LocalDate.now().getYear() + 1;
        int nextLeapYear = -1;

        while (nextLeapYear == -1){
            if (Year.isLeap(currentYear)){
                nextLeapYear = currentYear;
            }else{
                currentYear++;
            }
        }

        LocalDate startDate = LocalDate.now().withYear(nextLeapYear).withMonth(2).withDayOfMonth(28); // Leap year end of February
        LocalDate endDate = LocalDate.now().withYear(nextLeapYear).withMonth(3).withDayOfMonth(5); // Leap year start of March

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);
        RoomDto roomDto = new RoomDto(roomId, "Leap Year Room", "", true, RoomSize.SUITE, Collections.emptyList());

        when(roomServiceImpl.getRoom(roomId)).thenReturn(roomDto);
        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L); // Simulating the saved entity with ID
            return booking;
        });

        BookingDto result = bookingServiceImpl.createBooking(roomId, bookingDto);

        verify(roomServiceImpl).getRoom(roomId);
        verify(bookingRepository).save(any());
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
    }

    @Test
    void testCreateBooking_BookingInNonLeapYear() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusYears(0).withMonth(12).withDayOfMonth(30); // End of December
        LocalDate endDate = LocalDate.now().plusYears(1).withMonth(1).withDayOfMonth(3); // Start of January

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);
        RoomDto roomDto = new RoomDto(roomId, "Non-Leap Year Room", "", true, RoomSize.SUITE, Collections.emptyList());

        when(roomServiceImpl.getRoom(roomId)).thenReturn(roomDto);

        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L); // Simulating the saved entity with ID
            return booking;
        });

        BookingDto result = bookingServiceImpl.createBooking(roomId, bookingDto);

        verify(roomServiceImpl).getRoom(roomId);
        verify(bookingRepository).save(any());
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
    }

    @Test
    void testCreateBooking_SameStartAndEndDates() {
        Long roomId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        RoomDto roomDto = new RoomDto(roomId, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        roomServiceImpl.createRoom(roomDto);
        when(roomServiceImpl.getRoom(roomId)).thenReturn(roomDto);

        BookingDto bookingDto = new BookingDto(null, date, date);

        assertDoesNotThrow(() -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    void testCreateBooking_MinimumStartDateAndEndDate() {
        Long roomId = 1L;
        LocalDate startDate = LocalDate.MIN;
        LocalDate endDate = LocalDate.MIN;

        BookingDto bookingDto = new BookingDto(null, startDate, endDate);

        assertThrows(StartAndOrEndDateBeforeNowException.class, () -> bookingServiceImpl.createBooking(roomId, bookingDto));
    }

    @Test
    public void testCreateBooking_EndDateTodayStartInFutureEdge(){
        Booking booking = Booking.builder()
                .room(null)
                .startDate(LocalDate.now().plusDays(7)) // future
                .endDate(LocalDate.now().plusDays(0)) // today
                .build();

        BookingDto bookingDto = bookingServiceImpl.convertToDto(booking);
        assertThrows(EndDateBeforeStartDateException.class, () -> bookingServiceImpl.createBooking(1L, bookingDto));
    }

    @Test
    public void testCreateBooking_EndDateTodayStartMinimumInFuture(){
        Booking booking = Booking.builder()
                .room(null)
                .startDate(LocalDate.now().plusDays(7)) // future
                .endDate(LocalDate.now().plusDays(6)) // ealier
                .build();
        BookingDto bookingDto = bookingServiceImpl.convertToDto(booking);
        assertThrows(EndDateBeforeStartDateException.class, () -> bookingServiceImpl.createBooking(1L, bookingDto));
    }

    @Test
    public void testDeleteBookingIsInvoked() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        Room room = Room.builder()
                .id(1L)
                .name("Test Room")
                .description("")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .room(room)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(7))
                .build();

        log.info(room.toString());
        when(bookingServiceImpl.retrieveRoom(ArgumentMatchers.any())).thenReturn(roomDto);
        when(bookingRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingServiceImpl.createBooking(1L, bookingServiceImpl.convertToDto(booking));
        bookingServiceImpl.deleteBooking(bookingDto.id());

        log.info(bookingDto.toString());
        assertNotNull(bookingDto.startDate());
        assertNotNull(bookingDto.endDate());

        verify(bookingRepository, times(1)).findAllByRoomId(1L);
        verify(bookingRepository, times(1)).delete(booking);
    }


    @Test
    public void testDeleteBooking_NotInvokedNonExistentId() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        Room room = Room.builder()
                .id(1L)
                .name("Test Room")
                .description("")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .room(room)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(7))
                .build();

        log.info(room.toString());
        when(bookingServiceImpl.retrieveRoom(ArgumentMatchers.any())).thenReturn(roomDto);

        BookingDto bookingDto = bookingServiceImpl.createBooking(1L, bookingServiceImpl.convertToDto(booking));
        assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.deleteBooking(10000L));

        log.info(bookingDto.toString());
        assertNotNull(bookingDto.startDate());
        assertNotNull(bookingDto.endDate());

        verify(bookingRepository, times(1)).findById(10000L);
        verify(bookingRepository, times(0)).delete(booking);
    }

    @Test
    public void testDeleteBooking_Invoked() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        Room room = Room.builder()
                .id(1L)
                .name("Test Room")
                .description("")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .room(room)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(7))
                .build();

        log.info(room.toString());
        when(bookingServiceImpl.retrieveRoom(ArgumentMatchers.any())).thenReturn(roomDto);
        when(bookingRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingServiceImpl.createBooking(1L, bookingServiceImpl.convertToDto(booking));
        bookingServiceImpl.deleteBooking(10000L); //stubbed

        log.info(bookingDto.toString());
        assertNotNull(bookingDto.startDate());
        assertNotNull(bookingDto.endDate());

        verify(bookingRepository, times(1)).findById(10000L);
        verify(bookingRepository, times(1)).delete(booking);
    }

    @Test
    public void testUpdateBooking_Invoked() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        Room room = Room.builder()
                .id(1L)
                .name("Test Room")
                .description("")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .room(room)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(7))
                .build();

        LocalDate updateStartDate = LocalDate.now().plusDays(11);
        LocalDate updateEndDate = LocalDate.now().plusDays(17);

        BookingDto bookingDto = BookingDto
                .builder()
                .id(1L)
                .startDate(updateStartDate)
                .endDate(updateEndDate)
                .build();

        log.info(room.toString());
        when(bookingServiceImpl.retrieveRoom(ArgumentMatchers.any())).thenReturn(roomDto);
        when(bookingRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(booking));
        when(roomServiceImpl.getRoom(1L)).thenReturn(roomDto);

        BookingDto updatedBookingDto = bookingServiceImpl.updateBooking(bookingDto);

        assertNotNull(bookingDto.startDate());
        assertNotNull(bookingDto.endDate());

        verify(roomServiceImpl, times(1)).getRoom(bookingDto.id());
        verify(bookingRepository, times(1)).findById(1L);
        assertTrue(updateStartDate.isEqual(updatedBookingDto.startDate()));
        assertTrue(updateEndDate.isEqual(updatedBookingDto.endDate()));
    }

    @Test
    public void testUpdateBooking_NotInvokedIdNotPresent() throws RoomBookedOutException, StartAndOrEndDateBeforeNowException, EndDateBeforeStartDateException {
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());
        Room room = Room.builder()
                .id(1L)
                .name("Test Room")
                .description("")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .room(room)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(7))
                .build();

        LocalDate updateStartDate = LocalDate.now().plusDays(11);
        LocalDate updateEndDate = LocalDate.now().plusDays(17);

        BookingDto bookingDto = BookingDto
                .builder()
                .id(1L)
                .startDate(updateStartDate)
                .endDate(updateEndDate)
                .build();

        log.info(room.toString());
        when(bookingServiceImpl.retrieveRoom(ArgumentMatchers.any())).thenReturn(roomDto);
        assertThrows(ResponseStatusException.class, () -> bookingServiceImpl.updateBooking(bookingDto));
    }

    private List<Booking> someBookings() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(LocalDate.now().plusDays(2));
        booking1.setEndDate(LocalDate.now().plusDays(6));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStartDate(LocalDate.now().plusDays(7));
        booking2.setEndDate(LocalDate.now().plusDays(10));

        return List.of(booking1, booking2);
    }
}

