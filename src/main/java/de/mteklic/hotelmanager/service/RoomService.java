package de.mteklic.hotelmanager.service;

import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    /**
     * Returns a single RoomDto by its Id.
     * @param id
     * @return RoomDto colliding with the id.
     */
    RoomDto getRoom(Long id);

    /**
     * Retrieves all rooms.
     *
     * @return List of RoomDto representing all rooms.
     */
    List<RoomDto> getAllRooms();

    /**
     * Adds a new room to the system.
     *
     * @param roomDto RoomDto representing the room to be added.
     * @return RoomDto representing the added room.
     */
    RoomDto createRoom(RoomDto roomDto);
    /**
     * Deletes a room by its ID.
     *
     * @param id ID of the room to delete.
     * @throws ResponseStatusException If the room with the specified ID is not found.
     */
    void deleteRoom(Long id);

    /**
     * Edits a room based on the provided RoomDto.
     *
     * @param roomDto RoomDto containing the updated room details.
     * @return RoomDto representing the updated room.
     * @throws ResponseStatusException If the room with the specified ID is not found.
     */
    RoomDto updateRoom(RoomDto roomDto);

    /**
     * Filters rooms based on various criteria.
     *
     * @param ids             List of room IDs to filter.
     * @param name            Exact name to filter.
     * @param description     Exact description to filter.
     * @param startDate       Start date of availability to filter.
     * @param endDate         End date of availability to filter.
     * @param hasMinibar      Whether rooms should have minibar.
     * @return List of RoomDto representing filtered rooms.
     */
    List<RoomDto> getFilteredRooms(List<Long> ids, String name, String description, LocalDate startDate, LocalDate endDate, Boolean hasMinibar, RoomSize roomSize);
    /**
     * Converts a Room entity to RoomDto.
     *
     * @param room Room entity to convert.
     * @return RoomDto representing the converted room.
     */
    RoomDto convertToDto(Room room);

    /**
     * Executes housekeeping tasks for all rooms.
     * This method is scheduled to run every day at 10:00 AM.
     * --
     * This demonstrates the usage of a scheduled task.
     */
    void housekeeping();
}
