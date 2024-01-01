package com.mrbpurnachandra.jourvicebackend.mappers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteAdditionInfoDto;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteAdditionInfoDtoMapper {
    public Note mapToNote(NoteAdditionInfoDto noteAdditionInfoDto) {
        if (noteAdditionInfoDto == null) return null;

        Note note = new Note();

        note.setContent(noteAdditionInfoDto.getContent());

        if (noteAdditionInfoDto.getMood() != null) {
            Mood mood = Mood.builder().id(noteAdditionInfoDto.getMood().getId()).build();
            note.setMood(mood);
        }

        return note;
    }
}
