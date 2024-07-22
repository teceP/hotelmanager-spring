package de.mteklic.hotelmanager.service.impl;

import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import de.mteklic.hotelmanager.model.dto.BookingDto;
import de.mteklic.hotelmanager.model.dto.RoomDto;
import de.mteklic.hotelmanager.repository.RoomRepository;
import de.mteklic.hotelmanager.service.BookingService;
import de.mteklic.hotelmanager.service.RoomService;
import de.mteklic.hotelmanager.specification.RoomSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing rooms in a hotel management system.
 */
@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    private final HousekeepingService housekeepingService;

    private final BookingService bookingService;

    public RoomServiceImpl(RoomRepository roomRepository, HousekeepingService housekeepingService, BookingService bookingService){
        this.roomRepository = roomRepository;
        this.housekeepingService = housekeepingService;
        this.bookingService = bookingService;
    }

    @Override
    public RoomDto getRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));

        return convertToDto(room);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return this.roomRepository
                .findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        // Validation of the object is done automatically by Springs @Valid annotations and our custom rules in the Room Model class.
        Room room = Room.builder()
                .name(roomDto.name())
                .description(roomDto.description())
                .hasMinibar(roomDto.hasMinibar())
                .roomSize(roomDto.roomSize())
                .bookings(new ArrayList<>())
                .build();
        return convertToDto(this.roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id)   {
        this.roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));
        this.roomRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoomDto updateRoom(RoomDto roomDto) {
        log.debug("Edit Room with id {} in complete mode", roomDto.id());
        Room updateableRoom = this.roomRepository.findById(roomDto.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found."));

        log.debug("Updateable Room before: {}", updateableRoom);

        // Members are annotated with @NonNull, no null check needed before we call the getters.
        // Update room details based on RoomDto:
        if (roomDto.name() != null){
            updateableRoom.setName(roomDto.name());
        }

        if (roomDto.description() != null){
            updateableRoom.setDescription(roomDto.description());
        }

        if (roomDto.roomSize() != null){
            updateableRoom.setRoomSize(roomDto.roomSize());
        }

        if (roomDto.hasMinibar() != null){
            updateableRoom.setHasMinibar(roomDto.hasMinibar());
        }

        log.debug("Updated Room: {}", updateableRoom);
        // We are in a @Transactional method - we do not need to call save()-methode.
        return convertToDto(updateableRoom);
    }

    @Override
    public List<RoomDto> getFilteredRooms(List<Long> ids, String name, String description, LocalDate startDate, LocalDate endDate, Boolean hasMinibar, RoomSize roomSize) {
        Specification<Room> specification = Specification.where(null);

        // Build specifications based on provided criteria
        specification = applyIdFilter(specification, ids);
        specification = applyNameFilter(specification, name);
        specification = applyDescriptionFilter(specification, description);
        specification = applyHasMinibarFilter(specification, hasMinibar);
        specification = applyRoomSizeFilter(specification, roomSize);

        // Filter rooms based on specifications
        List<Room> filteredRooms = this.roomRepository.findAll(specification);

        log.debug("Filtered size after database findAll(specs): {}", filteredRooms.size());

        // Returned list from repository might be immutable. Avoid errors and work with list from applyDateRangeFilter method
        List<Room> mutableFilteredRooms = applyDateRangeFilter(filteredRooms, startDate, endDate);

        log.debug("FilteredRooms size: {}", mutableFilteredRooms.size());

        // Convert filtered rooms to RoomDto
        return mutableFilteredRooms
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public Specification<Room> applyIdFilter(Specification<Room> specification, List<Long> ids){
        if (ids != null) {
            log.debug("Add id's spec: {}", ids);
            specification = specification.and(RoomSpecifications.hasId(ids));
        }
        return specification;
    }

    public Specification<Room> applyNameFilter(Specification<Room> specification, String name){
        if (name != null && !name.isBlank()) {
            name = "%" + name + "%";
            log.debug("Add name spec: {}", name);
            specification = specification.and(RoomSpecifications.hasName(name));
        }
        return specification;
    }

    public Specification<Room> applyDescriptionFilter(Specification<Room> specification, String description){
        if (description != null && !description.isBlank()) {
            description = "%" + description + "%";
            log.debug("Add description spec: {}", description);
            specification = specification.and(RoomSpecifications.hasDescription(description));
        }
        return specification;
    }

    public Specification<Room> applyHasMinibarFilter(Specification<Room> specification, Boolean hasMinibar){
        if (hasMinibar != null) {
            log.debug("Add hasMinibar spec: {}", hasMinibar);
            specification = specification.and(RoomSpecifications.hasMinibar(hasMinibar));
        }
        return specification;
    }

    public Specification<Room> applyRoomSizeFilter(Specification<Room> specification, RoomSize roomSize){
        if (roomSize != null) {
            log.debug("Add roomSize spec: {}", roomSize);
            specification = specification.and(RoomSpecifications.hasRoomSize(roomSize));
        }
        return specification;
    }

    public List<Room> applyDateRangeFilter(List<Room> rooms, LocalDate startDate, LocalDate endDate){
        // If start date and end date are provided, filter based on availability
        if (startDate != null && endDate != null) {
            log.debug("Add startDate: {} & endDate spec: {}", startDate, endDate);

            // Create mutable list
            List<Room> roomsMutable = new ArrayList<>(rooms);

            //Get unavailable rooms within the specific date range
            List<Long> availableRooms = this.bookingService.getUnavailableBookings(rooms.stream().map(Room::getId).toList(), startDate, endDate)
                    .stream()
                    .map(Booking::getRoom)
                    .map(Room::getId)
                    .toList();

            // Filter rooms that are not available in the specific date range
            roomsMutable.removeIf(room -> availableRooms.contains(room.getId()));

            return roomsMutable;
        }

        log.debug("Start and or end date was null, returning provided list.");
        return rooms;
    }

    @Override
    public RoomDto convertToDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .hasMinibar(room.getHasMinibar())
                .roomSize(room.getRoomSize())
                .bookings(room.getBookings() != null ? room.getBookings()
                        .stream()
                        .map(b -> BookingDto.builder()
                                .id(b.getId())
                                .startDate(b.getStartDate())
                                .endDate(b.getEndDate())
                                .build())
                        .toList() : new ArrayList<>())
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 10 * * *")
    public void housekeeping() {
        log.debug("A housekeeping cron, which exists to demonstrate a crontask and keeps all rooms tidied up. :)");
        log.debug("Main Threads name: {}", Thread.currentThread().getName());
        this.roomRepository.findAll().forEach(housekeepingService::tidyUpRoom);
    }
}
