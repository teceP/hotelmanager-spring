package de.mteklic.hotelmanager.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import de.mteklic.hotelmanager.model.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for mapping specific exceptions to appropriate HTTP responses.
 */
@RestControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    /**
     * Handles ConstraintViolationException and returns a ResponseEntity with a custom error response body.
     *
     * @param ex The ConstraintViolationException that occurred.
     * @return ResponseEntity containing a custom error response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Exception handler for ConstraintViolationException");

        String path = request.getRequestURI();
        String message = "Violation against constraints";
        int statusCode = HttpStatus.BAD_REQUEST.value();
        LocalDateTime localDateTime = LocalDateTime.now();
        List<ApiError.Entry> entries = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            ApiError.Entry entry = ApiError.Entry
                    .builder()
                    .fieldName(violation.getPropertyPath().toString())
                    .invalidValue(violation.getInvalidValue())
                    .message(violation.getMessage())
                    .build();
            entries.add(entry);
        }

        log.error(entries.toString());

        ApiError apiError = ApiError.builder()
                .path(path)
                .message(message)
                .statusCode(statusCode)
                .localDateTime(localDateTime)
                .entries(entries)
                .build();

        return ResponseEntity.badRequest().body(apiError);
    }

    /**
     * Handles HttpMessageNotReadableException and returns a ResponseEntity with a custom error response body.
     *
     * @param ex The HttpMessageNotReadableException that occurred.
     * @return ResponseEntity containing a custom error response.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Exception handler for HttpMessageNotReadableException");

        String path = request.getRequestURI();
        String message = "Malformed JSON request or wrong object type or object value.";
        int statusCode = HttpStatus.BAD_REQUEST.value();
        LocalDateTime localDateTime = LocalDateTime.now();
        List<ApiError.Entry> entries = new ArrayList<>();

        Throwable mostSpecificCause = ex.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .reduce((f1, f2) -> f1 + "." + f2)
                    .orElse("unknown");
            ApiError.Entry entry = ApiError.Entry
                    .builder()
                    .fieldName(fieldName)
                    .invalidValue(ife.getValue())
                    .message(String.format("Incorrect value '%s' for field '%s'. Expected type: '%s'",
                            ife.getValue(), fieldName, ife.getTargetType().getSimpleName()))
                    .build();
            entries.add(entry);
        } else {
            ApiError.Entry entry = ApiError.Entry.builder().message(mostSpecificCause.getMessage()).build();
            entries.add(entry);
        }

        ApiError apiError = ApiError.builder()
                .path(path)
                .message(message)
                .statusCode(statusCode)
                .localDateTime(localDateTime)
                .entries(entries)
                .build();

        return ResponseEntity.badRequest().body(apiError);
    }

    /**
     * Handles ResponseStatusException and returns a ResponseEntity with a custom status error response body.
     *
     * @param ex The ResponseStatusException that occurred.
     * @return ResponseEntity containing a custom status error response.
     */
    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.error("Exception handler for ResponseStatusException");

        ApiError apiError = ApiError
                .builder()
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .statusCode(ex.getStatusCode().value())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiError, ex.getStatusCode());
    }

    /**
     * Handles RoomBookedOutException and returns a ResponseEntity with a custom status error response body.
     *
     * @param ex The RoomBookedOutException that occurred.
     * @return ResponseEntity containing a custom status error response.
     */
    @ExceptionHandler(RoomBookedOutException.class)
    protected ResponseEntity<ApiError> handleRoomBookedOutException(RoomBookedOutException ex, HttpServletRequest request) {
        log.error("Exception handler for RoomBookedOutException");
        log.error(ex.getMessage());

        ApiError apiError = ApiError
                .builder()
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Handles EndDateBeforeStartDateException and returns a ResponseEntity with a custom status error response body.
     *
     * @param ex The EndDateBeforeStartDateException that occurred.
     * @return ResponseEntity containing a custom status error response.
     */
    @ExceptionHandler(EndDateBeforeStartDateException.class)
    protected ResponseEntity<ApiError> handleEndDateBeforeStartDateException(EndDateBeforeStartDateException ex, HttpServletRequest request) {
        log.error("Exception handler for EndDateBeforeStartDateException");
        log.error(ex.getMessage());

        ApiError apiError = ApiError
                .builder()
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles StartAndOrEndDateBeforeNowException and returns a ResponseEntity with a custom status error response body.
     *
     * @param ex The StartAndOrEndDateBeforeNowException that occurred.
     * @return ResponseEntity containing a custom status error response.
     */
    @ExceptionHandler(StartAndOrEndDateBeforeNowException.class)
    protected ResponseEntity<ApiError> handleStartAndOrEndDateBeforeNowException(StartAndOrEndDateBeforeNowException ex, HttpServletRequest request) {
        log.error("Exception handler for StartAndOrEndDateBeforeNowException");
        log.error(ex.getMessage());

        ApiError apiError = ApiError
                .builder()
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles StartAndOrEndDateBeforeNowException and returns a ResponseEntity with a custom status error response body.
     *
     * @param ex The StartAndOrEndDateBeforeNowException that occurred.
     * @return ResponseEntity containing a custom status error response.
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<ApiError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        log.error("Exception handler for StartAndOrEndDateBeforeNowException");
        log.error(ex.getMessage());

        ApiError apiError = ApiError
                .builder()
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
