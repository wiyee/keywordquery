package main.pojo;

/**
 * Created by wiyee on 2018/4/25.
 * position
 */
public class Position {
    private double lon; // 经度
    private double lat; // 维度

    public Position(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
