package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.LoginRequest;
import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.dto.request.SignupRequest;
import net.crusadergames.bugwars.dto.response.JwtResponse;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import net.crusadergames.bugwars.security.jwt.JwtUtils;
import net.crusadergames.bugwars.service.AuthService;
import net.crusadergames.bugwars.service.ScriptService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ScriptService scriptService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScriptRepository scriptRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // Tests scrapped for now
//    @Test
//    @WithMockUser
//    public void postScript_returnsScriptResponse() throws Exception {
//        ScriptRequest scriptRequest = new ScriptRequest(); // populate with test data
//        Script script = new Script(); // populate with test data
//        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User())); // replace with actual test data
//        when(scriptService.createNewScript(anyLong(), any(ScriptRequest.class))).thenReturn(script);
//
//        ResultActions response = mockMvc.perform(post("/script/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(scriptRequest)))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    public void getAllScripts_returns_Response() throws Exception {
//        SignupRequest signupRequest = new SignupRequest("tester", "tester@hotmail.com", "passwordtest");
//        User savedUser = authService.registerUser(signupRequest);
//
//        Principal mockPrincipal = Mockito.mock(Principal.class);
//        Mockito.when(mockPrincipal.getName()).thenReturn("tester");
//
//        ScriptRequest scriptRequest = new ScriptRequest("script", "body");
//        Script testScript = new Script(3L, "script", "body", LocalDate.now(), LocalDate.now(), savedUser);
//        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));
//        when(scriptRepository.save(any(Script.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        Script result = scriptService.createNewScript(savedUser.getId(), scriptRequest);
//
//
//        mockMvc.perform(get("/api/scripts/all")
//                .principal(mockPrincipal)).andExpect(status().isOk());



//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/api/scripts/all")
//                .principal(mockPrincipal)
//                .accept(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        MockHttpServletResponse response = result.getResponse();
//        int status = response.getStatus();
//        Assert.assertEquals("Incorrect response", 200, status);


//        ResultActions listResponse = mockMvc.perform(get("/api/scripts/all")).andExpect(status().isOk());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.body").exists());
//    }




}

