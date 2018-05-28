package main.pojo;

import java.util.Date;

/**
 * Created by wiyee on 2018/3/13.
 * 评价信息
 */
public class Review {
    private String reviewId;
    private double stars;
    private Date date;
    private String text;
    private int useful;
    private int funny;
    private int cool;
    private String businessId;
    private String userId;


    public Review() {
    }

    public Review(double stars, Date date, String businessId) {
        this.stars = stars;
        this.date = date;
        this.businessId = businessId;
    }

    public Review(String reviewId, double stars, Date date, String text, int useful, int funny, int cool, String businessId, String userId) {
        this.reviewId = reviewId;
        this.stars = stars;
        this.date = date;
        this.text = text;
        this.useful = useful;
        this.funny = funny;
        this.cool = cool;
        this.businessId = businessId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", stars=" + stars +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", useful=" + useful +
                ", funny=" + funny +
                ", cool=" + cool +
                ", businessId='" + businessId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
