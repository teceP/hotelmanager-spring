package de.mteklic.hotelmanager.specification;

import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Utility class for creating specifications to query {@link Room} entities.
 */
public class RoomSpecifications {

    /**
     * Creates a specification for finding rooms by a list of room IDs.
     *
     * @param ids The list of room IDs.
     * @return A specification to filter rooms by room IDs.
     */
    public static Specification<Room> hasId(List<Long> ids) {
        return (root, query, criteriaBuilder) -> root.get("id").in(ids);
    }

    /**
     * Creates a specification for finding rooms by exact name.
     *
     * @param name The exact name of the room.
     * @return A specification to filter rooms by exact name.
     */
    public static Specification<Room> hasName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), name);
    }

    /**
     * Creates a specification for finding rooms by a name pattern.
     *
     * @param nameLike The name pattern of the room.
     * @return A specification to filter rooms by name pattern.
     */
    public static Specification<Room> hasNameLike(String nameLike) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), nameLike);
    }

    /**
     * Creates a specification for finding rooms by exact description.
     *
     * @param description The exact description of the room.
     * @return A specification to filter rooms by exact description.
     */
    public static Specification<Room> hasDescription(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), description);
    }

    /**
     * Creates a specification for finding rooms by a description pattern.
     *
     * @param descriptionLike The description pattern of the room.
     * @return A specification to filter rooms by description pattern.
     */
    public static Specification<Room> hasDescriptionLike(String descriptionLike) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), descriptionLike);
    }

    /**
     * Creates a specification for finding rooms by minibar availability.
     *
     * @param hasMinibar A boolean indicating if the room has a minibar.
     * @return A specification to filter rooms by minibar availability.
     */
    public static Specification<Room> hasMinibar(boolean hasMinibar) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hasMinibar"), hasMinibar);
    }

    /**
     * Creates a specification for finding rooms by room size.
     *
     * @param roomSize A boolean indicating if the room has a specific roomSize.
     * @return A specification to filter rooms by room size.
     */
    public static Specification<Room> hasRoomSize(RoomSize roomSize) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roomSize"), roomSize);
    }
}
