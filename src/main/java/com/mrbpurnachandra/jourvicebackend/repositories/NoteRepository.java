package com.mrbpurnachandra.jourvicebackend.repositories;

import com.mrbpurnachandra.jourvicebackend.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
