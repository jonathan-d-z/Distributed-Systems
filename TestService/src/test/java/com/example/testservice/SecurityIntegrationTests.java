package com.example.testservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void foodApiRejectsRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/foods"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void foodApiAcceptsAuthenticatedJwt() throws Exception {
        mockMvc.perform(get("/foods").with(jwt().jwt(token -> token.subject("test-subject"))))
                .andExpect(status().isOk());
    }
}
