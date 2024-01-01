package com.mrbpurnachandra.jourvicebackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // iss and sub should uniquely identify account
    private String iss;
    private String sub;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    private boolean isActive = true;

    private Timestamp createdAt = new Timestamp(new Date().getTime());

    public void setName(String name) {
        this.name = name.trim();
    }
}
