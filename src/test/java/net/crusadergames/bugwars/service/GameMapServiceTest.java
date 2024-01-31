package net.crusadergames.bugwars.service;

import lombok.SneakyThrows;
import net.crusadergames.bugwars.dto.request.GameMapRequest;
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
import org.testcontainers.shaded.org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
            GameMapRequest gmRequest = new GameMapRequest("Map 1",5L,5L,"11111\n10001\n10101\n11011\n11111");
            when(userRepository.findByUsername(any())).thenReturn(Optional.of(USER));
            GameMap createdMap = gameMapService.createNewGameMap(mockPrincipal,gmRequest);
        });


    }
    @Test
    void createNewGameMap_ShouldThrowExceptionIfMapNameExists() {
    }
    @Test
    void createNewGameMap_ShouldThrowExceptionIfTitleOrBodyIsBlank() {
    }

    @Test
    void updateMap() {
    }

    @Test
    void deleteGameMapById() {
    }

    @Test
    void isAdmin() {
    }

    @Test
    void mapNameAlreadyExists() {
    }
}