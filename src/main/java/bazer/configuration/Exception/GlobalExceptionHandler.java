package bazer.configuration.Exception;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicate(DataIntegrityViolationException ex){

        Map<String,Object> error = new HashMap<>();
        error.put("status",409);
        error.put("error","Conflict");
        error.put("message","Usuário já existe");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAcessDenied(AuthorizationDeniedException ex){
        Map<String,Object> error = new HashMap<>();
        error.put("status", 403);
        error.put("error", "Access Denied");
        error.put("message", "Seu perfil não tem acesso a esta ação");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String,Object>> handleBusinessRule(BusinessRuleException ex){
        Map<String,Object> error = new HashMap<>();
        error.put("status", 422);
        error.put("error", "Regra de negócio violada");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.valueOf(422)).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(EntityNotFoundException ex){
        Map<String,Object> error = new HashMap<>();
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String,Object>> handleMultipart(MultipartException ex){
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        LOGGER.error("MultipartException: {} | Cause: {}", ex.getMessage(), cause.getMessage());
        Map<String,Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("cause", cause.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> handleResponseStatus(ResponseStatusException ex){
        Map<String,Object> error = new HashMap<>();
        error.put("status", ex.getStatusCode().value());
        error.put("error", ex.getStatusCode().toString());
        error.put("message", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex){
        LOGGER.error("Unhandled exception: {}", ex.getMessage(), ex);
        Map<String,Object> error = new HashMap<>();
        error.put("status",500);
        error.put("error","Internal Server Error");
        error.put("message","Erro inesperado no servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDeniedException(AccessDeniedException ex){

        Map<String,Object> error = new HashMap<>();
        error.put("status",403);
        error.put("error","Forbidden");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    

}