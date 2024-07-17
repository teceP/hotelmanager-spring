package de.mteklic.hotelmanager.repository;

import de.mteklic.hotelmanager.model.Booking;
import de.mteklic.hotelmanager.model.Room;
import de.mteklic.hotelmanager.model.RoomSize;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class BookingRepositoryTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    Room room;

    @BeforeEach
    public void init() {
        room = Room.builder().name("Berlin").description("Beautiful Room in Berlin style.").hasMinibar(true).roomSize(RoomSize.SUITE).build();
        roomRepository.save(room);
        bookingRepository.deleteAll();
    }

    @Test
    void testcontainersDatabaseUpAndRunning(){
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(bookingRepository).isNotNull();
        assertThat(roomRepository).isNotNull();
        assertThat(room).isNotNull();
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_Past_Booking_Start_Date() {
        Booking booking = Booking.builder()
                .room(room)
                .startDate(LocalDate.now().minusDays(2)) // past
                .endDate(LocalDate.now().plusDays(7)) // future
                .build();
        assertThrows(ConstraintViolationException.class, () -> bookingRepository.save(booking));
    }

    @Test
    public void Should_Violate_Constraint_When_Adding_Past_Booking_End_Date() {
        Booking booking = Booking.builder()
                .room(room)
                .startDate(LocalDate.now().plusDays(2)) // future
                .endDate(LocalDate.now().minusDays(7)) // past
                .build();
        assertThrows(ConstraintViolationException.class, () -> bookingRepository.save(booking));
    }


    @Test
    public void Should_Save_When_Adding_Correct_Booking_Dates() {
        Booking booking = Booking.builder()
                .room(room)
                .startDate(LocalDate.now().plusDays(0)) // today
                .endDate(LocalDate.now().plusDays(7)) // future
                .build();
        Assertions.assertEquals(1L, bookingRepository.save(booking).getId().longValue());
    }

    @Test
    public void Should_Save_When_Adding_Start_And_End_Same_Date() {
        Booking booking = Booking.builder()
                .room(room)
                .startDate(LocalDate.now().plusDays(0)) // same
                .endDate(LocalDate.now().plusDays(0)) // same
                .build();
        Assertions.assertTrue(bookingRepository.save(booking).getId() > 0L);
    }

    @Test
    public void Should_Return_Equal_When_Adding_And_Get_By_Id() {
        Booking booking = Booking.builder()
                .room(room)
                .startDate(LocalDate.now().plusDays(0)) // same
                .endDate(LocalDate.now().plusDays(0)) // same
                .build();
        final Long id = bookingRepository.save(booking).getId();
        Optional<Booking> optBooking = bookingRepository.findById(id);
        assertNotNull(optBooking.get());
        Assertions.assertEquals(booking, optBooking.get());
    }

    @Test
    public void Should_Return_List_instead_Iterable() {
        var obj = bookingRepository.findAll();
        Assertions.assertInstanceOf(List.class, obj);
    }
}