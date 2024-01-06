package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
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

@CrossOrigin
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
    public ResponseEntity<Script> postScript(@RequestBody ScriptRequest scriptRequest, Principal principal) {
        Script script = scriptService.createNewScript(principal, scriptRequest);
        if(script == null){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(script, HttpStatus.CREATED);
    }

    @DeleteMapping("/{scriptId}")
    public void deleteScript(@PathVariable Long scriptId, Principal principal){
        scriptService.deleteScriptById(scriptId, principal);
    }

    @GetMapping()
    public List<Script> getUserScripts(Principal principal){
        return scriptService.getAllScriptsByUser(principal);
    }

    @PutMapping("/{scriptId}")
    public ResponseEntity<Script> updateScript(@RequestBody ScriptRequest scriptRequest, Principal principal, @PathVariable Long scriptId) {
        Script script = scriptService.updateOldScript(principal, scriptRequest, scriptId);
        if(script == null){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(script, HttpStatus.CREATED);
    }
}
