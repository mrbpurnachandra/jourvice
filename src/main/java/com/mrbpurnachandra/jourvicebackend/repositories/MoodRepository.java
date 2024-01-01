package com.mrbpurnachandra.jourvicebackend.repositories;

import com.mrbpurnachandra.jourvicebackend.models.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Integer> {
}
