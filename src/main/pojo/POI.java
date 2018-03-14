package main.pojo;

/**
 * Created by wiyee on 2018/3/9.
 * 兴趣点信息
 */
public class POI {
    private String id;
    private String name;
    private String neighborhood;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Double lat;
    private Double lon;
    private Double stars;
    private int reviewCount;
    private int isOpen;

    public POI() {
    }

    public POI(String id, String name, String neighborhood, String address, String city, String state, String postalCode, Double lat, Double lon, Double stars, int reviewCount, int isOpen) {
        this.id = id;
        this.name = name;
        this.neighborhood = neighborhood;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.lat = lat;
        this.lon = lon;
        this.stars = stars;
        this.reviewCount = reviewCount;
        this.isOpen = isOpen;
    }

    @Override
    public String toString() {
        return "POI{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", stars=" + stars +
                ", reviewCount=" + reviewCount +
                ", isOpen=" + isOpen +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }
}
