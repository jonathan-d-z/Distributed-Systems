package com.example.profileservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profileApiRejectsRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/profiles/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profileApiCreatesProfileForAuthenticatedSubject() throws Exception {
        mockMvc.perform(get("/profiles/me").with(jwt().jwt(token -> token
                        .subject("test-subject")
                        .claim("preferred_username", "test-user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("test-subject"))
                .andExpect(jsonPath("$.username").value("test-user"));
    }
}
