package main.pojo;

import java.util.Date;

/**
 * Created by wiyee on 2018/3/13.
 * user类
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

    public User() {
    }

    public User(String id, String name, int review_count, Date yelp_since, double average_stars, int useful, int funny, int cool, int fans) {
        this.id = id;
        this.name = name;
        this.review_count = review_count;
        this.yelp_since = yelp_since;
        this.average_stars = average_stars;
        this.useful = useful;
        this.funny = funny;
        this.cool = cool;
        this.fans = fans;
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

}
