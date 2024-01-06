package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
        if (user.isPresent()) {
            LocalDate currentDate = LocalDate.now();
            Script script = new Script(null, scriptRequest.getScript_name(), scriptRequest.getScript_body(), currentDate, currentDate, user.get());
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

    public List<Script> getAllScripts(Long user_id) {
        Optional<User> user = userRepository.findById(user_id);
        if (user.isPresent()) {
            List<Script> scriptList = new ArrayList<>();
            scriptList = scriptRepository.findByOwner_Id(user_id);
            return scriptList;
        }
        return null;
    }

    public ResponseEntity<?> deleteScriptById(Long scriptId, Principal principal) {
        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (optionalScript.isEmpty()) {
            return new ResponseEntity<>("Bo script found with id " + scriptId, HttpStatus.NOT_FOUND);
        }
        if (user.isEmpty()) {
            return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
        }
        Script script = optionalScript.get();
        User currentUser = user.get();
        if (!currentUser.getScripts().contains(script)) {
            return new ResponseEntity<>("The script with the id: " + script.getScript_id() + " does not belong to this user", HttpStatus.NOT_FOUND);
        }
        currentUser.getScripts().remove(script);
        userRepository.save(currentUser);
        scriptRepository.deleteById(scriptId);
        return new ResponseEntity<>("Script deleted", HttpStatus.OK);
    }
}


