package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.TopicNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.TopicRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TopicServiceTest {
    static final String SUB = "123456789";
    static final String OTHER_SUB = "Other Sub";
    static final String ISS = "https://account.google.com";
    static final Long TOPIC_ID = 1L;
    static final String TOPIC_NAME = "Topic";
    static final String TOPIC_DESCRIPTION = "Topic Description";

    @MockBean
    TopicRepository topicRepository;

    @Autowired
    TopicService topicService;

    @Nested
    class SaveTopicTest {


        @Test
        void saveTopicShouldInvokeSaveMethodOnTopicRepository() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Topic topic = Topic.builder().name(TOPIC_NAME).description(TOPIC_DESCRIPTION).build();

            topicService.saveTopic(topic, user);

            verify(topicRepository).save(assertArg(t -> assertEquals(t.getUser(), user)));
        }
    }

    @Nested
    class DeleteTopicTest {
        @Test
        void deleteTopicShouldThrowAccessDeniedExceptionWhenTryingToDeleteOthersTopic() {
            assertThrows(AccessDeniedException.class, () -> {
                User user1 = User.builder().sub(SUB).iss(ISS).build();
                User user2 = User.builder().sub(OTHER_SUB).iss(ISS).build();
                Topic topic = Topic.builder().id(TOPIC_ID).name(TOPIC_NAME).description(TOPIC_DESCRIPTION).user(user1).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

                topicService.deleteTopic(TOPIC_ID, user2);
            });
        }

        @Test
        void deleteTopicShouldInvokeDeleteMethodOnTopicRepository() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Topic topic = Topic.builder().id(TOPIC_ID).name(TOPIC_NAME).description(TOPIC_DESCRIPTION).user(user).build();

            when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

            topicService.deleteTopic(TOPIC_ID, user);

            verify(topicRepository).delete(eq(topic));
        }
    }

    @Nested
    class GetTopicTest {

        @Test
        void getTopicShouldThrowTopicNotFoundExceptionWhenTopicIsNotPresent() {
            assertThrows(TopicNotFoundException.class, () -> {
                User user = User.builder().sub(SUB).iss(ISS).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

                topicService.getTopic(TOPIC_ID, user);
            });

        }

        @Test
        void getTopicShouldThrowAccessDeniedExceptionWhenTryingToAccessOthersTopic() {
            assertThrows(AccessDeniedException.class, () -> {
                User user1 = User.builder().sub(SUB).iss(ISS).build();
                User user2 = User.builder().sub(OTHER_SUB).iss(ISS).build();
                Topic topic = Topic.builder().id(TOPIC_ID).name(TOPIC_NAME).description(TOPIC_DESCRIPTION).user(user1).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

                topicService.getTopic(TOPIC_ID, user2);
            });
        }

        @Test
        void getTopicShouldReturnTopic() {
            User user = User.builder().sub(SUB).iss(ISS).build();
            Topic topic = Topic.builder().id(TOPIC_ID).name(TOPIC_NAME).description(TOPIC_DESCRIPTION).user(user).build();

            when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

            Topic returnedTopic = topicService.getTopic(TOPIC_ID, user);

            assertEquals(topic, returnedTopic);
        }
    }
}