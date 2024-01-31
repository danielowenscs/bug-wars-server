package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.exceptions.*;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static net.crusadergames.bugwars.Util.Constants.RESPONSE_SCRIPTDELETED;

@Service
public class ScriptService {

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    UserRepository userRepository;

    public ScriptService(ScriptRepository scriptRepository, UserRepository userRepository){
        this.scriptRepository = scriptRepository;
        this.userRepository = userRepository;
    }

    public Script createNewScript(Principal principal, ScriptRequest scriptRequest) {
        if (scriptRequest.getName().isBlank() || scriptRequest.getBody().isBlank()) {
            throw new ScriptSaveException();
        }

        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        throwUserNotFound(optionalUser);

        Optional<Script> optionalScript = scriptRepository.findScriptByName(scriptRequest.getName());
        if (optionalScript.isPresent()) {
            throw new ScriptNameAlreadyExistsException();
        }

        User user = optionalUser.get();
        Script script = new Script(null,scriptRequest.getName(), scriptRequest.getBody(), LocalDate.now(), LocalDate.now(), user);

        script = scriptRepository.save(script);

        return script;
    }

    public String deleteScriptById(Long scriptId, Principal principal) {
        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        Optional<User> user = userRepository.findByUsername(principal.getName());

        throwScriptNotPresent(optionalScript);
        throwUserNotFound(user);

        User scriptUser = optionalScript.get().getUser();
        User currentUser = user.get();

        throwScriptDoesNotBelongToUser(currentUser, scriptUser);

        scriptRepository.deleteById(scriptId);
        return RESPONSE_SCRIPTDELETED;
    }

    public Script getScript(Long scriptId, Principal principal) {
        Optional<User> userOptional = userRepository.findByUsername(principal.getName());

        Optional<Script> scriptOptional = scriptRepository.findById(scriptId);

        throwUserNotFound(userOptional);
        throwScriptNotPresent(scriptOptional);

        Script script = scriptOptional.get();

        User currentUser = userOptional.get();
        User scriptUser = script.getUser();

        throwScriptDoesNotBelongToUser(currentUser, scriptUser);

        return script;
    }

    public Script updateOldScript(Principal principal, ScriptRequest scriptRequest, Long scriptId) {
        Optional<Script> optionalScript = scriptRepository.findById(scriptId);
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        throwUserNotFound(optionalUser);
        throwScriptNotPresent(optionalScript);

        Script oldScript = optionalScript.get();
        User currentUser = optionalUser.get();
        User scriptUser = optionalScript.get().getUser();

        throwScriptDoesNotBelongToUser(currentUser, scriptUser);

        LocalDate currentDate = LocalDate.now();
        Script newScript = new Script(scriptId, scriptRequest.getName(), scriptRequest.getBody(), oldScript.getDateCreated(), currentDate, currentUser);
        scriptRepository.save(newScript);

        return newScript;
    }

    public List<Script> getAllScriptsByUser(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        throwUserNotFound(optionalUser);

        User user = optionalUser.get();
        return scriptRepository.findScriptsByUser(user);
    }

    private void throwUserNotFound(Optional<User> user) {
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
    }

    private void throwScriptNotPresent(Optional<Script> script) {
        if (script.isEmpty()) {
            throw new ScriptNotFoundException();
        }
    }

    private void throwScriptDoesNotBelongToUser(User currentUser, User scriptUser) {
        if (!currentUser.getId().equals(scriptUser.getId())) {
            throw new ScriptDoesNotBelongToUserException();
        }
    }
}