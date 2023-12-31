package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.NoteNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.NoteRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class NoteServiceTest {
    @Autowired
    NoteService noteService;

    @MockBean
    TopicService topicService;

    @MockBean
    NoteRepository noteRepository;

    final String ISS = "https://accounts.google.com";
    final String SUB = "123456789";
    final long TOPIC_ID = 1L;
    final long NOTE_ID = 1L;
    final String NOTE_CONTENT = "Content of Note";

    @Nested
    class AddNoteTest {
        @Test
        void addNoteShouldInvokeGetTopicMethodOnTopicService() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Note note = Note.builder().content(NOTE_CONTENT).build();

            noteService.saveNote(note, TOPIC_ID, user);

            verify(topicService).getTopic(eq(TOPIC_ID), eq(user));
        }

        @Test
        void addNoteShouldInvokeSaveMethodOnNoteRepository() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Topic topic = Topic.builder().id(TOPIC_ID).build();
            Note note = Note.builder().content(NOTE_CONTENT).build();

            when(topicService.getTopic(topic.getId(), user)).thenReturn(topic);

            noteService.saveNote(note, topic.getId(), user);

            verify(noteRepository).save(assertArg(n -> assertEquals(topic, n.getTopic())));
        }
    }

    @Nested
    class DeleteNoteTest {
        @Test
        void deleteNoteShouldInvokeGetTopicMethodOnTopicService() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Note note = Note.builder().id(NOTE_ID).build();

            when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(note));

            noteService.deleteNote(NOTE_ID, TOPIC_ID, user);

            verify(topicService).getTopic(eq(TOPIC_ID), eq(user));
        }

        @Test
        void deleteNoteShouldInvokeDeleteMethodOnNoteRepository() {
            Note note = Note.builder().id(NOTE_ID).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.deleteNote(note.getId(), TOPIC_ID, null);

            verify(noteRepository).delete(note);
        }
    }

    @Nested
    class GetNoteTest {
        @Test
            // This test ensures that user is authorized to access content of
            // topic
        void getNoteShouldInvokeGetTopicMethodOnTopicService() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Note note = Note.builder().id(NOTE_ID).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.getNote(NOTE_ID, TOPIC_ID, user);

            verify(topicService).getTopic(eq(TOPIC_ID), eq(user));
        }

        @Test
        void getNoteShouldInvokeFindNoteByIdMethodOnNoteRepository() {
            Note note = Note.builder().id(NOTE_ID).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.getNote(note.getId(), TOPIC_ID, null);

            verify(noteRepository).findById(eq(note.getId()));
        }

        @Test
        void getNoteShouldThrowNoteNotFoundExceptionWhenNoteIsNotPresent() {
            assertThrows(NoteNotFoundException.class, () -> noteService.getNote(NOTE_ID, TOPIC_ID, null));
        }
    }
}