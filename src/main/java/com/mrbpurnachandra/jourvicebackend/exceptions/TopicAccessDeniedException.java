package com.mrbpurnachandra.jourvicebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TopicAccessDeniedException extends CustomException {
    public TopicAccessDeniedException() {
        super("Topic access denied");
    }
}
