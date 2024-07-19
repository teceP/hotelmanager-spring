package de.mteklic.hotelmanager.service;

import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.repository.RoomRepository;
import de.mteklic.hotelmanager.service.impl.BookingServiceImpl;
import de.mteklic.hotelmanager.service.impl.HousekeepingService;
import de.mteklic.hotelmanager.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomServiceImplUnitTests {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImplUnitTests.class);

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingServiceImpl bookingServiceImpl;

    @Mock
    private HousekeepingService housekeepingService;

    @InjectMocks
    private RoomServiceImpl roomServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRoom() {
        Room room = Room.builder().id(1L).name("Test Room").build();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomDto roomDto = roomServiceImpl.getRoom(1L);

        assertNotNull(roomDto);
        assertEquals("Test Room", roomDto.name());
    }

    @Test
    public void testGetRoom_NotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> roomServiceImpl.getRoom(1L));
    }

    @Test
    public void testGetAllRooms() {
        Room room1 = Room.builder().id(1L).name("Room 1").build();
        Room room2 = Room.builder().id(2L).name("Room 2").build();
        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        List<RoomDto> rooms = roomServiceImpl.getAllRooms();

        assertEquals(2, rooms.size());
    }

    @Test
    public void testCreateRoom() {
        RoomDto roomDto = RoomDto.builder().name("New Room").build();
        Room room = Room.builder().id(1L).name("New Room").build();

        when(roomRepository.save(any(Room.class))).thenReturn(room);
        RoomDto savedRoomDto = roomServiceImpl.createRoom(roomDto);

        assertNotNull(savedRoomDto);
        assertEquals("New Room", savedRoomDto.name());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void testDeleteRoom() {
        Room room = Room.builder().id(1L).name("Test Room").build();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        doNothing().when(roomRepository).deleteById(1L);

        roomServiceImpl.deleteRoom(1L);

        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteRoom_NotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> roomServiceImpl.deleteRoom(1L));
    }

    @Test
    public void testEditRoom() {
        RoomDto roomDto = RoomDto.builder().id(1L).name("Updated Room").build();
        Room room = Room.builder().id(1L).name("Old Room").build();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        RoomDto updatedRoomDto = roomServiceImpl.editRoom(roomDto);

        assertNotNull(updatedRoomDto);
        assertEquals("Updated Room", updatedRoomDto.name());
    }

    @Test
    public void testHousekeeping() {
        Room room1 = Room.builder().id(1L).name("Room 1").build();
        Room room2 = Room.builder().id(2L).name("Room 2").build();

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        doNothing().when(housekeepingService).tidyUpRoom(any(Room.class));

        roomServiceImpl.housekeeping();

        verify(housekeepingService, times(1)).tidyUpRoom(room1);
        verify(housekeepingService, times(1)).tidyUpRoom(room2);
    }

    @Test
    public void testFilterRoomsNamesOnly() {
        Room room1 = Room.builder().id(1L).name("RoomOne").hasMinibar(true).roomSize(RoomSize.SUITE).build();
        Room room2 = Room.builder().id(2L).name("RoomTwo").hasMinibar(true).roomSize(RoomSize.SUITE).build();
        Room room3 = Room.builder().id(2L).name("RoomThree").hasMinibar(true).roomSize(RoomSize.SUITE).build();

        when(roomRepository.findAll(any(Specification.class))).thenReturn(List.of(room1, room2, room3));
        when(bookingServiceImpl.getUnavailableBookings(any(), any(), any())).thenReturn(List.of(
                Booking.builder().room(room1).build(),
                Booking.builder().room(room2).build()
        ));

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(7);
        roomServiceImpl.filterRooms(null, "RoomOne", null, startDate, endDate, null, null);

        verify(roomRepository, times(1)).findAll(any(Specification.class));
        verify(bookingServiceImpl, times(1)).getUnavailableBookings(any(), any(), any());
    }
}

