package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.MoodNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
import com.mrbpurnachandra.jourvicebackend.repositories.MoodRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MoodServiceTest {
    static final Integer MOOD_ID = 1;

    @Autowired
    MoodService moodService;

    @MockBean
    MoodRepository moodRepository;

    @Nested
    class GetMoodTest {
        @Test
        void getMoodShouldThrowMoodNotFoundExceptionWhenMoodIsNotPresent() {
            assertThrows(MoodNotFoundException.class, () -> {
                moodService.getMood(MOOD_ID);
            });
        }

        @Test
        void getMoodShouldInvokeFindByIdMethodOnMoodRepository() {
            when(moodRepository.findById(MOOD_ID)).thenReturn(Optional.of(new Mood()));

            moodService.getMood(MOOD_ID);

            verify(moodRepository).findById(eq(MOOD_ID));
        }

    }
}