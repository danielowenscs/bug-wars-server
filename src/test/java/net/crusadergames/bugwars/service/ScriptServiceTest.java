package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.exceptions.*;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ScriptServiceTest {
    private final User USER = new User(1L,"jeff", "gmail@email.com", "passing");
    private final User USER_2 = new User(2L,"mac", "gmail@email.com", "passing");
    private final User USER_NEW = new User(1L, "andrew", "gmail@email.com", "passing");
    private final User USER_FAKE = new User();

    private final Script SCRIPT_1 = new Script(1L,"First Script", "I am a script", LocalDate.now(),LocalDate.now(),USER);
    private final Script SCRIPT_2 = new Script(2L,"Second Script", "Eat chicken", LocalDate.now(),LocalDate.now(),USER_2);
    private final Script SCRIPT_Old = new Script(1L, "Old Script", "Old Food!", LocalDate.now(),LocalDate.now(), USER_NEW);
    private final Script SCRIPT_NEW = new Script(1L, "andrew", "Updated Script", LocalDate.now(),LocalDate.now(), USER_NEW);


    private ScriptService scriptService;
    private ScriptRepository scriptRepository;
    private UserRepository userRepository;
    private Principal mockPrincipal;

    @BeforeEach
    public void beforeEachTest(){
        userRepository = Mockito.mock(UserRepository.class);
        scriptRepository = Mockito.mock(ScriptRepository.class);
        mockPrincipal = Mockito.mock(Principal.class);
        scriptService = new ScriptService(scriptRepository,userRepository);
    }

    @Test
    public void createNewScript_shouldReturnCreatedScript()  {
        ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
        when(scriptRepository.findScriptByName(any())).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenReturn(USER);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(SCRIPT_1);

        Script createdScript = scriptService.createNewScript(mockPrincipal,request);

        Assert.assertNotNull (createdScript);
        Assert.assertEquals(createdScript.getScriptId(),SCRIPT_1.getScriptId());
        Assert.assertEquals(createdScript,SCRIPT_1);
    }

    @Test
    public void createNewScript_shouldReturnScriptNameExistsExceptionWhenRelevant()  {
        Assert.assertThrows(ScriptNameAlreadyExistsException.class, () ->{
            ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
            when(scriptRepository.findScriptByName(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.createNewScript(mockPrincipal,request);
        });
    }

    @Test
    public void createNewScript_shouldThrowScriptSaveErrorWhenTitleIsNull()  {
        Assert.assertThrows(ScriptSaveException.class, () ->{
            ScriptRequest request = new ScriptRequest("", "I am a script");
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_FAKE));
            when(scriptRepository.findScriptByName(any())).thenReturn(Optional.empty());
            when(userRepository.save(Mockito.any(User.class))).thenReturn(USER);

            scriptService.createNewScript(mockPrincipal,request);
        });
    }

    @Test
    public void createNewScript_shouldThrowUserNotFoundException() {
        Assert.assertThrows(UserNotFoundException.class, () ->{
            ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
            when(userRepository.findByUsername("Mac")).thenReturn(Optional.empty());

            scriptService.createNewScript(mockPrincipal,request);
        });
    }

    @Test
    public void createNewScript_shouldThrowScriptSaveException()  {
        Assert.assertThrows(ScriptSaveException.class, () ->{
            ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
            when(scriptRepository.findScriptByName(any())).thenReturn(Optional.empty());

            when(scriptRepository.save(any())).thenThrow(new ScriptSaveException());

            scriptService.createNewScript(mockPrincipal,request);
        });
    }

    @Test
    void deleteScriptById_shouldDeleteScriptById() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
        when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

        scriptService.deleteScriptById(1L, mockPrincipal);

        when(scriptRepository.findById(1L)).thenReturn(Optional.empty());
    }

    @Test
    void deleteScriptById_shouldThrowScriptNotFoundException() {
        Assert.assertThrows(ScriptNotFoundException.class, () ->{
            when(scriptRepository.findById(any())).thenReturn(Optional.empty());

            scriptService.deleteScriptById(1L, mockPrincipal);
        });
    }

    @Test
    void deleteScriptById_shouldThrowUserNotFoundException() {
        Assert.assertThrows(UserNotFoundException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.deleteScriptById(1L, mockPrincipal);
        });
    }

    @Test
    void deleteScriptById_shouldThrowScriptDoesNotBelongException() {
        Assert.assertThrows(ScriptDoesNotBelongToUserException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_2));
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.deleteScriptById(1L, mockPrincipal);
        });
    }

    @Test
    void getScript_shouldReturnCorrectScript() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
        when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

        Script script = scriptService.getScript(1L, mockPrincipal);

        Assert.assertNotNull (SCRIPT_1);
        Assert.assertEquals(script.getScriptId(), SCRIPT_1.getScriptId());
        Assert.assertEquals(script, SCRIPT_1);
    }

    @Test
    void getScript_shouldThrowScriptDoesNotBelongException() {
        Assert.assertThrows(ScriptDoesNotBelongToUserException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_2));
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.getScript(1L, mockPrincipal);
        });
    }

    @Test
    void getScript_shouldThrowUserNotFoundException() {
        Assert.assertThrows(UserNotFoundException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.getScript(1L, mockPrincipal);
        });
    }

    @Test
    void getScript_shouldThrowScriptNotFoundException() {
        Assert.assertThrows(ScriptNotFoundException.class, () ->{
            when(scriptRepository.findById(any())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_2));

            scriptService.getScript(1L, mockPrincipal);
        });
    }

    @Test
    void updateOldScript_shouldReturnNewScript() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_NEW));
        when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_Old));

        ScriptRequest requestOld = new ScriptRequest("Old Script", "Old Food!");
        ScriptRequest requestNew = new ScriptRequest("andrew", "Updated Script");
        scriptService.createNewScript(mockPrincipal, requestOld);

        Script script = scriptService.updateOldScript(mockPrincipal, requestNew, 1L);

        Assert.assertNotNull (script);
        Assert.assertEquals(script.getScriptId(), SCRIPT_NEW.getScriptId());
        Assert.assertEquals(script, SCRIPT_NEW);
    }

    @Test
    void updateOldScript_shouldThrowScriptNotFoundException() {
        Assert.assertThrows(ScriptNotFoundException.class, () ->{
            when(scriptRepository.findById(any())).thenReturn(Optional.empty());
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_2));

            ScriptRequest requestNew = new ScriptRequest("andrew", "Updated Script");
            scriptService.updateOldScript(mockPrincipal, requestNew, 1L);
        });
    }

    @Test
    void updateOldScript_shouldThrowUserNotFoundException() {
        Assert.assertThrows(UserNotFoundException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            ScriptRequest requestNew = new ScriptRequest("andrew", "Updated Script");
            scriptService.updateOldScript(mockPrincipal, requestNew, 1L);
        });
    }

    @Test
    void updateOldScript_shouldThrowScriptDoesNotBelongException() {
        Assert.assertThrows(ScriptDoesNotBelongToUserException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_2));
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            ScriptRequest requestNew = new ScriptRequest("andrew", "Updated Script");
            scriptService.updateOldScript(mockPrincipal, requestNew, 1L);
        });
    }

    @Test
    void getAllScriptsByUser_shouldReturnListOfScriptsUnderAUser() {
        Script script1 = new Script(1L, "Script 1", "Script 1", LocalDate.now(),LocalDate.now(), USER_NEW);
        Script script2 = new Script(2L, "Script 2", "Script 2", LocalDate.now(),LocalDate.now(), USER_NEW);

        List<Script> expected = new ArrayList<>();
        expected.add(script1);
        expected.add(script2);
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER_NEW));
        when(scriptRepository.findScriptsByName(any())).thenReturn(expected);

        ScriptRequest requestUno = new ScriptRequest("Script 1", "Script 1");
        scriptService.createNewScript(mockPrincipal, requestUno);

        ScriptRequest requestDos = new ScriptRequest("Script 2", "Script 2");
        scriptService.createNewScript(mockPrincipal, requestDos);

        List<Script> script = scriptService.getAllScriptsByUser(mockPrincipal);

        Assert.assertNotNull (script);
        Assert.assertEquals(expected, script);

    }

    @Test
    void getAllScriptsByUser_shouldThrowUserNotFoundException() {
        Assert.assertThrows(UserNotFoundException.class, () ->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
            when(scriptRepository.findById(any())).thenReturn(Optional.of(SCRIPT_1));

            scriptService.getAllScriptsByUser(mockPrincipal);
        });
    }

}

