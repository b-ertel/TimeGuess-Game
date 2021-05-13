package at.timeguess.backend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Activity;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

/**
 * A collection of tests for the {@link CubeService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class CubeServiceTest {
    
    @Autowired
    private CubeService cubeService;
    
    /**
     * Test if all the cube faces required could be successfully loaded from the data.sql file
     * and have the correct values.
     */
    @ParameterizedTest
    @CsvSource({"1, 1, TALK",
        "1, 2, TALK",
        "1, 3, TALK",
        "1, 1, RHYME",
        "1, 2, RHYME",
        "1, 3, RHYME",
        "2, 1, DRAW",
        "2, 2, DRAW",
        "2, 3, DRAW",
        "1, 1, MIME",
        "1, 2, MIME",
        "1, 3, MIME"
        })
    public void testCubeFaceInitialization(Integer time, Integer points, Activity activity) {
        List<CubeFace> cubeFaces = cubeService.allCubeFaces();
        Assertions.assertEquals(12, cubeFaces.size(),
                "Total number of cube faces is different from 12.");
        Assertions.assertTrue(cubeFaces.stream().anyMatch(c ->  c.getTime() == time && c.getPoints() == points && c.getActivity() == activity),
                "The required cube face with time " + time + " and points " + points + " and activity " + activity + " could not be found.");
    }
        
    /**
     * Test if a new cube can be saved.
     */
    @Test
    @DirtiesContext
    public void testSaveNewCube() {
        Cube cube = new Cube();
        cube.setMacAddress("123");
        cube = cubeService.saveCube(cube);
        Assertions.assertNotNull(cube);
        Assertions.assertNotNull(cube.getId());
        Assertions.assertNotNull(cube.getMacAddress());
        Assertions.assertEquals("123", cube.getMacAddress());
        Assertions.assertNull(cube.getName());
    }

    /**
     * Test if it is impossible to save two different cubes with the same MAC address.
     */
    @Test
    @DirtiesContext
    public void testSaveNewCubeDuplicateMacAddress() {
        Cube cube1 = new Cube();
        cube1.setMacAddress("123");
        cubeService.saveCube(cube1);
        Cube cube2 = new Cube();
        cube2.setMacAddress("123");
        Assertions.assertThrows(IllegalArgumentException.class, () -> cubeService.saveCube(cube2),
                "It was possible to save two cubes with the same MAC address!");
    }

    /**
     * Test if an existing cube can be saved.
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testSaveExistingCube() {
        Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
        cube.setName("My funny name");
        cube = cubeService.saveCube(cube);
        Assertions.assertNotNull(cube);
        Assertions.assertNotNull(cube.getId());
        Assertions.assertNotNull(cube.getMacAddress());
        Assertions.assertEquals("56:23:89:34:56", cube.getMacAddress());
        Assertions.assertNotNull(cube.getName());
        Assertions.assertEquals("My funny name", cube.getName());
    }
    
    /**
     * Test if all cubes from the data.sql have been loaded.
     */
    @Test
    public void testCubeInitialization() {
        List<Cube> cubes = cubeService.allCubes();
        Assertions.assertEquals(3, cubes.size(),
                "The number of cubes loaded is different from 3.");
    }
    
    /**
     * Test if the methods to find cubes by their MAC address work as expected.
     */
    @Test
    public void testFindByMacAddress() {
        // good
        String goodMacAddress = "56:23:89:34:56";
        Assertions.assertTrue(cubeService.isMacAddressKnown(goodMacAddress));
        Cube cube = cubeService.getByMacAddress(goodMacAddress);
        Assertions.assertNotNull(cube);
        Assertions.assertNotNull(cube.getId());
        Assertions.assertNotNull(cube.getMacAddress());
        Assertions.assertEquals(goodMacAddress, cube.getMacAddress());
        // bad
        String badMacAddress = "123";
        Assertions.assertFalse(cubeService.isMacAddressKnown(badMacAddress));
        Assertions.assertNull(cubeService.getByMacAddress(badMacAddress));
    }
    
    /**
     * Test if a cube can be deleted.
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteCube() {
        Cube cube = cubeService.getByMacAddress("56:00:89:44:56");
        cubeService.deleteCube(cube);
        Assertions.assertFalse(cubeService.isMacAddressKnown("56:00:89:44:56"),
                "Deleted cube is still present.");
    }
    
    /**
     * Test if existing configurations for a cube can be detected correctly.
     */
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testIsConfigured() {
        Cube configuredCube = cubeService.getByMacAddress("56:23:89:34:56");
        Assertions.assertTrue(cubeService.isConfigured(configuredCube),
                "Configured cube was reported to be unconfigured.");
        Cube unconfiguredCube = cubeService.getByMacAddress("56:00:89:44:56");
        Assertions.assertFalse(cubeService.isConfigured(unconfiguredCube),
                "Unconfigured cube was reported to be configured.");
    }
    
    /**
     * Test if all configurations for a cube can be deleted successfully.
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteConfigurations() {
        Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
        cubeService.deleteConfigurations(cube);
        Assertions.assertFalse(cubeService.isConfigured(cube),
                "Deleted configurations are still present.");
    }
    
    /**
     * Test if a new set of configurations can be saved.
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testSaveMappingForCube() {
        Cube cube = cubeService.getByMacAddress("56:00:89:44:56");
        Map<CubeFace, Integer> mapping = new HashMap<>();
        for (CubeFace cubeFace : cubeService.allCubeFaces()) {
            mapping.put(cubeFace, (int) (Math.random() * 12));
        }
        cubeService.saveMappingForCube(cube, mapping);
        Assertions.assertTrue(cubeService.isConfigured(cube),
                "Cube is still reported as unconfigured after saving a new mapping.");
    }
    
    /**
     * Test if it is impossible to save a new set of configurations for
     * a cube that already has a saved configuration.
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testSaveMappingForCubeExistingConfiguration() {
        Cube cube = cubeService.getByMacAddress("56:23:89:34:56");
        Map<CubeFace, Integer> mapping = new HashMap<>();
        for (CubeFace cubeFace : cubeService.allCubeFaces()) {
            mapping.put(cubeFace, (int) (Math.random() * 12));
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> cubeService.saveMappingForCube(cube, mapping),
                "It was possible save a mapping for a cube that already has a saved configuration!");
    }
    
    /**
     * Test if mapped cube faces are retrieved as expected. 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetMappedCubeFace() {
        Cube cube = cubeService.getByMacAddress("56:00:89:44:56");
        Map<CubeFace, Integer> mapping = new HashMap<>();
        List<CubeFace> cubeFaces = cubeService.allCubeFaces();
        CubeFace cubeFace1 = cubeFaces.get(0);
        CubeFace cubeFace2 = cubeFaces.get(1);
        CubeFace cubeFace3 = cubeFaces.get(2);
        mapping.put(cubeFace1, 42);
        mapping.put(cubeFace2, 43);
        mapping.put(cubeFace3, 43);
        cubeService.saveMappingForCube(cube, mapping);
        Assertions.assertTrue(cubeService.isConfigured(cube),
                "Cube is still reported as unconfigured after saving a new mapping.");
        Assertions.assertNotNull(cubeService.getMappedCubeFace(cube, 42));
        Assertions.assertEquals(cubeFace1, cubeService.getMappedCubeFace(cube, 42));
        Assertions.assertNull(cubeService.getMappedCubeFace(cube, 43));
        Assertions.assertNull(cubeService.getMappedCubeFace(cube, 44));
    }
    
}
