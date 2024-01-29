package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.controller.ScriptController;
import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import net.crusadergames.bugwars.service.ScriptService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptControllerTest {

    private final User USER = new User(1L,"jeff", "gmail@email.com", "passing");
    private final User USER_2 = new User(2L,"mac", "gmail@email.com", "passing");
    private final User USER_NEW = new User(1L, "andrew", "gmail@email.com", "passing");
    private final User USER_FAKE = new User();

    private final Script SCRIPT_1 = new Script(1L,"First Script", "I am a script", LocalDate.now(),LocalDate.now(), USER);
    private final Script SCRIPT_2 = new Script(2L,"Second Script", "Eat chicken", LocalDate.now(),LocalDate.now(), USER_2);
    private final Script SCRIPT_Uno = new Script(1L, "Old Script", "Old Food!", LocalDate.now(),LocalDate.now(), USER_NEW);
    private final Script SCRIPT_Dos = new Script(1L, "andrew", "Updated Script", LocalDate.now(),LocalDate.now(), USER_NEW);


    private ScriptService scriptService;
    private ScriptController scriptController;
    private Principal mockPrincipal;

    @BeforeEach
    public void beforeEachTest(){
        mockPrincipal = Mockito.mock(Principal.class);
        scriptService = Mockito.mock(ScriptService.class);
        scriptController = new ScriptController(scriptService);
    }

    @Test
    public void postScripts_shouldReturnCreatedScript() {
        ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
        when(scriptService.createNewScript(mockPrincipal, request)).thenReturn(SCRIPT_1);

        ResponseEntity<Script> createdScript = scriptController.postScript(request, mockPrincipal);

        Assert.assertEquals(SCRIPT_1, createdScript.getBody());
        Assert.assertEquals(HttpStatus.CREATED, createdScript.getStatusCode());
    }

    @Test
    public void deleteScript_shouldReturnDeletedScriptMessage() {
        when(scriptService.deleteScriptById(1L, mockPrincipal)).thenReturn("Script Deleted");

        ResponseEntity<String> deletedScript = scriptController.deleteScript(1L, mockPrincipal);

        Assert.assertEquals("Script Deleted", deletedScript.getBody());
        Assert.assertEquals(HttpStatus.OK, deletedScript.getStatusCode());
    }

    @Test
    public void getUserScript_shouldReturnUserScript() {
        when(scriptService.getScript(1L, mockPrincipal)).thenReturn(SCRIPT_1);

        ResponseEntity<Script> retrievedScript = scriptController.getUserScript(1L, mockPrincipal);

        Assert.assertEquals(SCRIPT_1, retrievedScript.getBody());
        Assert.assertEquals(HttpStatus.OK, retrievedScript.getStatusCode());
    }

    @Test
    public void getAllScriptsByUser_shouldReturnAllScriptsFromUser() {
        List<Script> expectedScript = new ArrayList<>();
        expectedScript.add(SCRIPT_Uno);
        expectedScript.add(SCRIPT_Dos);
        when(scriptService.getAllScriptsByUser(mockPrincipal)).thenReturn(expectedScript);

        List<Script> listOfScripts = scriptController.getAllScriptsByUser(mockPrincipal);

        Assert.assertEquals(expectedScript, listOfScripts);
    }

    @Test
    public void updateScript_shouldReturnUpdatedScript() {
        ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
        when(scriptService.updateOldScript(mockPrincipal, request, 1L)).thenReturn(SCRIPT_1);

        ResponseEntity<Script> updatedScript = scriptController.updateScript(request, mockPrincipal, 1L);

        Assert.assertEquals(SCRIPT_1, updatedScript.getBody());
        Assert.assertEquals(HttpStatus.ACCEPTED, updatedScript.getStatusCode());
    }

}
