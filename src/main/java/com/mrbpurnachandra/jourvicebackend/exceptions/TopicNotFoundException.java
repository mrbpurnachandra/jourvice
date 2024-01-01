package com.mrbpurnachandra.jourvicebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TopicNotFoundException extends CustomException {
    public TopicNotFoundException() {
        super("Topic not found");
    }
}
