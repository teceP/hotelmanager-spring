package de.mteklic.hotelmanager.controller.impl;

import de.mteklic.hotelmanager.controller.RoomController;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.service.impl.RoomServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing hotel rooms.
 */
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomControllerImpl implements RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomControllerImpl.class);

    private final RoomServiceImpl roomServiceImpl;

    public RoomControllerImpl(RoomServiceImpl roomServiceImpl){
        this.roomServiceImpl = roomServiceImpl;
    }

    @Override
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(this.roomServiceImpl.getAllRooms());
    }

    @Override
    public ResponseEntity<RoomDto> getHotelRoom(@PathVariable("id") Long id) throws ResponseStatusException {
        return ResponseEntity.ok(this.roomServiceImpl.getRoom(id));
    }

    @Override
    public ResponseEntity<List<RoomDto>> filter(@RequestParam(required = false) List<Long> ids,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String description,
                                                @RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate,
                                                @RequestParam(required = false) Boolean hasMinibar,
                                                @RequestParam(required = false) RoomSize roomSize) {
        return ResponseEntity.ok(this.roomServiceImpl.filterRooms(ids, name, description, startDate, endDate, hasMinibar, roomSize));
    }

    @Override
    public ResponseEntity<RoomDto> addRoom(@RequestBody @Valid RoomDto roomDto) {
        return ResponseEntity.ok(this.roomServiceImpl.addRoom(roomDto));
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) throws ResponseStatusException {
        log.debug("deleteById {}", id);
        this.roomServiceImpl.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RoomDto> editHotelRoom(@RequestBody @Valid RoomDto roomDto) throws ResponseStatusException {
        return ResponseEntity.ok(this.roomServiceImpl.editRoom(roomDto));
    }
}
