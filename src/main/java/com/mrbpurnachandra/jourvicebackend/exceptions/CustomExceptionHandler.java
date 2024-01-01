package com.mrbpurnachandra.jourvicebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handle(CustomException exception) {
        Map<String, Object> response = new HashMap<>();

        response.put("message", exception.getMessage());

        HttpStatus statusCode = HttpStatus.OK;

        if (exception.getClass().isAnnotationPresent(ResponseStatus.class)) {
            statusCode = exception.getClass().getAnnotation(ResponseStatus.class).value();

            response.put("status", statusCode.value());
        }

        return new ResponseEntity<>(response, statusCode);
    }
}
