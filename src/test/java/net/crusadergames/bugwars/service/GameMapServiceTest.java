package net.crusadergames.bugwars.service;

import lombok.SneakyThrows;
import net.crusadergames.bugwars.dto.request.GameMapRequest;
import net.crusadergames.bugwars.exceptions.MapNameAlreadyExistsException;
import net.crusadergames.bugwars.exceptions.MapNameOrBodyBlankException;
import net.crusadergames.bugwars.exceptions.NotAnAdminException;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.GameMapRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameMapServiceTest {

    private final User USER = new User(1L,"user", "gmail@email.com", "passing", Set.of(new Role(1, ERole.ROLE_USER)));
    private final User ADMIN = new User(2L,"admin", "gmail@email.com", "passing",Set.of(new Role(3,
            ERole.ROLE_ADMIN)));

    private final GameMap MAP_1 = new GameMap(1L,"Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");
    private final GameMap MAP_2 = new GameMap(2L,"Map 2",7L,7L,"1111111\n1000001\n1000001\n1000001\n1000001" +
                                                                            "\n1000001\n1111111");
    private final GameMap OLD_MAP = new GameMap(1L,"Old Map",5L,5L,"11111\n10001\n10101\n11011\n11111");
    private final GameMap NEW_MAP = new GameMap(1L,"I Am New",2L,2L,"11\n11");

    private GameMapService gameMapService;
    private GameMapRepository gameMapRepository;
    private UserRepository userRepository;
    private Principal mockPrincipal;
    private List<GameMap> allMaps = new ArrayList<>();

    @BeforeEach
    public void beforeEachTest(){

        gameMapRepository=Mockito.mock(GameMapRepository.class);;
        userRepository=Mockito.mock(UserRepository.class);
        mockPrincipal=Mockito.mock(Principal.class);
        gameMapService = new GameMapService(gameMapRepository,userRepository);
        allMaps.add(MAP_1);
        allMaps.add(MAP_2);
    }

    @Test
    void getAllGameMaps_shouldReturnAllGameMaps() {
       GameMap map1 = new GameMap(1L,"Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");
       GameMap map2 = new GameMap(2L,"Map 2",7L,7L,"1111111\n1000001\n1000001\n1000001\n1000001" +"\n1000001\n1111111");
       List<GameMap> expected = new ArrayList<>();
       expected.add(map1);
       expected.add(map2);
       when(gameMapRepository.findAll()).thenReturn(allMaps);
       List<GameMap> maps = gameMapService.getAllGameMaps();

       Assert.assertEquals(expected,maps);

    }

    @Test
    void getGameMapById_ReturnsCorrectMap() throws Exception {
        GameMap expected = new GameMap(1L,"Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");

        when(gameMapRepository.findById(any())).thenReturn(Optional.of(MAP_1));

        GameMap actual = gameMapService.getGameMapById(MAP_1.getId());

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getId(),actual.getId());
        Assert.assertEquals(expected,actual);

    }
    @Test
    void getGameMapById_ThrowsExceptionIfMapDoesNotExist() throws Exception {
       Assert.assertThrows(Exception.class,()->{
           when(gameMapRepository.findById(any())).thenReturn(Optional.empty());
           GameMap actual = gameMapService.getGameMapById(MAP_1.getId());
       });

    }

    @SneakyThrows
    @Test
    void createNewGameMap_ShouldReturnANewMap() {
        GameMapRequest gmRequest = new GameMapRequest("Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
        when(gameMapRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(gameMapRepository.save(Mockito.any(GameMap.class))).thenReturn(MAP_1);
        //act
        GameMap createdMap = gameMapService.createNewGameMap(mockPrincipal,gmRequest);

        Assert.assertNotNull (createdMap);
        Assert.assertEquals(createdMap.getId(),MAP_1.getId());
        Assert.assertEquals(createdMap,MAP_1);
    }

    @Test
    void createNewGameMap_ShouldThrowExceptionIfUserIsNotAdmin() {
        Assert.assertThrows(NotAnAdminException.class,()->{
            GameMapRequest gmRequest = new GameMapRequest("Map 1",5L,5L,"11\n11");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
            GameMap createdMap = gameMapService.createNewGameMap(mockPrincipal,gmRequest);
        });
    }
    @Test
    void createNewGameMap_ShouldThrowExceptionIfMapNameExists() {
        Assert.assertThrows(MapNameAlreadyExistsException.class,()->{
            GameMapRequest gmRequest = new GameMapRequest("Map 1",2L,2L,"11\n11");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
            when(gameMapRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(MAP_1));
            GameMap createdMap = gameMapService.createNewGameMap(mockPrincipal,gmRequest);
        });
    }
    @Test
    void createNewGameMap_ShouldThrowExceptionIfTitleOrBodyIsBlank() {
        Assert.assertThrows(MapNameOrBodyBlankException.class,()->{
            GameMapRequest gmRequest = new GameMapRequest("",2L,2L,"");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
            GameMap createdMap = gameMapService.createNewGameMap(mockPrincipal,gmRequest);
        });
    }

    @SneakyThrows
    @Test
    void updateMap_ShouldReturnNewMap() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(ADMIN));
        when(gameMapRepository.findById(any())).thenReturn(Optional.of(OLD_MAP));
        GameMapRequest oldRequest = new GameMapRequest("Old Map",5L,5L,"11111\n10001\n10101\n11011\n11111");
        GameMapRequest newRequest = new GameMapRequest("I Am New",2L,2L,"11\n11");
        gameMapService.createNewGameMap(mockPrincipal,oldRequest);

        GameMap newMap = gameMapService.updateMap(mockPrincipal, newRequest, 1L);

        Assert.assertNotNull (newMap);
        Assert.assertEquals(newMap.getId(), NEW_MAP.getId());
        Assert.assertEquals(newMap, NEW_MAP);
    }
    @Test
    void updateMap_ShouldThrowExceptionIfUserIsNotAdmin() {
        GameMap oldMap = MAP_1;

        Assert.assertThrows(NotAnAdminException.class,()->{
            GameMapRequest newRequest = new GameMapRequest("Map 1",2L,2L,"11\n11");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
            gameMapService.updateMap(mockPrincipal,newRequest, oldMap.getId());
        });
    }
    @Test
    void updateMap_ShouldThrowExceptionIfMapNameExists() {
        GameMap oldMap = OLD_MAP;
        Assert.assertThrows(MapNameAlreadyExistsException.class,()->{
            GameMapRequest newRequest = new GameMapRequest("Map 2",2L,2L,"11\n11");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
            when(gameMapRepository.findById(any())).thenReturn(Optional.of(oldMap));
            when(gameMapRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(MAP_2));
            gameMapService.updateMap(mockPrincipal,newRequest,oldMap.getId());
        });
    }
    @Test
    void updateMap_ShouldThrowExceptionIfTitleOrBodyIsBlank() {
        GameMap oldMap = OLD_MAP;
        Assert.assertThrows(MapNameOrBodyBlankException.class,()->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
            when(gameMapRepository.findById(any())).thenReturn(Optional.of(oldMap));
            GameMapRequest newRequest = new GameMapRequest("",2L,2L,"");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(ADMIN));
            gameMapService.updateMap(mockPrincipal,newRequest,oldMap.getId());
        });
    }

    @SneakyThrows
    @Test
    void deleteGameMapById_ShouldDeleteCorrectMap() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(ADMIN));
        when(gameMapRepository.findById(any())).thenReturn(Optional.of(MAP_1));

        gameMapService.deleteGameMapById(1L,mockPrincipal);

        verify(gameMapRepository, times(1)).deleteById(1L);
    }
    @Test
    void deleteMap_ShouldThrowExceptionIfNotAdmin() {
        Assert.assertThrows(NotAnAdminException.class,()->{
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
            gameMapService.deleteGameMapById(1L,mockPrincipal);

        });

    }

    @Test
    void isAdmin_ShouldReturnTrueWhenUserHasAdminRole() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(ADMIN));
        boolean actual = gameMapService.isAdmin(ADMIN.getUsername());
        Assert.assertTrue(actual);
    }

    @Test
    void isAdmin_ShouldReturnFalseWhenUserDoesNotHaveAdminRole() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(USER));
        boolean actual = gameMapService.isAdmin(ADMIN.getUsername());
        Assert.assertFalse(actual);

    }

    @Test
    void mapNameAlreadyExists_ShouldReturnTrueWhenGameMapExistsWithSameName() {
        when(gameMapRepository.findByNameIgnoreCase(any())).thenReturn(Optional.ofNullable(MAP_1));

        boolean actual = gameMapService.mapNameAlreadyExists("Map 1");
        Assert.assertTrue(actual);

    }

    @Test
    void mapNameAlreadyExists_ShouldReturnFalseWhenGameMapWithSameNameDoesNotExist() {
        when(gameMapRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());

        boolean actual = gameMapService.mapNameAlreadyExists("Map 100");
        Assert.assertFalse(actual);
    }



}