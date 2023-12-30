package com.mrbpurnachandra.jourvicebackend.repositories;

import com.mrbpurnachandra.jourvicebackend.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
