package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.exceptions.*;
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

    public ScriptService(ScriptRepository scriptRepository,
                         UserRepository userRepository) {
        this.scriptRepository = scriptRepository;
        this.userRepository = userRepository;
    }

    public Script createNewScript(Principal principal, ScriptRequest scriptRequest) {
        if (scriptRequest.getName().isBlank() || scriptRequest.getBody().isBlank()) {
            throw new ScriptSaveException();
        }
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        Optional<Script> optionalScript = scriptRepository.findScriptByName(scriptRequest.getName());
        if (optionalScript.isPresent()) {
            throw new ScriptNameAlreadyExistsException();
        }
        User user = optionalUser.get();
        LocalDate currentDate = LocalDate.now();
        Script script = new Script(null, scriptRequest.getName().toLowerCase(), scriptRequest.getBody(), currentDate,
                currentDate, user);
        script = scriptRepository.save(script);
        userRepository.save(user);
        return script;
    }

    public ResponseEntity<?> deleteScriptById(Long scriptId, Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        if (optionalScript.isEmpty()) {
            throw new ScriptNotFoundException();
        }

        Script script = optionalScript.get();
        User currentUser = optionalUser.get();
        if (!currentUser.getId().equals(script.getUser().getId())) {
            throw new ScriptDoesNotBelongToUserException();
        }
        scriptRepository.deleteById(scriptId);
        userRepository.save(currentUser);
        return new ResponseEntity<>("Script deleted", HttpStatus.OK);
    }

    public ResponseEntity<Script> getScript(Long scriptId, Principal principal) {
        Optional<User> userOptional = userRepository.findByUsername(principal.getName());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = userOptional.get();
        Optional<Script> scriptOptional = scriptRepository.findById(scriptId);
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
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        if (optionalScript.isEmpty()) {
            throw new ScriptNotFoundException();
        }

        Optional<Script> testScriptNameOptional = scriptRepository.findScriptByName(scriptRequest.getName());
        if(testScriptNameOptional.isPresent() && !(testScriptNameOptional.get().getScriptId().equals(optionalScript.get().getScriptId()))){
            throw new ScriptNameAlreadyExistsException();
        }

        Script oldScript = optionalScript.get();
        User currentUser = optionalUser.get();
        if (!currentUser.getScripts().contains(oldScript)) {
            throw new ScriptDoesNotBelongToUserException();
        }

        LocalDate currentDate = LocalDate.now();
        Script newScript = new Script(scriptId, scriptRequest.getName().toLowerCase(), scriptRequest.getBody(),
                oldScript.getDateCreated(), currentDate, currentUser);
        scriptRepository.save(newScript);
        userRepository.save(currentUser);
        return newScript;
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
