package de.mteklic.hotelmanager.service.impl;

import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class for managing housekeeping tasks in a hotel management system.
 */
@Service
public class HousekeepingService {

    private static final Logger log = LoggerFactory.getLogger(HousekeepingService.class);

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Performs housekeeping tasks asynchronously for a room.
     * ---
     * This demonstrates the usage of a async task.
     * It runs in different threads and since it does not run in the main thread, it doesnt block anything.
     * We have always one housekeeper available (CorePoolSize) but we can scale up to a maximum 3 housekeepers (MaxPoolSize).
     *
     * Info: Neither the main thread nor any else will get blocked by the sleep call, since we are using different threads
     * and can be sure, that our REST API is not effected by this.
     *
     * @param room Room object representing the room to tidy up.
     */
    @Async
    public void tidyUpRoom(Room room) {
        log.debug("{} is today cleaning the room with ID: {}, Name: {}", Thread.currentThread().getName(), room.getName(), room.getId());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", ie);
        }

        log.debug("{} has finished cleaning the room with ID: {}, Name: {}", Thread.currentThread().getName(), room.getName(), room.getId());
    }
}
