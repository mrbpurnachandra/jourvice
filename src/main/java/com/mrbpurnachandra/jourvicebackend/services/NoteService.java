package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteCreationInfoDto;
import com.mrbpurnachandra.jourvicebackend.exceptions.InvalidMoodException;
import com.mrbpurnachandra.jourvicebackend.exceptions.MoodNotFoundException;
import com.mrbpurnachandra.jourvicebackend.exceptions.NoteNotFoundException;
import com.mrbpurnachandra.jourvicebackend.mappers.NoteCreationInfoDtoMapper;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteService {
    private final TopicService topicService;
    private final MoodService moodService;
    private final NoteRepository noteRepository;
    private final NoteCreationInfoDtoMapper noteCreationInfoDtoMapper;

    @Autowired
    public NoteService(TopicService topicService, MoodService moodService, NoteRepository noteRepository, NoteCreationInfoDtoMapper noteCreationInfoDtoMapper) {
        this.topicService = topicService;
        this.moodService = moodService;
        this.noteRepository = noteRepository;
        this.noteCreationInfoDtoMapper = noteCreationInfoDtoMapper;
    }

    public Note addNote(NoteCreationInfoDto noteCreationInfoDto, Long topicId, User user) {
        Topic topic = topicService.getTopic(topicId, user);

        Note note = noteCreationInfoDtoMapper.mapToNote(noteCreationInfoDto);

        note.setTopic(topic);

        // This is to ensure that user does not specify invalid mood
        if (note.getMood() != null) {
            try {
                Mood mood = moodService.getMood(note.getMood().getId());
                note.setMood(mood);
            } catch (MoodNotFoundException e) {
                throw new InvalidMoodException();
            }
        }

        return noteRepository.save(note);
    }

    public void deleteNote(Long id, Long topicId, User user) {
        // This handles the authorization because user other than owner of topic
        // cannot get the topic
        topicService.getTopic(topicId, user);

        Optional<Note> note = noteRepository.findById(id);

        note.ifPresent(noteRepository::delete);
    }

    public Note getNote(Long id, Long topicId, User user) {
        // This handles the authorization because user other than owner of topic
        // cannot get the topic
        topicService.getTopic(topicId, user);

        return noteRepository.findById(id).orElseThrow(NoteNotFoundException::new);
    }
}
