package de.mteklic.hotelmanager.controller;

import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(locations = {"classpath:application-test.properties"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomControllerIntegrationTests {

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
    private RoomServiceImpl roomServiceMock;

    private RoomDto roomDto1;
    private RoomDto roomDto2;
    private RoomDto roomDto3;

    @BeforeEach
    public void setup() {
        roomDto1 = new RoomDto(1L, "Room One", "Description One", true, RoomSize.SINGLE, new ArrayList<>());
        roomDto2 = new RoomDto(2L, "Room Two", "Description Two", true, RoomSize.DOUBLE, new ArrayList<>());
        roomDto3 = new RoomDto(3L, "Room Three", "Description Three", false, RoomSize.SUITE, new ArrayList<>());
    }

    @Test
    public void testGetAllRooms() throws Exception {
        List<RoomDto> rooms = Arrays.asList(roomDto1, roomDto2, roomDto3);

        when(roomServiceMock.getAllRooms()).thenReturn(rooms);

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Room One"))
                .andExpect(jsonPath("$[1].name").value("Room Two"))
                .andExpect(jsonPath("$[2].name").value("Room Three"));

        verify(roomServiceMock, times(1)).getAllRooms();
    }

    @Test
    public void testGetHotelRoom() throws Exception {
        Long roomId = 1L;

        when(roomServiceMock.getRoom(roomId)).thenReturn(roomDto1);

        mockMvc.perform(get("/api/v1/rooms/{id}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Room One"));

        verify(roomServiceMock, times(1)).getRoom(roomId);
    }

    @Test
    public void testFilterRooms() throws Exception {
        when(roomServiceMock.filterRooms(null, "Room Two", null, null, null, null, null))
                .thenReturn(Collections.singletonList(roomDto2));

        mockMvc.perform(get("/api/v1/rooms/filter")
                        .param("name", "Room Two"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Room Two"));

        verify(roomServiceMock, times(1)).filterRooms(null, "Room Two", null, null, null, null, null);
    }

    @Test
    public void testCreateRoom() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String roomDtoJson = objectMapper.writeValueAsString(roomDto3);

        when(roomServiceMock.createRoom(any(RoomDto.class))).thenReturn(roomDto3);

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Room Three"));

        verify(roomServiceMock, times(1)).createRoom(any(RoomDto.class));
    }

    @Test
    public void testDeleteById() throws Exception {
        Long roomId = 1L;

        mockMvc.perform(delete("/api/v1/rooms/{id}", roomId))
                .andExpect(status().isNoContent());

        verify(roomServiceMock, times(1)).deleteRoom(roomId);
    }

    @Test
    public void testEditHotelRoom() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String roomDtoJson = objectMapper.writeValueAsString(roomDto1);

        when(roomServiceMock.editRoom(any(RoomDto.class))).thenReturn(roomDto1);

        mockMvc.perform(put("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Room One"));

        verify(roomServiceMock, times(1)).editRoom(any(RoomDto.class));
    }
}

