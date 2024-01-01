package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.exceptions.TopicAccessDeniedException;
import com.mrbpurnachandra.jourvicebackend.exceptions.TopicNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.repositories.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Topic saveTopic(Topic topic, User user) {
        topic.setUser(user);

        return topicRepository.save(topic);
    }

    private Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    @Transactional
    public void deleteTopic(Long id, User user) {
        Optional<Topic> topic = findById(id);

        topic.ifPresent((t) -> {
            if (!t.getUser().equals(user))
                throw new TopicAccessDeniedException();
            topicRepository.delete(t);
        });
    }

    @Transactional
    public Topic getTopic(Long id, User user) {
        Optional<Topic> topic = findById(id);

        if (topic.isEmpty()) throw new TopicNotFoundException();

        Topic t = topic.get();

        if (!t.getUser().equals(user))
            throw new TopicAccessDeniedException();

        return t;
    }
}
