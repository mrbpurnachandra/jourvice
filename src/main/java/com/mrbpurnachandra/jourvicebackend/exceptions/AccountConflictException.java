package com.mrbpurnachandra.jourvicebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class AccountConflictException extends CustomException {
    public AccountConflictException() {
        super("Account already exists");
    }
}
