package com.mrbpurnachandra.jourvicebackend.mappers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteCreationInfoDto;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteCreationInfoDtoMapper {
    public Note mapToNote(NoteCreationInfoDto noteCreationInfoDto) {
        if (noteCreationInfoDto == null) return null;

        Note note = new Note();

        note.setContent(noteCreationInfoDto.getContent());

        if (noteCreationInfoDto.getMood() != null) {
            Mood mood = Mood.builder().id(noteCreationInfoDto.getMood().getId()).build();
            note.setMood(mood);
        }

        return note;
    }
}
