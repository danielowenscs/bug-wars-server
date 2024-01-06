package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.exceptions.ScriptDoesNotBelongToUserException;
import net.crusadergames.bugwars.exceptions.ScriptNotFoundException;
import net.crusadergames.bugwars.exceptions.ScriptSaveException;
import net.crusadergames.bugwars.exceptions.UserNotFoundException;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScriptService {

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    UserRepository userRepository;

    public Script createNewScript(Principal principal, ScriptRequest scriptRequest) {
        try {
            Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException();
            }
            User user = optionalUser.get();
            LocalDate currentDate = LocalDate.now();
            Script script = new Script(null, scriptRequest.getScript_name(), scriptRequest.getScript_body(), currentDate, currentDate, user);
            scriptRepository.save(script);
            userRepository.save(user);
            return script;
        } catch (Exception e) {
            throw new ScriptSaveException();
        }
    }

    public ResponseEntity<?> deleteScriptById(Long scriptId, Principal principal) {
        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (optionalScript.isEmpty()) {
            throw new ScriptNotFoundException();
        }
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        Script script = optionalScript.get();
        User currentUser = user.get();
        if (!currentUser.getId().equals(script.getUser().getId())) {
            throw new ScriptDoesNotBelongToUserException();
        }
        scriptRepository.deleteById(scriptId);
        userRepository.save(currentUser);
        return new ResponseEntity<>("Script deleted", HttpStatus.OK);
    }

    public ResponseEntity<Script> getScript(Long scriptId, Principal principal) {
        Optional<User> userOptional = userRepository.findByUsername(principal.getName());
        Optional<Script> scriptOptional = scriptRepository.findById(scriptId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = userOptional.get();
        if (scriptOptional.isEmpty()) {
            throw new ScriptNotFoundException();
        }

        Script script = scriptOptional.get();
        if (!user.getId().equals(script.getUser().getId())) {
            throw new ScriptDoesNotBelongToUserException();
        }
        return new ResponseEntity<>(script, HttpStatus.OK);
    }

    public Script updateOldScript(Principal principal, ScriptRequest scriptRequest, Long scriptId) {
        try {
            Optional<Script> optionalScript = scriptRepository.findById(scriptId);
            Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException();
            }
            if (optionalScript.isEmpty()) {
                throw new ScriptNotFoundException();
            }

            Script oldScript = optionalScript.get();
            User currentUser = optionalUser.get();
            if (!currentUser.getScripts().contains(oldScript)) {
                throw new ScriptDoesNotBelongToUserException();
            }

            LocalDate currentDate = LocalDate.now();
            Script newScript = new Script(scriptId, scriptRequest.getScript_name(), scriptRequest.getScript_body(), oldScript.getDate_created(), currentDate, currentUser);
            scriptRepository.save(newScript);
            userRepository.save(currentUser);
            return newScript;
        } catch (Exception e) {
            throw new ScriptSaveException();
        }

    }

    public List<Script> getAllScriptsByUser(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        return scriptRepository.getScriptsByUser(user);
    }
}
