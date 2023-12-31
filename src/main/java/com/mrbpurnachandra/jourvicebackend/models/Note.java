package com.mrbpurnachandra.jourvicebackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 256)
    private String content;

    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @ManyToOne
    private Topic topic;

    public void setContent(String content) {
        this.content = content.trim();
    }
}
