package com.mrbpurnachandra.jourvicebackend.utils;

import com.mrbpurnachandra.jourvicebackend.models.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public class AuthenticationUtils {
    public static User getUser(JwtAuthenticationToken authentication) {
        assert authentication != null;

        Map<String, Object> attributes = authentication.getTokenAttributes();

        String sub = (String) attributes.get("sub");
        String iss = (String) attributes.get("iss");

        return User.builder().sub(sub).iss(iss).build();
    }
}
