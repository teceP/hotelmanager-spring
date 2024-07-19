package de.mteklic.hotelmanager.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.mteklic.hotelmanager.model.RoomSize;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@JsonSerialize
public record RoomDto(Long id, String name, String description, Boolean hasMinibar, RoomSize roomSize, List<BookingDto> bookings)  {}
