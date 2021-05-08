package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.IntervalType;
import at.timeguess.backend.model.api.StatusMessage;

import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.CubeRepository;
import at.timeguess.backend.services.CubeService;

@SpringBootTest
@WebAppConfiguration
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
public class CubeStatusControllerTest {
	
	@Autowired
	private CubeStatusController cubeStatusController;
	@Autowired
	private CubeService cubeService;
	@Autowired
	private CubeRepository cubeRepo;	
	@Autowired
	private ConfigurationRepository configRepo;
	
	/**
	 * @return a cube with status LIVE
	 */
	private Cube produceLiveCube() {
		
		Cube liveCube = cubeRepo.findById(102L).get();
		
		StatusMessage statusMessage = new StatusMessage();
		
		statusMessage.setIdentifier(liveCube.getMacAddress()); 
		statusMessage.setBatteryLevel(50);
		statusMessage.setCalibrationVersion(0);
		statusMessage.setRssi(40);
		
		cubeStatusController.processStatus(statusMessage);
		return liveCube;
	}
	
	/**
	 * @return a cube with status READY
	 */
	private Cube produceReadyCube() {
		
		Cube readyCube = cubeRepo.findById(100L).get();

		StatusMessage statusMessage = new StatusMessage();
		
		statusMessage.setIdentifier(readyCube.getMacAddress()); 
		statusMessage.setBatteryLevel(50);
		statusMessage.setCalibrationVersion(1);
		statusMessage.setRssi(40);
		
		cubeStatusController.processStatus(statusMessage);
		return readyCube;
	}
	
	/**
	 * @return a cube with status OFFLINE
	 */
	private Cube produceOfflineCube() {
		
		Cube offlineCube = cubeRepo.findById(101L).get();
		return offlineCube;
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSetupCubeStatus() {
				
		List<Cube> allCubes = cubeRepo.findAll();
		
		Assertions.assertEquals(allCubes.size(), cubeStatusController.getCubeStatusInfos().size());
		Assertions.assertEquals(3, allCubes.size());
		Assertions.assertEquals(3, cubeStatusController.getCubeStatusInfos().size());
	}
    
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testProcessStatus() {
    		
		// produce READY cube
		Cube readyCube = produceReadyCube();
		
		Assertions.assertEquals(1, cubeStatusController.getHealthStatus().size());
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		// produce LIVE cube
		Cube liveCube = produceLiveCube();
		
		Assertions.assertEquals(2, cubeStatusController.getHealthStatus().size());
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(liveCube.getMacAddress()));
		 
		// status for offline cube
		Cube offlineCube = produceOfflineCube();
		Assertions.assertEquals(CubeStatus.OFFLINE, cubeStatusController.getStatus(offlineCube.getMacAddress()));  
		
		// get a second status message from cube 100
		StatusMessage statusMessage = new StatusMessage();
		statusMessage.setIdentifier(readyCube.getMacAddress());
		statusMessage.setBatteryLevel(5);
		statusMessage.setCalibrationVersion(1);
		
		cubeStatusController.processStatus(statusMessage);
		Assertions.assertEquals(5, cubeStatusController.getHealthStatus().get(statusMessage.getIdentifier()).getBatteryLevel());
		
    }
    
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUpdateCube() {
    	
		// unknown cube is onboarding
    	StatusMessage statusMessage = new StatusMessage();
    	
		statusMessage.setIdentifier("78:12:45:77:99"); 
		statusMessage.setBatteryLevel(50);
		statusMessage.setCalibrationVersion(1);
		statusMessage.setRssi(40);
    	
    	cubeStatusController.processStatus(statusMessage);
    	// cube should be in database
    	Assertions.assertEquals("78:12:45:77:99", cubeRepo.findByMacAddress("78:12:45:77:99").getMacAddress());
    	//cube should have a health status
    	Assertions.assertEquals(50, cubeStatusController.getHealthStatus().get("78:12:45:77:99").getBatteryLevel());
    	
    }
    
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetOffline() { 
		
		// produce READY cube
		Cube readyCube = produceReadyCube();
		
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		cubeStatusController.setOffline(readyCube.getMacAddress());
		
		//status should now be OFFLINE
		Assertions.assertEquals(CubeStatus.OFFLINE, cubeStatusController.getStatus(readyCube.getMacAddress()));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetInConfig() {

		// produce LIVE cube
		Cube liveCube = produceLiveCube();
		
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(liveCube.getMacAddress()));
		
		cubeStatusController.setInConfig(liveCube.getMacAddress());
		
		//status should now be IN_CONFIG
		Assertions.assertEquals(CubeStatus.IN_CONFIG, cubeStatusController.getStatus(liveCube.getMacAddress()));
		
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetInGame() {
		
		// produce READY cube
		Cube readyCube = produceReadyCube();
		
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		cubeStatusController.setInGame(readyCube.getMacAddress());
		
		//status should now be IN_GAME
		Assertions.assertEquals(CubeStatus.IN_GAME, cubeStatusController.getStatus(readyCube.getMacAddress()));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetReady() {

		// produce LIVE cube
		Cube liveCube = produceLiveCube();
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(liveCube.getMacAddress()));
		
		cubeStatusController.setReady(liveCube.getMacAddress());
		
		//status should now be READY
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(liveCube.getMacAddress()));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetLive() {

		// produce READY cube
		Cube readyCube = produceReadyCube();
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		cubeStatusController.setLive(readyCube.getMacAddress());
		
		//status should now be LIVE
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testIsConfigured() {
		
		// get configured cube
		Cube configuredCube = produceReadyCube();
		Assertions.assertTrue(cubeStatusController.isConfigured(configuredCube));
		
		// get unconfigured cube
		Cube unconfiguredCube = produceLiveCube();
		Assertions.assertFalse(cubeStatusController.isConfigured(unconfiguredCube));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testIsReady() {
		
		// get live cube
		Cube configuredCube = produceLiveCube();
		Assertions.assertFalse(cubeStatusController.isReady(configuredCube));
		
		cubeStatusController.setReady(configuredCube.getMacAddress());
		Assertions.assertTrue(cubeStatusController.isReady(configuredCube));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testDeleteStatus() {

		// produce READY cube
		Cube readyCube = produceReadyCube();
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		// delete status
		cubeStatusController.deleteStatus(readyCube.getMacAddress());
		Assertions.assertNull(cubeStatusController.getStatus(readyCube.getMacAddress()));
	}
	 
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testChangeStatus() {

		// produce READY cube
		Cube readyCube = produceReadyCube();
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		// delete configuration 
		for(Configuration c : configRepo.findByCube(readyCube)) {
			configRepo.delete(c);  
		}
		
		Assertions.assertFalse(cubeStatusController.isConfigured(readyCube));
		
		cubeStatusController.changeStatus(readyCube.getMacAddress());
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(readyCube.getMacAddress()));

		// offline cube
		Cube offlineCube = produceOfflineCube();
		cubeStatusController.changeStatus(offlineCube.getMacAddress());
		
		Assertions.assertEquals(CubeStatus.OFFLINE, cubeStatusController.getStatus(offlineCube.getMacAddress()));
	}
	
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testCheckDeletion() {
	
		// produce READY cube
		Cube readyCube = produceReadyCube();
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));
		
		// it should not be able to delete it
		Assertions.assertFalse(cubeStatusController.checkDeletion(readyCube));
		
		// unconfigured cube which is offline 
		Cube offlineCube = cubeRepo.findById(102L).get();
		Assertions.assertTrue(cubeStatusController.checkDeletion(offlineCube));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testCheckConfiguration() {
		
		// produce LIVE CUBE
		Cube liveCube = produceLiveCube();
		Assertions.assertEquals(CubeStatus.LIVE, cubeStatusController.getStatus(liveCube.getMacAddress()));
		Assertions.assertTrue(cubeStatusController.checkConfiguration(liveCube.getMacAddress()));
		
		// produce READY CUBE
		Cube readyCube = produceReadyCube();
		
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress()));  
		Assertions.assertTrue(cubeStatusController.checkConfiguration(readyCube.getMacAddress()));
		
		//  OFFLINE CUBE
		Cube offlineCube = produceOfflineCube();
		Assertions.assertEquals(CubeStatus.OFFLINE, cubeStatusController.getStatus(offlineCube.getMacAddress()));
		Assertions.assertFalse(cubeStatusController.checkConfiguration(offlineCube.getMacAddress()));
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testUpdateCubeInStatus() {
		
		// produce LIVE cube
		Cube liveCube = produceLiveCube();  
		liveCube.setName("Garfield");
		
		cubeStatusController.updateCubeInStatus(liveCube);
		
		String cubeName = new String();
		
		for(CubeStatusInfo c : cubeStatusController.getCubeStatusInfos()){
			if(c.getCube().getMacAddress().equals(liveCube.getMacAddress()))
				cubeName = c.getCube().getName();
		}
		Assertions.assertEquals("Garfield", cubeName);
	}
	
	@Test
	@DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testUpdateHealthStatus() {
		
		// produce READY cube 
		Cube readyCube = produceReadyCube();
		Assertions.assertEquals(CubeStatus.READY, cubeStatusController.getStatus(readyCube.getMacAddress())); 
		
		// change some parameters
		StatusMessage statusMessage = new StatusMessage();
		statusMessage.setIdentifier(readyCube.getMacAddress());
		statusMessage.setBatteryLevel(5);
		statusMessage.setCalibrationVersion(1);
		statusMessage.setRssi(-90);
		
		cubeStatusController.processStatus(statusMessage);
		cubeStatusController.updateHealthStatus();
		
		// set interval to 2 seconds
		cubeService.updateInterval(IntervalType.EXPIRATION_INTERVAL, 2);
		
		// wait 5 second --> status should be offline
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();  
		}
		Assertions.assertEquals(CubeStatus.OFFLINE, cubeStatusController.getStatus(readyCube.getMacAddress())); 
	}
}
