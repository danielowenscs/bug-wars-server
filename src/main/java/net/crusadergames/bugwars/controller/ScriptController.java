package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.repository.script.ScriptRepository;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scripts")
@PreAuthorize("isAuthenticated()")
public class ScriptController {
    @Autowired
    ScriptService scriptService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScriptRepository scriptRepository;

    @PostMapping()
    public ResponseEntity<Script> postScript(@RequestBody ScriptRequest scriptResponse, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Script script = scriptService.createNewScript(user.get().getId(), scriptResponse);
        return new ResponseEntity<>(script, HttpStatus.OK);
    }

<<<<<<< HEAD
    @GetMapping("/all")
    public ResponseEntity<List<Script>> getAllScripts(Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        List<Script> scriptList= scriptService.getAllScripts(user.get().getId());
        return new ResponseEntity<>(scriptList, HttpStatus.OK);
=======
    @DeleteMapping("/{scriptId}")
    public void deleteScript(@PathVariable Long scriptId, Principal principal){
        scriptService.deleteScriptById(scriptId, principal);
>>>>>>> dev
    }
}
