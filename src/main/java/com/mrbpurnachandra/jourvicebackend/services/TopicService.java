package com.mrbpurnachandra.jourvicebackend.services;

import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.repositories.TopicRepository;
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

    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }
}
