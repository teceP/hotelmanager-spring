package de.mteklic.hotelmanager.controller;

import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller interface for managing bookings in a hotel management system.
 * Interface makes multiple api-Version of controllers possible. Also, documentation in an interface looks way prettier.
 */
public interface RoomController {

    /**
     * Endpoint to retrieve all hotel rooms.
     *
     * @return ResponseEntity containing a list of all RoomDto objects.
     */
    @GetMapping
    ResponseEntity<List<RoomDto>> getAllRooms();

    /**
     * Endpoint to retrieve a specific hotel room by its ID.
     *
     * @param id ID of the room to retrieve.
     * @return ResponseEntity containing the RoomDto for the specified ID.
     */
    @GetMapping("/{id}")
    ResponseEntity<RoomDto> getHotelRoom(@PathVariable("id") Long id) throws ResponseStatusException;

    /**
     * Endpoint to filter hotel rooms based on various criteria.
     *
     * @param ids             List of IDs to filter by.
     * @param name            Exact name to filter by.
     * @param description     Exact description to filter by.
     * @param startDate       Start date for available rooms.
     * @param endDate         End date for available rooms.
     * @param hasMinibar      Whether rooms should have a minibar or not.
     * @return ResponseEntity containing a list of RoomDto objects that match the filter criteria.
     */
    @GetMapping("/filter")
    ResponseEntity<List<RoomDto>> filter(@RequestParam(required = false) List<Long> ids,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String description,
                                                @RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate,
                                                @RequestParam(required = false) Boolean hasMinibar,
                                                @RequestParam(required = false) RoomSize roomSize);

    /**
     * Endpoint to add a new hotel room.
     *
     * @param roomDto RoomDto object representing the room to add.
     * @return ResponseEntity containing the added RoomDto with generated ID.
     */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RoomDto> createRoom(@RequestBody @Valid RoomDto roomDto);
    /**
     * Endpoint to delete a hotel room by its ID.
     *
     * @param id ID of the room to delete.
     * @return ResponseEntity indicating success (status code 204) or failure (status code 404 if room not found).
     */

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable("id") Long id) throws ResponseStatusException;

    /**
     * Endpoint to edit a hotel room with full updates.
     *
     * @param roomDto RoomDto object representing the updated room details.
     * @return ResponseEntity containing the updated RoomDto.
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RoomDto> editHotelRoom(@RequestBody @Valid RoomDto roomDto) throws ResponseStatusException;
}
