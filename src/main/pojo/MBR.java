package main.pojo;

/**
 * Created by wiyee on 2018/4/20.
 * mbr类
 */

public class MBR {
    private int id;
    private int fatherNodeId;
    private int depth;
    private double minLon;
    private double minLat;
    private double maxLon;
    private double maxLat;

    public MBR(int id, int fatherNodeId, int depth, double minLon, double minLat, double maxLon, double maxLat) {
        this.id = id;
        this.fatherNodeId = fatherNodeId;
        this.depth = depth;
        this.minLon = minLon;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.maxLat = maxLat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFatherNodeId() {
        return fatherNodeId;
    }

    public void setFatherNodeId(int fatherNodeId) {
        this.fatherNodeId = fatherNodeId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }
}
