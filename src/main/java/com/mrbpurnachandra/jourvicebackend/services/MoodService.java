package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.MoodNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
import com.mrbpurnachandra.jourvicebackend.repositories.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodRepository moodRepository;

    @Autowired
    public MoodService(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    public Mood getMood(Integer id) {
        return moodRepository.findById(id).orElseThrow(MoodNotFoundException::new);
    }
}
