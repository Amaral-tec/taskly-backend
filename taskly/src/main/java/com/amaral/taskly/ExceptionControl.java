package com.amaral.taskly;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.amaral.taskly.shared.dto.ErrorObjectResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionControl extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObjectResponseDTO> handleExceptionCustom(BusinessException ex, WebRequest request) {
        ErrorObjectResponseDTO errorObject = new ErrorObjectResponseDTO(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.toString(),
            request.getDescription(false).replace("uri=", ""),
            LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(errorObject);
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String msg;
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            StringBuilder sb = new StringBuilder();
            List<ObjectError> list = validationEx.getBindingResult().getAllErrors();
            list.forEach(error -> sb.append(error.getDefaultMessage()).append("\n"));
            msg = sb.toString().trim();
        } else if (ex instanceof HttpMessageNotReadableException) {
            msg = "No data was sent in the request body";
        } else {
            msg = ex.getMessage();
        }

        ErrorObjectResponseDTO errorObject = new ErrorObjectResponseDTO(
            msg,
            status.value() + " - " + status.getReasonPhrase(),
            request.getDescription(false).replace("uri=", ""),
            LocalDateTime.now()
        );

        log.error("Unhandled error caught", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, SQLException.class })
    protected ResponseEntity<ErrorObjectResponseDTO> handleDatabaseException(Exception ex, WebRequest request) {
        String msg;
        if (ex instanceof DataIntegrityViolationException dataEx) {
            msg = "Database integrity error: " + dataEx.getMostSpecificCause().getMessage();
        } else if (ex instanceof ConstraintViolationException consEx) {
            msg = "Foreign key error: " + consEx.getSQLException().getMessage();
        } else if (ex instanceof SQLException sqlEx) {
            msg = "SQL Error: " + sqlEx.getMessage();
        } else {
            msg = ex.getMessage();
        }

        ErrorObjectResponseDTO errorObject = new ErrorObjectResponseDTO(
            msg,
            HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            request.getDescription(false).replace("uri=", ""),
            LocalDateTime.now()
        );

        log.error("Database error caught", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }
}
