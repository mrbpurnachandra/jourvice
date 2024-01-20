package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.AccountConflictException;
import com.mrbpurnachandra.jourvicebackend.exceptions.AccountNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Account;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account, User user) {
        Optional<Account> a = accountRepository.findAccountByIssAndSub(user.getIss(), user.getSub());

        if (a.isPresent()) {
            Account existingAccount = a.get();

            if (existingAccount.isActive()) throw new AccountConflictException();

            existingAccount.setActive(true);

            return accountRepository.save(existingAccount);
        }

        account.setIss(user.getIss());
        account.setSub(user.getSub());

        return accountRepository.save(account);
    }

    public Account getAccountByUser(User user) {
        Optional<Account> account = accountRepository.findAccountByIssAndSub(user.getIss(), user.getSub());

        return account.orElseThrow(AccountNotFoundException::new);
    }
}