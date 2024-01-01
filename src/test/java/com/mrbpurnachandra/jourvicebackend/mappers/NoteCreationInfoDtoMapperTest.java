package com.mrbpurnachandra.jourvicebackend.mappers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteCreationInfoDto;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NoteCreationInfoDtoMapperTest {
    static final String NOTE_CONTENT = "Note Content";
    static final Integer MOOD_ID = 1;

    @Autowired
    NoteCreationInfoDtoMapper noteCreationInfoDtoMapper;

    @Test
    void mapToNoteReturnsNoteForNoteInputDTO() {
        NoteCreationInfoDto.MoodDto moodDto = NoteCreationInfoDto.MoodDto.builder().id(MOOD_ID).build();
        NoteCreationInfoDto noteCreationInfoDto = NoteCreationInfoDto.builder().content(NOTE_CONTENT).mood(moodDto).build();

        Note note = noteCreationInfoDtoMapper.mapToNote(noteCreationInfoDto);

        assertEquals(note.getContent(), noteCreationInfoDto.getContent());
        assertEquals(note.getMood().getId(), moodDto.getId());
    }
}