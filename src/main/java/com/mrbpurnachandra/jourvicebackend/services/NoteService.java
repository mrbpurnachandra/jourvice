package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.NoteNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteService {
    private final TopicService topicService;
    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(TopicService topicService, NoteRepository noteRepository) {
        this.topicService = topicService;
        this.noteRepository = noteRepository;
    }


    @Transactional
    public Note saveNote(Note note, Long topicId, User user) {
        Topic topic = topicService.getTopic(topicId, user);

        note.setTopic(topic);

        return noteRepository.save(note);
    }

    @Transactional
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
