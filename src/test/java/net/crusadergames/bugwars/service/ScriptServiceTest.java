package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ScriptServiceTest {

    @Autowired
    private ScriptService scriptService;

    @MockBean
    private ScriptRepository scriptRepository;

    @MockBean
    private UserRepository userRepository;

}


