package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.service.ScriptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScriptService scriptService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScriptController scriptController;

    @Test
    @WithMockUser
    public void shouldCreateNewScript() throws Exception {
        ScriptRequest scriptRequest = new ScriptRequest(); // populate with data
        Script script = new Script(); // populate with data

        when(scriptService.createNewScript(any(), any())).thenReturn(script);

        mockMvc.perform(post("/api/scripts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // replace with actual JSON representation of scriptRequest
                .andExpect(status().isCreated());

        verify(scriptService, times(1)).createNewScript(any(), any());
    }

    @Test
    @WithMockUser
    public void shouldNotCreateNewScriptWhenServiceFails() throws Exception {
        ScriptRequest scriptRequest = new ScriptRequest(); // populate with data

        when(scriptService.createNewScript(any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/scripts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // replace with actual JSON representation of scriptRequest
                .andExpect(status().isBadRequest());

        verify(scriptService, times(1)).createNewScript(any(), any());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void deleteScriptTest() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = new User("testUser", "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")).toString());
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(context);

        Long scriptId = 1L;
        scriptController.deleteScript(scriptId, SecurityContextHolder.getContext().getAuthentication());
        verify(scriptService, times(1)).deleteScriptById(scriptId, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @WithMockUser
    public void shouldReturnScript() throws Exception {
        Long scriptId = 1L;
        Script script = new Script();
        given(scriptService.getScript(scriptId, null)).willReturn(new ResponseEntity<>(script, HttpStatus.OK));
        mockMvc.perform(get("/api/scripts/" + scriptId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldUpdateScript() throws Exception {
        Script script = new Script();
        script.setScript_id(1L);
        when(scriptService.updateOldScript(any(), any(), any())).thenReturn(script);
        mockMvc.perform(put("/api/scripts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"value\"}")) // replace with actual request body
                .andExpect(status().isCreated());

        verify(scriptService, times(1)).updateOldScript(any(), any(), any());
    }

    @Test
    @WithMockUser
    public void shouldNotUpdateScriptWhenNull() throws Exception {
        when(scriptService.updateOldScript(any(), any(), any())).thenReturn(null);
        mockMvc.perform(put("/api/scripts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"value\"}")) // replace with actual request body
                .andExpect(status().isBadRequest());

        verify(scriptService, times(1)).updateOldScript(any(), any(), any());
    }

}

