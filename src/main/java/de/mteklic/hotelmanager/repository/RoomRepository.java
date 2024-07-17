package de.mteklic.hotelmanager.repository;

import de.mteklic.hotelmanager.model.Room;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Room} entities.
 */
public interface RoomRepository extends CrudRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    /**
     * Retrieves all rooms from the database.
     * This method redefines findAll to return a List instead of an Iterable.
     *
     * @return a list of all rooms
     */
    @Override
    List<Room> findAll();
}
