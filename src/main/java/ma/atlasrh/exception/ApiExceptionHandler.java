package ma.atlasrh.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<?> notFound(RuntimeException e) {
        return response(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    ResponseEntity<?> conflict(Exception e) {
        return response(HttpStatus.CONFLICT, e instanceof ConflictException ? e.getMessage() : "Contrainte d'unicité ou relation invalide");
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ResponseEntity<?> badRequest(Exception e) {
        String m = e instanceof MethodArgumentNotValidException v ? v.getBindingResult().getFieldErrors().stream().map(x -> x.getField() + ": " + x.getDefaultMessage()).findFirst().orElse("Requête invalide") : e.getMessage();
        return response(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseEntity<?> response(HttpStatus s, String m) {
        return ResponseEntity.status(s).body(Map.of("timestamp", Instant.now().toString(), "status", s.value(), "error", s.getReasonPhrase(), "message", m));
    }
}
