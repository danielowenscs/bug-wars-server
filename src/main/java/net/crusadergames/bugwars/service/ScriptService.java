package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScriptService {

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    UserRepository userRepository;

    public Script createNewScript(Long user_id, ScriptRequest scriptRequest) {
        Optional<User> user = userRepository.findById(user_id);
        if(user.isPresent()){
            LocalDate currentDate = LocalDate.now();
            Script script = new Script(null, scriptRequest.getScript_name(), scriptRequest.getScript_body(), currentDate,currentDate,user.get());
            List<Script> userScripts = user.get().getScripts();
            if (userScripts == null) {
                userScripts = new ArrayList<>();
                user.get().setScripts(userScripts);
            }
            userScripts.add(script);
            userRepository.save(user.get());
            scriptRepository.save(script);
            return script;
        }
        return null;
    }

}
