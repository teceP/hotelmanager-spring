package de.mteklic.hotelmanager.repository;

import de.mteklic.hotelmanager.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Booking} entities.
 */
public interface BookingRepository extends ListCrudRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    /**
     * Retrieves all bookings associated with the specified room ID.
     *
     * @param roomId the ID of the room
     * @return a list of bookings associated with the room
     */
    List<Booking> findAllByRoomId(Long roomId);

    /**
     * Retrieves all bookings associated with the specified list of room IDs using a native query.
     * ---
     * Just to demonstrate this kind of native query...
     *
     * @param roomIds the list of room IDs
     * @return a list of bookings associated with the specified room IDs
     */
    @Query(value = "SELECT * FROM booking WHERE room_id IN (:roomIds)", nativeQuery = true)
    List<Booking> findAllByRoomIds(List<Long> roomIds);
}
