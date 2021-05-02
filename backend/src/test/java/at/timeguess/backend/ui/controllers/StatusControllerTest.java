package at.timeguess.backend.ui.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeStatus;
import at.timeguess.backend.model.CubeStatusInfo;
import at.timeguess.backend.model.HealthStatus;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.model.api.StatusResponse;
import at.timeguess.backend.repositories.CubeRepository;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

@SpringBootTest
@WebAppConfiguration
@DirtiesContext
@RunWith(MockitoJUnitRunner.class)
public class StatusControllerTest {
	
	@InjectMocks
	private StatusController statusController;
	@Mock
	private CubeRepository cubeRepo;
	@Mock
	private CubeService cubeService;
	
	@BeforeEach
	public void initEach() {
		Cube cubeOne = new Cube();
		cubeOne.setId(100L);
		cubeOne.setMacAddress("56:23:89:34:56");
		cubeService.saveCube(cubeOne);
		
		Cube cubeTwo = new Cube();
		cubeTwo.setId(101L);
		cubeTwo.setMacAddress("22:23:89:90:56");
		cubeService.saveCube(cubeTwo);
		
		Cube cubeThree = new Cube();
		cubeThree.setId(102L);
		cubeThree.setMacAddress("56:00:89:44:56");
		cubeService.saveCube(cubeThree);
		
	}
	
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSetupCubeStatus() {
		
		Cube cubeOne = new Cube();
		cubeOne.setId(100L);
		cubeOne.setMacAddress("56:23:89:34:56");
		cubeService.saveCube(cubeOne);
		
		Cube cubeTwo = new Cube();
		cubeTwo.setId(101L);
		cubeTwo.setMacAddress("22:23:89:90:56");
		cubeService.saveCube(cubeTwo);
		
		Cube cubeThree = new Cube();
		cubeThree.setId(102L);
		cubeThree.setMacAddress("56:00:89:44:56");
		cubeService.saveCube(cubeThree);
		
		List<Cube> allCubes = cubeRepo.findAll();
		
		Assertions.assertEquals(allCubes.size(), statusController.getCubeStatusInfos().size());
		Assertions.assertEquals(3, allCubes.size());
		Assertions.assertEquals(3, statusController.getCubeStatusInfos().size());
	//	Assertions.assertEquals(allCubes.get(0).getMacAddress(), "56:23:89:34:56");
		
	}
    
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testProcessStatus() {
    	
		StatusMessage statusMessage = new StatusMessage();
		
		// get macAddress of known cube
	/*	Cube knownCube = cubeRepo.findById(100L).get();

		
		statusMessage.setIdentifier(knownCube.getMacAddress()); */
		statusMessage.setIdentifier("56:23:89:34:56");
		statusMessage.setBatteryLevel(50);
		statusMessage.setCalibrationVersion(1);
		statusMessage.setRssi(40);
		
		statusController.processStatus(statusMessage);
		
		Assertions.assertEquals(1, statusController.getHealthStatus().size());
		Assertions.assertEquals(CubeStatus.READY, statusController.getStatus("56:23:89:34:56"));
		
    }
    
	/*
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public Cube testUpdateCube() {
    	
    	Cube cube = null;
    	
    	return cube;
    }
    
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testStatusChange() {
    	
    	
    }
    
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public Collection<CubeStatusInfo> testGetCubeStatusInfos() {
    	
    	
    	return null;
    }
    
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public Set<Cube> testGetReadyCubes() {
    	
    	return null;
    }
    
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public Map<String, HealthStatus> testGetHealthStatus() {

		return null;
	}
	*/
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetOffline() { 
		

		
	}
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetInConfig() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetInGame() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetReady() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testSetLive() {
		
	}
	/*
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public CubeStatus testGetStatus() {
		
		return null;
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public boolean testIsConfigured() {
		
		return false;
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testDeleteStatus() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testChangeStatus() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public boolean testCheckDeletion() {
	
		return false;
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public boolean testCheckConfiguration() {
		
		return false;
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testUpdateSockets() {
		
	}
	
	@Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testUpdateCube() {
		
	}
	

	
	
	*/
	
    

}
