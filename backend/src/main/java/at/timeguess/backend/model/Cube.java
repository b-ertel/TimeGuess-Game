package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.domain.Persistable;

@Entity
public class Cube implements Serializable, Comparable<Cube>, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String macAddress;

    private String name;

    @OneToMany(mappedBy = "cube")
    private List<Configuration> configs = new ArrayList<>();

    @Override
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Cube other = (Cube) obj;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 11;
        int result = 57;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public int compareTo(Cube o) {
        return this.getId() == null ? o.getId() == null ? 0 : -1
                : o.getId() == null ? 1 : this.getId().compareTo(o.getId());
    }

    @Override
    public String toString() {
        return getId().toString() + " [" + getMacAddress().toString() + "] ";
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id == 0L;
    }
}
