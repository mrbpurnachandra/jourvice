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
    @MockBean
    TopicRepository topicRepository;

    @Autowired
    TopicService topicService;

    @Nested
    class SaveTopicTest {

        @Test
        void saveTopicShouldInvokeSaveMethodOnTopicRepository() {
            String sub = "123456789";
            String iss = "https://account.google.com";

            User user = User.builder().sub(sub).iss(iss).build();
            Topic topic = Topic.builder().name("Topic").description("Topic Description").build();

            topicService.saveTopic(topic, user);

            verify(topicRepository).save(assertArg(t -> assertEquals(t.getUser(), user)));
        }
    }

    @Nested
    class DeleteTopicTest {
        @Test
        void deleteTopicShouldThrowAccessDeniedExceptionWhenTryingToDeleteOthersTopic() {
            assertThrows(AccessDeniedException.class, () -> {
                String sub = "123456789";
                String iss = "https://account.google.com";

                User user1 = User.builder().sub(sub).iss(iss).build();
                User user2 = User.builder().sub("Other Sub").iss(iss).build();

                Topic topic = Topic.builder().id(1L).name("Topic").description("Topic Description").user(user1).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

                topicService.deleteTopic(1L, user2);
            });
        }

        @Test
        void deleteTopicShouldInvokeDeleteMethodOnTopicRepository() {
            String sub = "123456789";
            String iss = "https://account.google.com";

            User user = User.builder().sub(sub).iss(iss).build();

            Topic topic = Topic.builder().id(1L).name("Topic").description("Topic Description").user(user).build();

            when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

            topicService.deleteTopic(1L, user);

            verify(topicRepository).delete(eq(topic));
        }
    }

    @Nested
    class GetTopicTest {

        @Test
        void getTopicShouldThrowTopicNotFoundExceptionWhenTopicIsNotPresent() {
            assertThrows(TopicNotFoundException.class, () -> {
                String sub = "123456789";
                String iss = "https://account.google.com";

                User user = User.builder().sub(sub).iss(iss).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

                topicService.getTopic(1L, user);
            });

        }

        @Test
        void getTopicShouldThrowAccessDeniedExceptionWhenTryingToAccessOthersTopic() {
            assertThrows(AccessDeniedException.class, () -> {
                String sub = "123456789";
                String iss = "https://account.google.com";

                User user1 = User.builder().sub(sub).iss(iss).build();
                User user2 = User.builder().sub("Other Sub").iss(iss).build();

                Topic topic = Topic.builder().id(1L).name("Topic").description("Topic Description").user(user1).build();

                when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

                topicService.getTopic(1L, user2);
            });
        }

        @Test
        void getTopicShouldReturnTopic() {
            String sub = "123456789";
            String iss = "https://account.google.com";

            User user = User.builder().sub(sub).iss(iss).build();

            Topic topic = Topic.builder().id(1L).name("Topic").description("Topic Description").user(user).build();

            when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

            Topic returnedTopic = topicService.getTopic(1L, user);

            assertEquals(topic, returnedTopic);
        }
    }
}