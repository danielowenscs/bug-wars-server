package net.crusadergames.bugwars.controller;

import lombok.RequiredArgsConstructor;
import net.crusadergames.bugwars.dto.request.GameMapRequest;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.service.GameMapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class GameMapController {

    private final GameMapService gameMapService;

    @GetMapping()
    public List<GameMap> getAllGameMaps() {
        return gameMapService.getAllGameMaps();
    }

    @GetMapping("/{gameMapId}")
    public GameMap getGameMapById(@PathVariable Long gameMapId) throws Exception {
        return gameMapService.getGameMapById(gameMapId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<GameMap> postGameMap(@RequestBody GameMapRequest gameMapRequest, Principal principal) throws Exception{
        GameMap gameMap = gameMapService.createNewGameMap(principal, gameMapRequest);
        return new ResponseEntity<>(gameMap, HttpStatus.CREATED);
    }

    @PutMapping("/{gameMapId}")
    public ResponseEntity<GameMap> updateGameMap(@RequestBody GameMapRequest gameMapRequest, Principal principal, @PathVariable Long gameMapId) throws Exception{
        GameMap gameMap = gameMapService.updateMap(principal, gameMapRequest, gameMapId);
        return new ResponseEntity<>(gameMap, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{gameMapId}")
    public ResponseEntity<String> deleteGameMap(@PathVariable Long gameMapId, Principal principal) throws Exception {
        String response = gameMapService.deleteGameMapById(gameMapId, principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
