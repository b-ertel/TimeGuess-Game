package at.timeguess.backend.ui.beans;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.HealthStatus;
import at.timeguess.backend.model.IntervalType;
import at.timeguess.backend.model.ThresholdType;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.controllers.StatusController;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledTasks {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
	private final DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
	private Map<String, HealthStatus> healthStatus = new ConcurrentHashMap<>();

	@Autowired
	private CubeService cubeService;
	
	@Autowired
	private StatusController statusController;
	
	@Scheduled(fixedRate = 5000) // means every 5 sec
	public void updateHealthStatus() {
		
		this.healthStatus = statusController.getHealthStatus();
		
		if(healthStatus.isEmpty()) {
			log.info("no cubes are online.....");
		}
		else {
			for(Map.Entry<String, HealthStatus> m : healthStatus.entrySet()) {
				if(m.getValue().getTimestamp().isBefore(LocalDateTime.now().minusSeconds(cubeService.queryInterval(IntervalType.EXPIRATION_INTERVAL)))) {
					log.info("cube with mac {} lost connection", m.getKey());
					statusController.setOffline(m.getKey());
					log.info("cube with mac {} changed to status OFFLINE", m.getKey());
				}
				else {
					if(m.getValue().getBatteryLevel() < cubeService.queryThreshold(ThresholdType.BATTERY_LEVEL_THRESHOLD)) {
						log.info("cube with mac {} has low battery (level at {})", m.getKey(), m.getValue().getBatteryLevel());
					}
					if(m.getValue().getRssi() < cubeService.queryThreshold(ThresholdType.RSSI_THRESHOLD)) {
						log.info("something with rssid.....");
					}
					
				}

			}
		}
		log.info("health status of all online cubes was last checked on {}", LocalDateTime.now().format(myFormat));
	}
	
}
