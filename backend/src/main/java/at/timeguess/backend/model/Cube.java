package at.timeguess.backend.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cube {

	@Id
	private Long id;
	
	private String macAddress;
	
	private boolean config;
	
	private Long deviceNo;
	
	@OneToMany(mappedBy="cube")
	private List<CubeFace> cubeFaces;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<CubeFace> getCubeFaces() {
		return cubeFaces;
	}

	public void setCubeFaces(List<CubeFace> cubeFaces) {
		this.cubeFaces = cubeFaces;
	}

	public Long getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(Long deviceNo) {
		this.deviceNo = deviceNo;
	}
	
	
	
}
