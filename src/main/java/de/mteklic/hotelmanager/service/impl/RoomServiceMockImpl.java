package de.mteklic.hotelmanager.service.impl;

import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.service.RoomService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceMockImpl implements RoomService {

    @Override
    public RoomDto getRoom(Long id) {
        // Mock implementation to return a RoomDto
        return RoomDto.builder()
                .id(id)
                .name("Mock Room")
                .description("Mock Room Description")
                .hasMinibar(true)
                .roomSize(RoomSize.SUITE)
                .build();
    }

    @Override
    public List<RoomDto> getAllRooms() {
        // Mock implementation to return a list of RoomDto
        List<RoomDto> rooms = new ArrayList<>();
        rooms.add(RoomDto.builder().id(1L).name("Room 1").build());
        rooms.add(RoomDto.builder().id(2L).name("Room 2").build());
        rooms.add(RoomDto.builder().id(3L).name("Room 3").build());
        return rooms;
    }

    @Override
    public RoomDto addRoom(RoomDto roomDto) {
        // Mock implementation to add a room and return the added RoomDto
        return RoomDto.builder()
                .id(roomDto.id())
                .name(roomDto.name())
                .description(roomDto.description())
                .hasMinibar(roomDto.hasMinibar())
                .roomSize(roomDto.roomSize())
                .build();
    }

    @Override
    public void deleteRoom(Long id) {
        // Mock implementation to delete a room
        System.out.println("Deleting room with ID: " + id);
    }

    @Override
    public RoomDto editRoom(RoomDto roomDto) {
        // Mock implementation to edit a room and return the updated RoomDto
        return RoomDto.builder()
                .id(roomDto.id())
                .name(roomDto.name())
                .description(roomDto.description())
                .hasMinibar(roomDto.hasMinibar())
                .roomSize(roomDto.roomSize())
                .build();
    }

    @Override
    public List<RoomDto> filterRooms(List<Long> ids, String name, String description, LocalDate startDate, LocalDate endDate, Boolean hasMinibar, RoomSize roomSize) {
        // Mock implementation to filter rooms based on criteria
        List<RoomDto> filteredRooms = new ArrayList<>();
        // Implement your filtering logic here based on the parameters
        // For simplicity, returning a mock list
        filteredRooms.add(RoomDto.builder().id(1L).name("Filtered Room 1").build());
        return filteredRooms;
    }

    @Override
    public RoomDto convertToDto(Room room) {
        // Mock implementation to convert Room entity to RoomDto
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .hasMinibar(room.getHasMinibar())
                .roomSize(room.getRoomSize())
                .build();
    }

    @Override
    public void housekeeping() {
        // Mock implementation of housekeeping task
        System.out.println("Performing housekeeping for all rooms...");
    }
}
