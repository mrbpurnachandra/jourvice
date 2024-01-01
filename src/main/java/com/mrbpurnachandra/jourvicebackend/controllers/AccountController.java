package com.mrbpurnachandra.jourvicebackend.controllers;

import com.mrbpurnachandra.jourvicebackend.models.Account;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.services.AccountService;
import com.mrbpurnachandra.jourvicebackend.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@Valid @RequestBody Account account, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        return accountService.createAccount(account, user);
    }
}
