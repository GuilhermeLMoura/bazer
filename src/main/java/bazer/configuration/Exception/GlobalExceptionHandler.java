package bazer.configuration.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex){

        Map<String,Object> error = new HashMap<>();
        error.put("status",500);
        error.put("error","Internal Server Error");
        error.put("message","Erro inesperado no servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}