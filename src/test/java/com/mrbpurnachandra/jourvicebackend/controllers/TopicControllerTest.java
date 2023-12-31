package com.mrbpurnachandra.jourvicebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
class TopicControllerTest {
    static final String TOPIC_NAME = "Name";
    static final String TOPIC_DESCRIPTION = "Description";
    static final String ISS = "https://accounts.google.com";
    static final String SUB = "123456789";
    static final long TOPIC_ID = 1L;
    static final String TOPIC_BASE_URL = "/topic";
    static final String TOPIC_URL = TOPIC_BASE_URL + "/" + TOPIC_ID;

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
            mockMvc.perform(post(TOPIC_BASE_URL)).andExpect(status().isForbidden());
        }

        @Test
        void addTopicShouldReturnBadRequestWithoutTopicData() throws Exception {
            mockMvc.perform(post(TOPIC_BASE_URL).with(jwt())).andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @MethodSource("parameters")
        void addTopicShouldReturnBadRequestWhenDataDoNotMeetCriteria(String name, String description) throws Exception {
            Topic topic = Topic.builder().name(name).description(description).build();

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(topic);

            mockMvc.perform(
                            post(TOPIC_BASE_URL)
                                    .with(jwt())
                                    .content(content)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void addTopicShouldInvokeSaveTopicMethodOnTopicService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Topic topic = Topic.builder().name(TOPIC_NAME).description(TOPIC_DESCRIPTION).build();

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(topic);

            mockMvc.perform(
                    post(TOPIC_BASE_URL)
                            .with(jwt().jwt(j -> j.claim("iss", ISS).claim("sub", SUB)))
                                    .content(content)
                                    .contentType(MediaType.APPLICATION_JSON));

            verify(topicService).saveTopic(eq(topic), eq(user));
        }

    }

    @Nested
    class DeleteTopicTest {
        @Test
        void deleteTopicShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(delete(TOPIC_URL)).andExpect(status().isForbidden());
        }

        @Test
        void deleteTopicShouldInvokeDeleteTopicMethodOnTopicService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            mockMvc.perform(delete(TOPIC_URL).with(jwt().jwt(j -> j.claim("iss", ISS).claim("sub", SUB))));
            verify(topicService).deleteTopic(eq(TOPIC_ID), eq(user));
        }
    }

    @Nested
    class GetTopicTest {
        @Test
        void getTopicShouldReturnUnauthorizedWithoutUser() throws Exception {
            mockMvc.perform(get(TOPIC_URL)).andExpect(status().isUnauthorized());
        }

        @Test
        void getTopicShouldInvokeGetTopicMethodOnTopicService() throws Exception {
            User user = User.builder().sub(SUB).iss(ISS).build();

            mockMvc.perform(get(TOPIC_URL).with(jwt().jwt(j -> j.claim("sub", SUB).claim("iss", ISS))));

            verify(topicService).getTopic(eq(TOPIC_ID), eq(user));

        }
    }
}