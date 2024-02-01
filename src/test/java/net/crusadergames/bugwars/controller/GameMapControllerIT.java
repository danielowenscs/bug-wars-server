package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.GameMapRequest;
import net.crusadergames.bugwars.exceptions.MapNameAlreadyExistsException;
import net.crusadergames.bugwars.exceptions.MapNameOrBodyBlankException;
import net.crusadergames.bugwars.exceptions.NotAnAdminException;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.service.GameMapService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameMapControllerIT {

    private final User USER = new User(1L,"user", "gmail@email.com", "passing", Set.of(new Role(1, ERole.ROLE_USER)));
    private final User ADMIN = new User(2L,"admin", "gmail@email.com", "passing",Set.of(new Role(3,
            ERole.ROLE_ADMIN)));

    private final GameMap MAP_1 = new GameMap(1L,"Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");
    private final GameMap MAP_2 = new GameMap(2L,"Map 2",7L,7L,"1111111\n1000001\n1000001\n1000001\n1000001" +
            "\n1000001\n1111111");
    private final GameMap OLD_MAP = new GameMap(1L,"Old Map",5L,5L,"11111\n10001\n10101\n11011\n11111");
    private final GameMap NEW_MAP = new GameMap(1L,"I Am New",2L,2L,"11\n11");

    private GameMapService gameMapService;
    private GameMapController gameMapController;
    private Principal mockPrincipal;
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEachTest() {
        gameMapService = Mockito.mock(GameMapService.class);
        gameMapController = new GameMapController(gameMapService);
        mockPrincipal = Mockito.mock(Principal.class);
        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    public void getAllGameMapsShouldReturnAllMaps() {
        List<GameMap> expectedGameMaps = new ArrayList<>();
        expectedGameMaps.add(MAP_1);
        expectedGameMaps.add(MAP_2);
        when(gameMapService.getAllGameMaps()).thenReturn(expectedGameMaps);

        List<GameMap> listOfGameMaps = gameMapController.getAllGameMaps();

        Assert.assertEquals(expectedGameMaps, listOfGameMaps);
    }

    @Test
    public void getGameMapByIdShouldReturnCorrectGameMap() throws Exception{
        when(gameMapService.getGameMapById(1L)).thenReturn(MAP_1);

        GameMap retrievedMap = gameMapController.getGameMapById(1L);

        Assert.assertEquals(MAP_1, retrievedMap);
    }

    @Test
    public void getGameMapByIdShouldThrowExceptionIfMapDoesNotExist() throws Exception{
        when(gameMapController.getGameMapById(any())).thenThrow(new Exception("Map name not found"));
        Assert.assertThrows(Exception.class, () -> gameMapController.getGameMapById(3L));
    }

    @Test
    public void postGameMapShouldReturnCreatedMap() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(gameMapService.createNewGameMap(mockPrincipal, request)).thenReturn(NEW_MAP);

        ResponseEntity<GameMap> createdGameMap = gameMapController.postGameMap(request, mockPrincipal);

        Assert.assertEquals(NEW_MAP, createdGameMap.getBody());
        Assert.assertEquals(HttpStatus.CREATED, createdGameMap.getStatusCode());
    }

    @Test
    public void postGameMapShouldThrowExceptionIfNotAdmin() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
        when(gameMapController.postGameMap(any(), any())).thenThrow(new NotAnAdminException());
        Assert.assertThrows(NotAnAdminException.class, () -> gameMapController.postGameMap(request, mockPrincipal));
    }

    @Test
    public void postGameMapShouldThrowExceptionIfMapNameAlreadyExists() throws Exception {
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(gameMapController.postGameMap(request, mockPrincipal)).thenThrow(new MapNameAlreadyExistsException());
        Assert.assertThrows(MapNameAlreadyExistsException.class, () -> gameMapController.postGameMap(request, mockPrincipal));
    }

    @Test
    public void postGameMapShouldThrowExceptionIfTitleOrBodyIsEmpty() throws Exception {
        GameMapRequest requestEmptyTitle = new GameMapRequest("",2L,2L,"11\n11");
        GameMapRequest requestEmptyBody = new GameMapRequest("I Am New",2L,2L,"");
        when(gameMapController.postGameMap(requestEmptyTitle, mockPrincipal)).thenThrow(new MapNameOrBodyBlankException());
        when(gameMapController.postGameMap(requestEmptyBody, mockPrincipal)).thenThrow(new MapNameOrBodyBlankException());
        Assert.assertThrows(MapNameOrBodyBlankException.class, () -> gameMapController.postGameMap(requestEmptyTitle, mockPrincipal));
        Assert.assertThrows(MapNameOrBodyBlankException.class, () -> gameMapController.postGameMap(requestEmptyBody, mockPrincipal));
    }

    @Test
    public void updateGameMapShouldReturnUpdatedGameMap() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(gameMapService.updateMap(mockPrincipal, request, 1L)).thenReturn(NEW_MAP);

        ResponseEntity<GameMap> updatedGameMap = gameMapController.updateGameMap(request, mockPrincipal, 1L);

        Assert.assertEquals(NEW_MAP, updatedGameMap.getBody());
        Assert.assertEquals(HttpStatus.ACCEPTED, updatedGameMap.getStatusCode());
    }

    @Test
    public void updateGameMapShouldThrowExceptionIfNotAdmin() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
        when(gameMapService.updateMap(mockPrincipal, request, 1L)).thenThrow(new NotAnAdminException());
        Assert.assertThrows(NotAnAdminException.class, () -> gameMapController.updateGameMap(request, mockPrincipal, 1L));
    }

    @Test
    public void updateGameMapShouldThrowExceptionIfTitleOrBodyIsEmpty() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(gameMapService.updateMap(mockPrincipal, request, 1L)).thenThrow(new MapNameOrBodyBlankException());
        Assert.assertThrows(MapNameOrBodyBlankException.class, () -> gameMapController.updateGameMap(request, mockPrincipal, 1L));
    }

    @Test
    public void updateGameMapShouldThrowExceptionIfMapNameAlreadyExists() throws Exception{
        GameMapRequest request = new GameMapRequest("I Am New",2L,2L,"11\n11");
        when(gameMapService.updateMap(mockPrincipal, request, 1L)).thenThrow(new MapNameAlreadyExistsException());
        Assert.assertThrows(MapNameAlreadyExistsException.class, () -> gameMapController.updateGameMap(request, mockPrincipal, 1L));
    }

    @Test
    public void deleteGameMapShouldReturnDeletedGameMapDeletedScriptMessage() throws Exception{
        when(gameMapService.deleteGameMapById(1L, mockPrincipal)).thenReturn("Game map successfully deleted");

        ResponseEntity<String> deletedGameMap = gameMapController.deleteGameMap(1L, mockPrincipal);

        Assert.assertEquals("Game map successfully deleted", deletedGameMap.getBody());
        Assert.assertEquals(HttpStatus.OK, deletedGameMap.getStatusCode());
    }

    @Test
    public void deleteGameMapShouldThrowExceptionIfNotAdmin() throws Exception{
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
        when(gameMapService.deleteGameMapById(1L, mockPrincipal)).thenThrow(new NotAnAdminException());
        Assert.assertThrows(NotAnAdminException.class, () -> gameMapController.deleteGameMap(1L, mockPrincipal));
    }

}
