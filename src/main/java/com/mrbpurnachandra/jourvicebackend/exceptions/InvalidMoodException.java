package com.mrbpurnachandra.jourvicebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMoodException extends CustomException {
    public InvalidMoodException() {
        super("Invalid mood");
    }
}
