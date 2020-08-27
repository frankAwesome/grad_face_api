package com.face.recognition.controller.handler;

import com.face.recognition.exceptions.ValidationException;
import com.face.recognition.models.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@ControllerAdvice
@RestController
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<ApiErrorResponse> validiationException(final ValidationException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiErrorResponse.builder()
                .message(e.getMessage())
                .status(403)
                .error("Forbidden")
                .timestamp(new Date())
                .build(), HttpStatus.FORBIDDEN);
    }
}
