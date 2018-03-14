package main.pojo;

import java.util.Date;

/**
 * Created by wiyee on 2018/3/13.
 */
public class User {
    private String id;
    private String name;
    private int review_count;
    private Date yelp_since;
    private double average_stars;
    private int useful;
    private int funny;
    private int cool;
    private int fans;
    private String activeCity;
    private String activeState;

    public User() {
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

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public Date getYelp_since() {
        return yelp_since;
    }

    public void setYelp_since(Date yelp_since) {
        this.yelp_since = yelp_since;
    }

    public double getAverage_stars() {
        return average_stars;
    }

    public void setAverage_stars(double average_stars) {
        this.average_stars = average_stars;
    }

    public int getUseful() {
        return useful;
    }

    public void setUseful(int useful) {
        this.useful = useful;
    }

    public int getFunny() {
        return funny;
    }

    public void setFunny(int funny) {
        this.funny = funny;
    }

    public int getCool() {
        return cool;
    }

    public void setCool(int cool) {
        this.cool = cool;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getActiveCity() {
        return activeCity;
    }

    public void setActiveCity(String activeCity) {
        this.activeCity = activeCity;
    }

    public String getActiveState() {
        return activeState;
    }

    public void setActiveState(String activeState) {
        this.activeState = activeState;
    }
}
