package com.mrbpurnachandra.jourvicebackend.controllers;

import com.mrbpurnachandra.jourvicebackend.dtos.NoteAdditionInfoDto;
import com.mrbpurnachandra.jourvicebackend.mappers.NoteAdditionInfoDtoMapper;
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
    private final NoteAdditionInfoDtoMapper noteAdditionInfoDtoMapper;

    @Autowired
    public NoteController(NoteService noteService, NoteAdditionInfoDtoMapper noteAdditionInfoDtoMapper) {
        this.noteService = noteService;
        this.noteAdditionInfoDtoMapper = noteAdditionInfoDtoMapper;
    }

    @PostMapping
    public Note addNote(@PathVariable("topicId") Long topicId, @Valid @RequestBody NoteAdditionInfoDto noteAdditionInfoDto, JwtAuthenticationToken authentication) {
        User user = AuthenticationUtils.getUser(authentication);

        Note note = noteAdditionInfoDtoMapper.mapToNote(noteAdditionInfoDto);

        return noteService.saveNote(note, topicId, user);
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
