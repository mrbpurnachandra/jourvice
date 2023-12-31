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

    @Nested
    class AddNoteTest {
        @Test
        void addNoteShouldInvokeGetTopicMethodOnTopicService() {
            String content = "Content of Note";

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();

            Note note = Note.builder().content(content).build();

            noteService.saveNote(note, 1L, user);

            verify(topicService).getTopic(eq(1L), eq(user));
        }

        @Test
        void addNoteShouldInvokeSaveMethodOnNoteRepository() {
            String content = "Content of Note";

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();
            Topic topic = Topic.builder().id(1L).build();

            Note note = Note.builder().content(content).build();

            when(topicService.getTopic(topic.getId(), user)).thenReturn(topic);

            noteService.saveNote(note, topic.getId(), user);

            verify(noteRepository).save(assertArg(n -> {
                assertEquals(topic, n.getTopic());
            }));
        }
    }

    @Nested
    class DeleteNoteTest {
        @Test
        void deleteNoteShouldInvokeGetTopicMethodOnTopicService() {
            long noteId = 1L;
            long topicId = 1L;

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();

            Note note = Note.builder().id(noteId).build();
            when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

            noteService.deleteNote(noteId, topicId, user);

            verify(topicService).getTopic(eq(topicId), eq(user));
        }

        @Test
        void deleteNoteShouldInvokeDeleteMethodOnNoteRepository() {
            Note note = Note.builder().id(1L).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.deleteNote(note.getId(), 1L, null);

            verify(noteRepository).delete(note);
        }
    }

    @Nested
    class GetNoteTest {
        @Test
            // This test ensures that user is authorized to access content of
            // topic
        void getNoteShouldInvokeGetTopicMethodOnTopicService() {
            long noteId = 1L;
            long topicId = 1L;

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();
            Note note = Note.builder().id(noteId).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.getNote(noteId, topicId, user);

            verify(topicService).getTopic(eq(topicId), eq(user));
        }

        @Test
        void getNoteShouldInvokeFindNoteByIdMethodOnNoteRepository() {
            Note note = Note.builder().id(1L).build();

            when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

            noteService.getNote(note.getId(), 1L, null);

            verify(noteRepository).findById(eq(note.getId()));
        }

        @Test
        void getNoteShouldThrowNoteNotFoundExceptionWhenNoteIsNotPresent() {
            assertThrows(NoteNotFoundException.class, () -> {
                noteService.getNote(1L, 1L, null);
            });
        }
    }
}