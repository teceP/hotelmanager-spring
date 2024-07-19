package de.mteklic.hotelmanager.repository;

import de.mteklic.hotelmanager.model.Room;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Room} entities.
 */
public interface RoomRepository extends ListCrudRepository<Room, Long>, JpaSpecificationExecutor<Room> { }
