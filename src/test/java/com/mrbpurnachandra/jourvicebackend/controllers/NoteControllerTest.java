package com.mrbpurnachandra.jourvicebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.services.NoteService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
class NoteControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    NoteService noteService;

    @Nested
    class AddNoteTest {
        private static final Long TOPIC_ID = 1L;
        private static final String BASE_URL = "/topic/" + TOPIC_ID + "/note";

        private static Arguments[] parameters() {
            List<Arguments> arguments = new ArrayList<>();

            arguments.add(Arguments.arguments("C"));
            arguments.add(Arguments.arguments("C  "));
            arguments.add(Arguments.arguments("C".repeat(257)));

            return arguments.toArray(new Arguments[0]);
        }

        @Test
        void addNoteShouldShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(post(BASE_URL)).andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @MethodSource("parameters")
        void addNoteShouldReturnBadRequestWhenDataDoNotMeetCriteria(String content) throws Exception {
            Note note = Note.builder().content(content).build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(note);

            mockMvc.perform(post(BASE_URL).content(body).contentType(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isBadRequest());
        }

        @Test
        void addNoteShouldInvokeSaveNoteMethodOnNoteService() throws Exception {
            String content = "Content of Note";

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();

            Note note = Note.builder().content(content).build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(note);

            mockMvc.perform(
                    post(BASE_URL)
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(jwt().jwt(j -> j.claim("sub", sub).claim("iss", iss))));

            verify(noteService).saveNote(eq(note), eq(TOPIC_ID), eq(user));
        }
    }

    @Nested
    class DeleteNoteTest {
        private static final Long TOPIC_ID = 1L;
        private static final String BASE_URL = "/topic/" + TOPIC_ID + "/note";

        @Test
        void deleteNoteShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(delete(BASE_URL)).andExpect(status().isForbidden());
        }

        @Test
        void deleteNoteShouldInvokeDeleteNoteMethodOnNoteService() throws Exception {
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            final long NOTE_ID = 1L;

            User user = User.builder().sub(sub).iss(iss).build();

            mockMvc.perform(delete(BASE_URL + "/" + NOTE_ID).with(jwt().jwt(j -> j.claim("iss", iss).claim("sub", sub))));

            verify(noteService).deleteNote(eq(NOTE_ID), eq(TOPIC_ID), eq(user));
        }
    }

    @Nested
    class GetNoteTest {
        private static final Long TOPIC_ID = 1L;
        private static final String BASE_URL = "/topic/" + TOPIC_ID + "/note";

        @Test
        void getNoteShouldReturnUnauthorizedWithoutUser() throws Exception {
            final long NOTE_ID = 1L;
            mockMvc.perform(get(BASE_URL + "/" + NOTE_ID)).andExpect(status().isUnauthorized());
        }

        @Test
        void getNoteShouldInvokeGetNoteMethodOnNoteService() throws Exception {
            final long NOTE_ID = 1L;

            String iss = "https://accounts.google.com";
            String sub = "123456789";

            User user = User.builder().sub(sub).iss(iss).build();

            mockMvc.perform(get(BASE_URL + "/" + NOTE_ID).with(jwt().jwt(j -> j.claim("sub", sub).claim("iss", iss))));

            verify(noteService).getNote(eq(NOTE_ID), eq(TOPIC_ID), eq(user));
        }
    }
}