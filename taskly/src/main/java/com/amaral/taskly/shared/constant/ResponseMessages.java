package com.amaral.taskly.shared.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseMessages {

    private ResponseMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String GET_SUCCESS = "GET request successful";
    public static final String POST_SUCCESS = "POST request successful";
    public static final String DELETE_SUCCESS = "DELETE request successful";

    public static final String NOT_FOUND = "Resource not found";
    public static final String BAD_REQUEST = "Bad request";
    public static final String SERVER_ERROR = "Internal server error";

    public static final String NOT_FOUND_ID = "Operation not performed: Not included in the ID database";
    public static final String NOT_FOUND_NAME = "Operation not carried out: Already registered with the name";
    public static final String DELETE_OK = "OK: Deletion completed successfully.";

    public static ResponseEntity<String> ok(String message) {
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public static ResponseEntity<String> created(String message) {
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    public static ResponseEntity<String> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<String> notFound(String message) {
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<String> badRequest(String message) {
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<String> serverError(String message) {
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
