package br.com.adoption.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("fields", fields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(AnimalNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleAnimalNotAvailable(AnimalNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(DuplicateAdoptionRequestException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateAdoptionRequest(DuplicateAdoptionRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(OwnerCannotAdoptOwnAnimalException.class)
    public ResponseEntity<Map<String, Object>> handleOwnerCannotAdoptOwnAnimal(OwnerCannotAdoptOwnAnimalException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(OnlyOwnerCanManageAdoptionRequestException.class)
    public ResponseEntity<Map<String, Object>> handleOnlyOwnerCanManageAdoptionRequest(
            OnlyOwnerCanManageAdoptionRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(OnlyOwnerCanManageAnimalException.class)
    public ResponseEntity<Map<String, Object>> handleOnlyOwnerCanManageAnimal(
            OnlyOwnerCanManageAnimalException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(OnlyOwnerCanManageUserException.class)
    public ResponseEntity<Map<String, Object>> handleOnlyOwnerCanManageUser(
            OnlyOwnerCanManageUserException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(AdoptionRequestNotPendingException.class)
    public ResponseEntity<Map<String, Object>> handleAdoptionRequestNotPending(
            AdoptionRequestNotPendingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 401,
                        "error", "Unauthorized",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(DuplicateUserEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateUserEmail(DuplicateUserEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotVerified(EmailNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(InvalidFileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFileUpload(InvalidFileUploadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(StorageUploadException.class)
    public ResponseEntity<Map<String, Object>> handleStorageUpload(StorageUploadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 502,
                        "error", "Bad Gateway",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDelivery(EmailDeliveryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 502,
                        "error", "Bad Gateway",
                        "message", ex.getMessage()
                )
        );
    }
}
