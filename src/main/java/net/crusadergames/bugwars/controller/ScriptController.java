package net.crusadergames.bugwars.dto.request.controller;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/script")
@PreAuthorize("isAuthenticated()")
public class ScriptController {

    @Autowired
    ScriptService scriptService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/post")
    public ResponseEntity<Script> postScript(@RequestBody ScriptRequest scriptResponse, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Script script = scriptService.createNewScript(user.get().getId(), scriptResponse);
        return new ResponseEntity<>(script, HttpStatus.OK);
    }

}
