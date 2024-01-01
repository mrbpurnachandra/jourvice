package com.mrbpurnachandra.jourvicebackend.controllers;

import com.mrbpurnachandra.jourvicebackend.models.Topic;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.services.TopicService;
import com.mrbpurnachandra.jourvicebackend.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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

        User user = AuthenticationUtils.getUser(authentication);

        return topicService.saveTopic(topic, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable("id") Long topicId, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        topicService.deleteTopic(topicId, user);
    }

    @GetMapping("/{id}")
    public Topic getTopic(@PathVariable("id") Long id, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        return topicService.getTopic(id, user);
    }
}
