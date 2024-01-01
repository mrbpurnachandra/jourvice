package com.mrbpurnachandra.jourvicebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbpurnachandra.jourvicebackend.models.Account;
import com.mrbpurnachandra.jourvicebackend.models.User;
import com.mrbpurnachandra.jourvicebackend.services.AccountService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Account associates raw iss and sub obtained from OAuth to a user account. Account
 * service should be implemented as separate service. But for time being it is implemented here
 * in the Journal Service (Jourvice).
 */

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    static final String ACCOUNT_BASE_URL = "/account";
    static final String ACCOUNT_NAME = "Prakash Bhattarai";
    static final String ISS = "https://accounts.google.com";
    static final String SUB = "123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @Nested
    class CreateAccountTest {
        private static Arguments[] parameters() {
            List<Arguments> arguments = new ArrayList<>();

            arguments.add(Arguments.arguments("P"));
            arguments.add(Arguments.arguments("P  "));
            arguments.add(Arguments.arguments("P".repeat(33)));

            return arguments.toArray(new Arguments[0]);
        }

        @Test
        void createAccountShouldReturnForbiddenWithoutUser() throws Exception {
            mockMvc.perform(post(ACCOUNT_BASE_URL)).andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @MethodSource("parameters")
        void createAccountShouldReturnBadRequestWhenProvidedDataDoNotMeetCriteria(String name) throws Exception {
            Account account = Account.builder()
                    .name(name)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(account);

            mockMvc.perform(post(ACCOUNT_BASE_URL).content(body).contentType(MediaType.APPLICATION_JSON).with(jwt())).andExpect(status().isBadRequest());
        }

        @Test
        void createAccountShouldInvokeCreateAccountMethodOnAccountService() throws Exception {
            User user = User.builder()
                    .iss(ISS)
                    .sub(SUB)
                    .build();

            Account account = Account.builder()
                    .name(ACCOUNT_NAME)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(account);

            mockMvc.perform(post(ACCOUNT_BASE_URL).content(body).contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(j -> j.claim("iss", ISS).claim("sub", SUB))));

            verify(accountService).createAccount(eq(account), eq(user));
        }
    }
}