package com.mrbpurnachandra.jourvicebackend.exceptions;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

public class AccountNotFoundException extends CustomException {
    public AccountNotFoundException() {
        super("Account not found.");
    }
}
