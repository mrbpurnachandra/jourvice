package com.mrbpurnachandra.jourvicebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.services.TopicService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class TopicControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TopicService topicService;

    @Nested
    class AddTopicTest {
        private static Arguments[] parameters() {
            List<Arguments> parameters = new ArrayList<>();

            parameters.add(Arguments.arguments("N", "Description"));
            parameters.add(Arguments.arguments("N".repeat(33), "Description"));
            parameters.add(Arguments.arguments("N   ", "Description"));
            parameters.add(Arguments.arguments("Name", "D"));
            parameters.add(Arguments.arguments("Name", "D".repeat(129)));
            parameters.add(Arguments.arguments("Name", "D    "));

            return parameters.toArray(new Arguments[0]);
        }

        @Test
        void addTopicShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(post("/topic")).andExpect(status().isForbidden());
        }

        @Test
        void addTopicShouldReturnBadRequestWithoutTopicData() throws Exception {
            mockMvc.perform(post("/topic").with(jwt())).andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("parameters")
        void addTopicShouldReturnBadRequestWhenDataDoNotMeetCriteria(String name, String description) throws Exception {
            Topic topic = Topic.builder().name(name).description(description).build();

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(topic);

            mockMvc.perform(
                            post("/topic")
                                    .with(jwt())
                                    .content(content)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void addTopicShouldReturnOkWithSavedTopic() throws Exception {
            String name = "Name";
            String description = "Description";
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            when(topicService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Topic topic = Topic.builder().name(name).description(description).build();

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(topic);

            mockMvc.perform(
                            post("/topic")
                                    .with(jwt().jwt(j -> j.claim("iss", iss).claim("sub", sub)))
                                    .content(content)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.iss").value(iss))
                    .andExpect(jsonPath("$.sub").value(sub));

        }

    }

    @Nested
    class DeleteTopicTest {
        @Test
        void deleteTopicShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(delete("/topic/1")).andExpect(status().isForbidden());
        }

        @Test
        void deleteTopicShouldReturnForbiddenWhenTryingToDeleteOthersTopic() throws Exception {
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            // Topic with different sub
            Topic topic = Topic.builder().name("Name").description("Description").iss(iss).sub("000000001").build();

            when(topicService.findById(anyLong())).thenReturn(Optional.of(topic));

            mockMvc.perform(delete("/topic/1").with(jwt().jwt(j -> j.claim("iss", iss).claim("sub", sub)))).andExpect(status().isForbidden());

        }

        @Test
        void deleteTopicShouldReturnNoContent() throws Exception {
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            // Topic with different sub
            Topic topic = Topic.builder().name("Name").description("Description").iss(iss).sub("123456789").build();

            when(topicService.findById(anyLong())).thenReturn(Optional.of(topic));

            mockMvc.perform(delete("/topic/1").with(jwt().jwt(j -> j.claim("iss", iss).claim("sub", sub)))).andExpect(status().isNoContent());

        }
    }

    @Nested
    class GetTopicTest {
        @Test
        void getTopicShouldReturnUnauthorizedWithoutUser() throws Exception {
            mockMvc.perform(get("/topic/1")).andExpect(status().isUnauthorized());
        }

        @Test
        void getTopicShouldReturnNotFoundWhenTopicIsNotPresent() throws Exception {
            mockMvc.perform(get("/topic/1").with(jwt())).andExpect(status().isNotFound());
        }

        @Test
        void getTopicShouldReturnForbiddenWhenTryingToAccessOthersTopic() throws Exception {
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            // Topic with different sub
            Topic topic = Topic.builder().name("Name").description("Description").iss(iss).sub("000000001").build();

            when(topicService.findById(anyLong())).thenReturn(Optional.of(topic));

            mockMvc.perform(get("/topic/1").with(jwt().jwt(j -> j.claim("sub", sub).claim("iss", iss)))).andExpect(status().isForbidden());
        }

        @Test
        void getTopicShouldReturnTopic() throws Exception {
            String iss = "https://accounts.google.com";
            String sub = "123456789";

            // Topic with different sub
            Topic topic = Topic.builder().name("Name").description("Description").iss(iss).sub(sub).build();

            when(topicService.findById(anyLong())).thenReturn(Optional.of(topic));

            mockMvc.perform(get("/topic/1").with(jwt().jwt(j -> j.claim("sub", sub).claim("iss", iss)))).andExpect(status().isOk());

        }
    }
}