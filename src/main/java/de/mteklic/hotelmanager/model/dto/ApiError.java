package de.mteklic.hotelmanager.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ApiError(String path, String message, int statusCode, LocalDateTime localDateTime, List<Entry> entries) {
    @Builder
    public record Entry (String fieldName, String message, Object invalidValue) {
    }
}
