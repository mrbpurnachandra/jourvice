package com.mrbpurnachandra.jourvicebackend.controllers;

import com.mrbpurnachandra.jourvicebackend.exceptions.TopicNotFoundException;
import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.services.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/topic")
public class TopicController {
    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public Topic addTopic(@Valid @RequestBody Topic topic, JwtAuthenticationToken authentication) {

        Map<String, Object> attributes = authentication.getTokenAttributes();

        String sub = (String) attributes.get("sub");
        String iss = (String) attributes.get("iss");

        topic.setSub(sub);
        topic.setIss(iss);

        return topicService.save(topic);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable("id") Long id, JwtAuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getTokenAttributes();

        String sub = (String) attributes.get("sub");
        String iss = (String) attributes.get("iss");

        Optional<Topic> topic = topicService.findById(id);

        topic.ifPresent((t) -> {
            if (!t.getSub().equals(sub) || !t.getIss().equals(iss))
                throw new AccessDeniedException("Unauthorized operation");
            topicService.delete(t);
        });
    }

    @GetMapping("/{id}")
    public Topic getTopic(@PathVariable("id") Long id, JwtAuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getTokenAttributes();

        String sub = (String) attributes.get("sub");
        String iss = (String) attributes.get("iss");

        Optional<Topic> topic = topicService.findById(id);

        if (topic.isEmpty()) throw new TopicNotFoundException();

        Topic t = topic.get();

        if (!t.getSub().equals(sub) || !t.getIss().equals(iss))
            throw new AccessDeniedException("Unauthorized operation");

        return t;
    }
}
