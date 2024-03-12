package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameMapRequest;
import net.crusadergames.bugwars.exceptions.*;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.GameMapRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GameMapService {

    @Autowired
    GameMapRepository gameMapRepository;

    @Autowired
    UserRepository userRepository;

    public GameMapService(GameMapRepository gameMapRepository, UserRepository userRepository) {
        this.gameMapRepository = gameMapRepository;
        this.userRepository = userRepository;
    }

    public List<GameMap> getAllGameMaps() {
        List<GameMap> mapList = gameMapRepository.findAll();
        return mapList;
    }

    public GameMap getGameMapById(Long gameMapId) throws Exception {
        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);
        throwMapNotFound(optionalGameMap);
        GameMap gameMap = optionalGameMap.get();
        return gameMap;
    }

    public GameMap createNewGameMap(GameMapRequest gameMapRequest) throws Exception {
        throwMapNameOrBodyBlank(gameMapRequest);

        mapNameAlreadyExists(gameMapRequest.getName());

        GameMap gameMap = new GameMap(null, gameMapRequest.getName(), gameMapRequest.getTerrainId(), gameMapRequest.getHeight(), gameMapRequest.getWidth(), gameMapRequest.getBody(), gameMapRequest.getImagePath());
        gameMap = gameMapRepository.save(gameMap);
        return gameMap;
    }

    public GameMap updateMap(Long gameMapId, GameMapRequest gameMapRequest) throws Exception {
        throwMapNameOrBodyBlank(gameMapRequest);

        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);
        throwMapNotFound(optionalGameMap);

        mapNameAlreadyExists(gameMapRequest.getName());

        GameMap newGameMap = new GameMap(gameMapId, gameMapRequest.getName(), gameMapRequest.getTerrainId(), gameMapRequest.getHeight(), gameMapRequest.getWidth(), gameMapRequest.getBody(), gameMapRequest.getImagePath());
        gameMapRepository.save(newGameMap);

        return newGameMap;
    }

    public String deleteGameMapById(Long gameMapId) throws Exception{
        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);
        throwMapNotFound(optionalGameMap);

        gameMapRepository.deleteById(gameMapId);
        return ("Game map successfully deleted");
    }

    public void mapNameAlreadyExists(String gameMapName) throws Exception{
        Optional<GameMap> optionalGameMap = gameMapRepository.findByNameIgnoreCase(gameMapName);
        if (optionalGameMap.isPresent()) {
            throw new MapNameAlreadyExistsException();
        }
    }

    public void throwMapNameOrBodyBlank(GameMapRequest gameMapRequest) throws Exception {
        if (gameMapRequest.getName().isBlank() || gameMapRequest.getBody().isBlank()) {
            throw new MapNameOrBodyBlankException();
        }
    }

    private void throwMapNotFound(Optional<GameMap> gameMap) throws Exception {
        if (gameMap.isEmpty()) {
            throw new NameNotFoundException("Map not found");
        }
    }


}
