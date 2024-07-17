package de.mteklic.hotelmanager.repository;

import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Those Repository Tests will ensure that all Validation Annotations are kept in mind and has not changed by accident or something.
 * Also, they are good to test against "self-made" native queries and queries built by conventional naming.
 *
 * Test against maximum-as-possible production near environment: use PostgreSQL Testcontainer instead of H2 for testing
 */
@DataJpaTest
@Testcontainers
@TestPropertySource(locations = {"classpath:application-test.properties"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomRepositoryTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    public void init() {
        roomRepository.deleteAll();
    }

    @Test
    void testcontainersDatabaseUpAndRunning(){
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(roomRepository).isNotNull();
    }

     @Test
    public void Should_Violate_Constraint_When_Adding_Without_Anything(){
        Room room = Room.builder().build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
     }

    @Test
    public void Should_Violate_Constraint_When_Adding_With_Blank_Name(){
        Room room = Room.builder().name("").description("abc").roomSize(RoomSize.SUITE).hasMinibar(true).build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_With_Too_Short_Name(){
        Room room = Room.builder().name("ab").description("abc").roomSize(RoomSize.SUITE).hasMinibar(true).build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_With_Too_Long_Name(){
        Room room = Room.builder().name("AAAAAAAAAAAAAAAAAAAAAAABbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb").description("abc").roomSize(RoomSize.SUITE).hasMinibar(true).build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_Without_RoomSize(){
        Room room = Room.builder().name("abcde").description("abc").roomSize(null).hasMinibar(true).build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_Without_HasMinibar(){
        Room room = Room.builder().name("abcde").description("abc").roomSize(RoomSize.SUITE).hasMinibar(null).build();
        assertThrows(ConstraintViolationException.class, () -> roomRepository.save(room));
    }

    @Test
    public void Should_NOT_Violate_Constraint_When_Adding_Without_Description(){
        final String NAME = "abcde";
        Room room = Room.builder().name(NAME).description("").roomSize(RoomSize.SUITE).hasMinibar(true).build();
        assertTrue(roomRepository.save(room).getName().equals(NAME) && room.getId() != null && room.getId() > 0);
    }
}