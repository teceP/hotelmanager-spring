package de.mteklic.hotelmanager.controller;

import de.mteklic.hotelmanager.exception.StartAndOrEndDateBeforeNowException;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.service.impl.BookingServiceImpl;
import de.mteklic.hotelmanager.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(locations = {"classpath:application-test.properties"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingControllerIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger(BookingControllerIntegrationTests.class);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingServiceImpl bookingServiceMock;

    @MockBean
    private RoomServiceImpl roomService;

    @Test
    public void testGetBookingsByRoomId() throws Exception {
        Long roomId = 1L;

        when(bookingServiceMock.getBookingsByRoomId(roomId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/bookings")
                        .param("roomId", roomId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingServiceMock, times(1)).getBookingsByRoomId(roomId);
    }

    @Test
    public void testGetBookingById() throws Exception {
        Long bookingId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(7);
        BookingDto bookingDto = new BookingDto(bookingId, startDate, endDate);

        when(bookingServiceMock.getBookingById(bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingServiceMock, times(1)).getBookingById(bookingId);
    }

    @Test
    public void testDeleteBookingById() throws Exception {
        Long bookingId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(7);
        BookingDto bookingDto = new BookingDto(bookingId, startDate, endDate);
        RoomDto roomDto = new RoomDto(1L, "Test Room", "", true, RoomSize.SUITE, Collections.emptyList());

        when(roomService.getRoom(anyLong())).thenReturn(roomDto);
        bookingServiceMock.addBooking(1L, bookingDto);

        mockMvc.perform(delete("/api/v1/bookings/{id}", bookingId))
                .andExpect(status().isNoContent());

        verify(bookingServiceMock, times(1)).deleteBooking(bookingId);
    }

    @Test
    public void testAddBooking_InvalidDates() throws Exception {
        Long bookingId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(7);
        BookingDto bookingDto = new BookingDto(bookingId, startDate, endDate);
        when(bookingServiceMock.addBooking(anyLong(), any(BookingDto.class))).thenThrow(StartAndOrEndDateBeforeNowException.class);

        mockMvc.perform(post("/api/v1/bookings/{roomId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingServiceMock, times(1)).addBooking(anyLong(), any(BookingDto.class));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BookingDto fromJsonString(final String str) {
        try {
            return new ObjectMapper().readValue(str, BookingDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

