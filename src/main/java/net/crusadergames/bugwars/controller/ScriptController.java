package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.dto.response.ScriptResponse;
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
@RequestMapping("/api/scripts")
@PreAuthorize("isAuthenticated()")
public class ScriptController {
    @Autowired
    ScriptService scriptService;

    @Autowired
    UserRepository userRepository;



    @PostMapping()
    public ResponseEntity<ScriptResponse> postScript(@RequestBody ScriptRequest scriptRequest, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        Script script = scriptService.createNewScript(user.get().getId(), scriptRequest);

        if(user == null || script == null){
            throw new IllegalArgumentException("User of script cannot be null");
        }
        ScriptResponse responseDTO = new ScriptResponse(script.getScript_id(),script.getScript_name(),script.getBody(),
                script.getDate_created(),script.getDate_Updated(),script.getUser().getId());
        return new ResponseEntity<ScriptResponse>(responseDTO, HttpStatus.OK);
    }



}
