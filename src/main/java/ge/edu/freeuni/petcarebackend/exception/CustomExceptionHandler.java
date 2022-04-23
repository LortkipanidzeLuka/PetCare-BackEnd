package ge.edu.freeuni.petcarebackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<Object> handleBusiness(RuntimeException ex, WebRequest request) {
        String body = null;
        if (ex.getMessage() != null) {
            body = new ObjectMapper().createObjectNode().put("errorCode", ex.getMessage()).toString();
        }

        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex, WebRequest request) {
        String body = null;
        if (ex.getMessage() != null) {
            body = new ObjectMapper().createObjectNode().put("internal_error", ex.getMessage()).toString();
        }
        ex.printStackTrace();
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
