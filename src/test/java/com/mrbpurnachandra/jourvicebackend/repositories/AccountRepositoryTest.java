package com.mrbpurnachandra.jourvicebackend.repositories;

import com.mrbpurnachandra.jourvicebackend.models.Account;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@DataJpaTest
@ActiveProfiles({"test"})
class AccountRepositoryTest {
    public static final String OTHER_ISS = "000000001";
    static final String ACCOUNT_NAME = "Prakash Bhattarai";
    static final String ISS = "https://accounts.google.com";
    static final String SUB = "123456789";
    @Autowired
    AccountRepository accountRepository;

    @Nested
    class FindAccountByIssAndSubTest {
        @Test
        void findAccountByIssAndSubShouldReturnNonEmptyResult() {
            Account account = Account.builder().name(ACCOUNT_NAME).iss(ISS).sub(SUB).build();
            accountRepository.save(account);

            Optional<Account> optional = accountRepository.findAccountByIssAndSub(ISS, SUB);

            assertTrue(optional.isPresent());
            assertEquals(account.getIss(), optional.get().getIss());
            assertEquals(account.getSub(), optional.get().getSub());
        }

        @Test
        void findAccountByIssAndSubShouldReturnEmptyResult() {
            Account account = Account.builder().name(ACCOUNT_NAME).iss(ISS).sub(SUB).build();
            accountRepository.save(account);

            Optional<Account> optional = accountRepository.findAccountByIssAndSub(ISS, OTHER_ISS);

            assertTrue(optional.isEmpty());
        }
    }
}