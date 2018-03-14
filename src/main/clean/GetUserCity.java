package main.clean;

import main.jdbc.JDBC;
import main.pojo.POI;
import main.pojo.User;

import java.sql.ResultSet;
import java.util.Date;

/**
 * Created by wiyee on 2018/3/14.
 */
public class GetUserCity {
    /*
     * 判断用户所在地点，根据state不同保存在不同的表里
     */
    private static void getUser(){

        JDBC jdbc = new JDBC();
        String sql = "SELECT id,name,review_count,yelp_since,average_stars,useful,funny,cool,fans from user";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()) {
                User user = new User();
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                int review_count = resultSet.getInt(3);
                Date yelp_since = resultSet.getDate(4);
                double average_stars = resultSet.getDouble(5);
                int useful = resultSet.getInt(6);
                int funny = resultSet.getInt(7);
                int cool = resultSet.getInt(8);
                int fans = resultSet.getInt(9);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }

    /*
     * 根据用户id获取用户活跃城市
     */
    private static POI getPOI(String poiId){
        POI poi = new POI();
        JDBC jdbc = new JDBC();
        String sql = "SELECT city,state from business where id=" + poiId;
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()) {
                poi.setCity(resultSet.getString(1));
                poi.setState(resultSet.getString(2));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
        return poi;
    }

    public static void main(String[] args) {
        getUser();
    }
}
