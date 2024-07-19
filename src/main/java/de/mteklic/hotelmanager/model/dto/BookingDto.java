package de.mteklic.hotelmanager.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@JsonSerialize
public record BookingDto (Long id,
                          LocalDate startDate,
                          LocalDate endDate) {
}
