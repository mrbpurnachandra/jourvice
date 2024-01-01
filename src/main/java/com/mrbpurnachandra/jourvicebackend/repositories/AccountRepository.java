package com.mrbpurnachandra.jourvicebackend.repositories;

import com.mrbpurnachandra.jourvicebackend.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a WHERE a.iss = :iss AND a.sub = :sub")
    Optional<Account> findAccountByIssAndSub(String iss, String sub);
}
