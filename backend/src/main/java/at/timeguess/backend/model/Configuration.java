package at.timeguess.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Configuration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "cube_id")
	private Cube cube;
	
	@ManyToOne
	@JoinColumn(name = "cubeface_id")
	private CubeFace cubeface;
	
	private Integer configuration = 0;
	
	private String macAddress;

	public Configuration() {
		
	}
	
	public Configuration(Cube cube, CubeFace cubeFace, String mac) {
		setCube(cube);
		setCubeface(cubeFace);
		setMacAddress(mac);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cube getCube() {
		return cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}

	public CubeFace getCubeface() {
		return cubeface;
	}

	public void setCubeface(CubeFace cubeface) {
		this.cubeface = cubeface;
	}

	public Integer getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Integer configuration) {
		this.configuration = configuration;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	
}
