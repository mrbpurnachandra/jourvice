package com.mrbpurnachandra.jourvicebackend.controllers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteCreationInfoDto;
import com.mrbpurnachandra.jourvicebackend.mappers.NoteCreationInfoDtoMapper;
import com.mrbpurnachandra.jourvicebackend.models.Note;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.services.NoteService;
import com.mrbpurnachandra.jourvicebackend.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic/{topicId}/note")
public class NoteController {
    private final NoteService noteService;
    private final NoteCreationInfoDtoMapper noteCreationInfoDtoMapper;

    @Autowired
    public NoteController(NoteService noteService, NoteCreationInfoDtoMapper noteCreationInfoDtoMapper) {
        this.noteService = noteService;
        this.noteCreationInfoDtoMapper = noteCreationInfoDtoMapper;
    }

    @PostMapping
    public Note addNote(@PathVariable("topicId") Long topicId, @Valid @RequestBody NoteCreationInfoDto noteCreationInfoDto, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        Note note = noteCreationInfoDtoMapper.mapToNote(noteCreationInfoDto);

        return noteService.addNote(note, topicId, user);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable("topicId") Long topicId, @PathVariable("id") Long id, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        noteService.deleteNote(id, topicId, user);
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable("topicId") Long topicId, @PathVariable("id") Long id, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        return noteService.getNote(id, topicId, user);
    }
}
