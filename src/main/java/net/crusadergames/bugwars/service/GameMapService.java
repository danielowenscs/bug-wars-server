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

    public GameMap getGameMapById(Long gameMapId) throws Exception{
        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);
        throwMapNotFound(optionalGameMap);
        GameMap gameMap = optionalGameMap.get();
        return gameMap;
    }

    public GameMap createNewGameMap(Principal principal, GameMapRequest gameMapRequest) throws Exception {
        if (!isAdmin(principal.getName())) {
            throw new NotAnAdminException();
        }

        if (gameMapRequest.getName().isBlank() || gameMapRequest.getBody().isBlank()) {
            throw new MapNameOrBodyBlankException();
        }

        if (mapNameAlreadyExists(gameMapRequest.getName())) {
            throw new MapNameAlreadyExistsException();
        }

        GameMap gameMap = new GameMap(null, gameMapRequest.getName(), gameMapRequest.getHeight(), gameMapRequest.getWidth(), gameMapRequest.getBody());
        gameMap = gameMapRepository.save(gameMap);
        return gameMap;
    }

    public GameMap updateMap(Principal principal, GameMapRequest gameMapRequest, Long gameMapId) throws Exception{
        if (!isAdmin(principal.getName())) {
            throw new NotAnAdminException();
        }

        if (gameMapRequest.getName().isBlank() || gameMapRequest.getBody().isBlank()) {
            throw new MapNameOrBodyBlankException();
        }

        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);

        throwMapNotFound(optionalGameMap);

        GameMap newGameMap = new GameMap(gameMapId, gameMapRequest.getName(), gameMapRequest.getHeight(), gameMapRequest.getWidth(), gameMapRequest.getBody());
        if (mapNameAlreadyExists(newGameMap.getName())) {
            throw new MapNameAlreadyExistsException();
        }
        gameMapRepository.save(newGameMap);

        return newGameMap;
    }

    public String deleteGameMapById(Long gameMapId, Principal principal) throws Exception{
        if (!isAdmin(principal.getName())) {
            throw new NotAnAdminException();
        }
        Optional<GameMap> optionalGameMap = gameMapRepository.findById(gameMapId);
        throwMapNotFound(optionalGameMap);

        gameMapRepository.deleteById(gameMapId);
        return ("Game map successfully deleted");
    }

    public boolean isAdmin(String username) {
        boolean hasAdminRole = false;
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User adminCheckUser = user.get();
            for (Role role : adminCheckUser.getRoles()) {
                if (role.getName().equals(ERole.ROLE_ADMIN)) {
                    hasAdminRole = true;
                    break;
                }
            }
        }
        return hasAdminRole;
    }

    public boolean mapNameAlreadyExists(String gameMapName) {
        Optional<GameMap> optionalGameMap = gameMapRepository.findByNameIgnoreCase(gameMapName);
        if (optionalGameMap.isPresent()) {
            return true;
        }
        return false;
    }

    private void throwMapNotFound(Optional<GameMap> gameMap) throws Exception {
        if (gameMap.isEmpty()) {
            throw new NameNotFoundException("Map name not found");
        }
    }


}
