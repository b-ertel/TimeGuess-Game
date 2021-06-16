package at.timeguess.backend.model;

/**
 * Just combines a cube and its status.
 */
public class CubeStatusInfo {

    private Cube cube;
    private CubeStatus status = CubeStatus.OFFLINE;

    public CubeStatusInfo(Cube cube) {
        super();
        this.cube = cube;
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    public CubeStatus getStatus() {
        return status;
    }

    public void setStatus(CubeStatus status) {
        this.status = status;
    }
}
