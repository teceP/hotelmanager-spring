package de.mteklic.hotelmanager.specification;

import de.mteklic.hotelmanager.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Utility class for creating specifications to query {@link Booking} entities.
 */
public class BookingSpecifications {

    /**
     * Creates a specification for finding bookings by room ID.
     *
     * @param roomId The ID of the room.
     * @return A specification to filter bookings by room ID.
     */
    public static Specification<Booking> hasRoomId(Long roomId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("room_id"), roomId);
    }

    /**
     * Creates a specification for finding bookings by a list of room IDs.
     *
     * @param roomIds The list of room IDs.
     * @return A specification to filter bookings by room IDs.
     */
    public static Specification<Booking> hasRoomIds(List<Long> roomIds) {
        return (root, query, criteriaBuilder) -> root.get("room_id").in(roomIds);
    }

    /**
     * Creates a specification for finding bookings that overlap with a given date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A specification to filter bookings that overlap with the given date range.
     */
    public static Specification<Booking> hasOverlap(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), criteriaBuilder.literal(endDate)),
                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), criteriaBuilder.literal(startDate)));
    }

    /**
     * Creates a specification for finding bookings that not overlap with a given date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A specification to filter bookings that overlap with the given date range.
     */
    public static Specification<Booking> hasNoOverlap(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), criteriaBuilder.literal(endDate)),
                criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), criteriaBuilder.literal(startDate)));
    }

}
