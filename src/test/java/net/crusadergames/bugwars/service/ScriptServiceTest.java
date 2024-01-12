package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.exceptions.ScriptNameAlreadyExistsException;
import net.crusadergames.bugwars.exceptions.ScriptSaveException;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ScriptServiceTest {
    private final User USER = new User(1L,"jeff", "gmail@email.com", "passing");
    private final Script SCRIPT_1 = new Script(1L,"First Script",
            "I am a script", LocalDate.now(),LocalDate.now(),USER);
    private final Script SCRIPT_2 = new Script(2L,"Second Script",
            "Eat chicken", LocalDate.now(),LocalDate.now(),USER);


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
        //ARRANGE
//        Script expectedScript = new Script(1L, "First Script",
//                "I am a script", LocalDate.now(),LocalDate.now(),USER);
        ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
        when(scriptRepository.findScriptByName(any())).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenReturn(USER);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(SCRIPT_1);
        // ACT
        Script createdScript = scriptService.createNewScript(mockPrincipal,request);

        // ASSERT
        Assert.assertNotNull (createdScript);
        Assert.assertEquals(createdScript.getScriptId(),SCRIPT_1.getScriptId());
        Assert.assertEquals(createdScript,SCRIPT_1);
    }
    @Test
    public void createNewScript_shouldReturnScriptNameExistsExceptionWhenRelevant()  {
        //ARRANGE
        // ASSERT
        Assert.assertThrows(ScriptNameAlreadyExistsException.class, () ->{
            ScriptRequest request = new ScriptRequest("First Script", "I am a Script");
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
            when(scriptRepository.findScriptByName(any())).thenReturn(Optional.of(SCRIPT_1));
            when(userRepository.save(Mockito.any(User.class))).thenReturn(USER);
            // ACT
            Script createdScript = scriptService.createNewScript(mockPrincipal,request);
        });
    }
    @Test
    public void createNewScript_shouldThrowScriptSaveErrorWhenTitleIsNull()  {
        //ARRANGE
        // ASSERT
        Assert.assertThrows(ScriptSaveException.class, () ->{
            ScriptRequest request = new ScriptRequest("", "I am a script");
            when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
            when(scriptRepository.findScriptByName(any())).thenReturn(Optional.empty());
            when(userRepository.save(Mockito.any(User.class))).thenReturn(USER);
            // ACT
            Script createdScript = scriptService.createNewScript(mockPrincipal,request);
        });
    }

    @Test
    void deleteScriptById() {
    }

    @Test
    void getScript_shouldReturnCorrectScript() {
    }

    @Test
    void updateOldScript() {
    }

    @Test
    void getAllScriptsByUser() {
    }





}

