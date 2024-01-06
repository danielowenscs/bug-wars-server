package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ScriptServiceTest {

    @Autowired
    private ScriptService scriptService;

    @MockBean
    private ScriptRepository scriptRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void createNewScript_returnsScript() {
        Long userId = 1L;
        ScriptRequest scriptRequest = new ScriptRequest("scriptName", "scriptBody");
        User user = new User();
        user.setId(userId);
        Script script = new Script(null, "scriptName", "scriptBody", LocalDate.now(), LocalDate.now(), user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scriptRepository.save(any(Script.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Script result = scriptService.createNewScript(userId, scriptRequest);

        assertEquals(script.getScript_name(), result.getScript_name());
        assertEquals(script.getBody(), result.getBody());
        verify(userRepository, times(1)).save(user);
        verify(scriptRepository, times(1)).save(script);
    }

    @Test
    public void testCreateNewScriptWhenUserNotFound() {
        Long userId = 1L;
        ScriptRequest scriptRequest = new ScriptRequest("Test Script", "Test Body");
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        Script result = scriptService.createNewScript(userId, scriptRequest);
        assertNull(result);

        verify(userRepository, times(1)).findById(userId);
        verify(scriptRepository, never()).save(any());
    }

    @Test

    public void testGetAllScriptsForUser() {
        long userId = 1L;
        List<Script> testList = scriptService.getAllScripts(userId);
        assertNotNull(testList);
    }

}
