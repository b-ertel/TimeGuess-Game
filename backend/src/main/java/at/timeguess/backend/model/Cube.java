package at.timeguess.backend.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cube {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String macAddress;
	
	private String name;
	
	private Integer configuration = 0;
	
	private CubeStatus cubeStatus = CubeStatus.OFFLINE;
	
	@OneToMany(mappedBy="cube")
	private List<Configuration> configs = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Configuration> getConfigs() {
		return configs;
	}

	public void addConfigs(Configuration config) {
		this.configs.add(config);
	}

	public Integer getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Integer configuration) {
		this.configuration = configuration;
	}

	public CubeStatus getCubeStatus() {
		return cubeStatus;
	}

	public void setCubeStatus(CubeStatus cubeStatus) {
		this.cubeStatus = cubeStatus;
	}

	
}