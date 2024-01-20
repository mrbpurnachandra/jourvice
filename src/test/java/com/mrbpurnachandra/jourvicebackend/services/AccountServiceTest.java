package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.AccountConflictException;
import com.mrbpurnachandra.jourvicebackend.exceptions.AccountNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Account;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.AccountRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@SpringBootTest
class AccountServiceTest {
    static final String ACCOUNT_NAME = "Prakash Bhattarai";
    static final String ISS = "https://accounts.google.com";
    static final String SUB = "123456789";

    @Autowired
    AccountService accountService;

    @MockBean
    AccountRepository accountRepository;

    @Nested
    class CreateAccountTest {
        @Test
        void createAccountShouldThrowAccountConflictExceptionWhenAccountExistsAndActive() {
            assertThrows(AccountConflictException.class, () -> {
                User user = User.builder().iss(ISS).sub(SUB).build();
                Account account = Account.builder().name(ACCOUNT_NAME).build();

                Account existingActiveAccountForUser = Account.builder().name(ACCOUNT_NAME).iss(ISS).sub(SUB).isActive(true).build();

                when(accountRepository.findAccountByIssAndSub(ISS, SUB)).thenReturn(Optional.of(existingActiveAccountForUser));

                accountService.createAccount(account, user);
            });
        }

        @Test
        void createAccountShouldUpdateExistingAccountToActiveWhenAccountExistsButInactive() {

            User user = User.builder().iss(ISS).sub(SUB).build();
            Account account = Account.builder().name(ACCOUNT_NAME).build();

            Account existingInactiveAccountForUser = Account.builder().name(ACCOUNT_NAME).iss(ISS).sub(SUB).isActive(false).build();

            when(accountRepository.findAccountByIssAndSub(ISS, SUB)).thenReturn(Optional.of(existingInactiveAccountForUser));

            accountService.createAccount(account, user);

            verify(accountRepository).save(assertArg(a -> {
                assertEquals(ISS, a.getIss());
                assertEquals(SUB, a.getSub());
                assertTrue(a.isActive());
            }));

        }

        @Test
        void createAccountShouldInvokeSaveMethodOnAccountRepository() {
            User user = User.builder().iss(ISS).sub(SUB).build();
            Account account = Account.builder().name(ACCOUNT_NAME).build();

            accountService.createAccount(account, user);

            verify(accountRepository).save(assertArg(a -> {
                assertEquals(ISS, account.getIss());
                assertEquals(SUB, account.getSub());
            }));
        }
    }

    @Nested
    class GetAccountByUserTest {
        @Test
        void getAccountByUserShouldInvokeFindAccountByIssAndSubOnAccountRepository() {
            User user = User.builder().iss(ISS).sub(SUB).build();
            Account account = Account.builder().name(ACCOUNT_NAME).build();

            when(accountRepository.findAccountByIssAndSub(ISS, SUB)).thenReturn(Optional.of(account));

            accountService.getAccountByUser(user);

            verify(accountRepository).findAccountByIssAndSub(eq(ISS), eq(SUB));
        }

        @Test
        void getAccountByUserShouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
            User user = User.builder().iss(ISS).sub(SUB).build();

            assertThrows(AccountNotFoundException.class, () -> {
                accountService.getAccountByUser(user);
            });
        }

    }

}