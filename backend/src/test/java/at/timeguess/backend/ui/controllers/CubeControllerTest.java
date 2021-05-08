package at.timeguess.backend.ui.controllers;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.CubeRepository;

@SpringBootTest
@WebAppConfiguration
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
public class CubeControllerTest {
	
	@Autowired
	private CubeController cubeController;
	@Autowired
	private CubeStatusController cubeStatusController;
	@Autowired
	private CubeRepository cubeRepo;
	@Autowired
	private ConfigurationRepository configRepo;
	
	private Cube getNewCube() {
		
		Cube newCube = new Cube();
		newCube.setId(144L);
		newCube.setMacAddress("44:22:67:43:55");
		newCube.setName("Garfield");
		
		return newCube;
	}
	
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetCube() {
		
		Cube newCube = getNewCube();
		cubeController.setCube(newCube);
		Assertions.assertEquals(newCube.getId(), cubeController.getCube().getId());
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSaveCube() {
		
		StatusMessage statusMessage = new StatusMessage();
		
		statusMessage.setIdentifier("55:88:33:67:45"); 
		statusMessage.setBatteryLevel(50);
		statusMessage.setCalibrationVersion(0);
		statusMessage.setRssi(40);
		
		Cube newCube = cubeStatusController.updateCube(statusMessage);

		newCube.setName("Garfield");
		cubeController.setCube(newCube);
		cubeController.saveCube();
		
		Assertions.assertEquals(newCube.getName(), cubeController.getCube().getName());		
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testGetAllCubes() {
		
		// we have 3 cubes in the database
		Assertions.assertEquals(3, cubeController.getAllCubes().size());
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testIsMacAddressKnown() {
		
		Cube newCube = getNewCube();
		Assertions.assertFalse(cubeController.isMacAddressKnown(newCube));
		
		// save cube in database
		cubeRepo.save(newCube);
		Assertions.assertTrue(cubeController.isMacAddressKnown(newCube));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testDeleteCube() {
		
		// put cube with id 144 in database
		Cube cube = getNewCube();
		cubeRepo.save(cube);
		
		// delete with controller
		cubeController.setCube(cube);
		cubeController.deleteCube();
		
		Assertions.assertThrows(NoSuchElementException.class, () -> cubeRepo.findById(144L).get());
	}

	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testDeleteConfiguration() {
		
		// we have a configuration for cube with id 100
		Cube configuredCube = cubeRepo.findById(100L).get();
		cubeController.setCube(configuredCube);
		cubeController.deleteConfigurations();
		
		Assertions.assertEquals(0, configRepo.findByCube(configuredCube).size());
	}
}
