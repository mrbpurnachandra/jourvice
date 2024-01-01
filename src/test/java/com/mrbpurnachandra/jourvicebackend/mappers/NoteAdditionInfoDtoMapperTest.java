package com.mrbpurnachandra.jourvicebackend.mappers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteAdditionInfoDto;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NoteAdditionInfoDtoMapperTest {
    static final String NOTE_CONTENT = "Note Content";
    static final Integer MOOD_ID = 1;

    @Autowired
    NoteAdditionInfoDtoMapper noteAdditionInfoDtoMapper;

    @Test
    void mapToNoteReturnsNoteForNoteInputDTO() {
        NoteAdditionInfoDto.MoodDto moodDto = NoteAdditionInfoDto.MoodDto.builder().id(MOOD_ID).build();
        NoteAdditionInfoDto noteAdditionInfoDto = NoteAdditionInfoDto.builder().content(NOTE_CONTENT).mood(moodDto).build();

        Note note = noteAdditionInfoDtoMapper.mapToNote(noteAdditionInfoDto);

        assertEquals(note.getContent(), noteAdditionInfoDto.getContent());
        assertEquals(note.getMood().getId(), moodDto.getId());
    }
}