package com.mrbpurnachandra.jourvicebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbpurnachandra.jourvicebackend.dtos.NoteCreationInfoDto;
import com.mrbpurnachandra.jourvicebackend.mappers.NoteCreationInfoDtoMapper;
import com.mrbpurnachandra.jourvicebackend.models.Mood;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
class NoteControllerTest {
    static final String ISS = "https://accounts.google.com";
    static final String SUB = "123456789";
    static final Long TOPIC_ID = 1L;
    static final Long NOTE_ID = 1L;
    static final Integer MOOD_ID = 1;
    static final String NOTE_CONTENT = "Content of Note";
    static final String NOTE_BASE_URL = "/topic/" + TOPIC_ID + "/note";
    static final String NOTE_URL = NOTE_BASE_URL + "/" + NOTE_ID;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NoteCreationInfoDtoMapper noteCreationInfoDtoMapper;

    @MockBean
    NoteService noteService;

    @Nested
    class AddNoteTest {
        private static Arguments[] parameters() {
            List<Arguments> arguments = new ArrayList<>();

            arguments.add(Arguments.arguments("C"));
            arguments.add(Arguments.arguments("C  "));
            arguments.add(Arguments.arguments("C".repeat(257)));

            return arguments.toArray(new Arguments[0]);
        }

        @Test
        void addNoteShouldShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(post(NOTE_BASE_URL)).andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @MethodSource("parameters")
        void addNoteShouldReturnBadRequestWhenDataDoNotMeetCriteria(String content) throws Exception {
            Note note = Note.builder().content(content).build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(note);

            mockMvc.perform(post(NOTE_BASE_URL).content(body).contentType(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isBadRequest());
        }

        @Test
        void addNoteShouldInvokeSaveNoteMethodOnNoteService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            Note note = Note.builder().content(NOTE_CONTENT).build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(note);

            when(noteCreationInfoDtoMapper.mapToNote(any())).thenReturn(note);

            mockMvc.perform(
                    post(NOTE_BASE_URL)
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(jwt().jwt(j -> j.claim("sub", SUB).claim("iss", ISS))));

            verify(noteService).addNote(eq(note), eq(TOPIC_ID), eq(user));
        }

        @Test
        void addNoteShouldAddMoodToNoteIfProvidedBeforeInvokingSaveNoteMethodOnNoteService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            NoteCreationInfoDto.MoodDto moodDto = NoteCreationInfoDto.MoodDto.builder().id(MOOD_ID).build();
            NoteCreationInfoDto noteCreationInfoDto = NoteCreationInfoDto.builder().content(NOTE_CONTENT).mood(moodDto).build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(noteCreationInfoDto);

            Mood mood = Mood.builder().id(MOOD_ID).build();
            Note note = Note.builder().content(NOTE_CONTENT).mood(mood).build();

            when(noteCreationInfoDtoMapper.mapToNote(any())).thenReturn(note);

            mockMvc.perform(
                    post(NOTE_BASE_URL)
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(jwt().jwt(j -> j.claim("sub", SUB).claim("iss", ISS))));

            verify(noteService).addNote(eq(note), eq(TOPIC_ID), eq(user));
        }
    }

    @Nested
    class DeleteNoteTest {
        @Test
        void deleteNoteShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(delete(NOTE_BASE_URL)).andExpect(status().isForbidden());
        }

        @Test
        void deleteNoteShouldInvokeDeleteNoteMethodOnNoteService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            mockMvc.perform(delete(NOTE_URL).with(jwt().jwt(j -> j.claim("iss", ISS).claim("sub", SUB))));

            verify(noteService).deleteNote(eq(NOTE_ID), eq(TOPIC_ID), eq(user));
        }
    }

    @Nested
    class GetNoteTest {
        @Test
        void getNoteShouldReturnUnauthorizedWithoutUser() throws Exception {
            mockMvc.perform(get(NOTE_URL)).andExpect(status().isUnauthorized());
        }

        @Test
        void getNoteShouldInvokeGetNoteMethodOnNoteService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            mockMvc.perform(get(NOTE_URL).with(jwt().jwt(j -> j.claim("sub", SUB).claim("iss", ISS))));

            verify(noteService).getNote(eq(NOTE_ID), eq(TOPIC_ID), eq(user));
        }
    }
}