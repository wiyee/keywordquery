package main.clean;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.pojo.User;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by wiyee on 2018/5/22.
 * 将user表数据根据行为所在地切分
 */
public class SplitUserData {

    /**
     * 将user表中的数据切分
     */
    private void loadUserData(){
        OJDBC jdbc = new OJDBC();
        String sql = "SELECT * from \"user\"";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            List<User> list = new ArrayList<User>();
            while(resultSet.next()) {
                String id = resultSet.getString(1);
                if (isRightCity(id)){
                    String name = resultSet.getString(2);
                    int review_count = resultSet.getInt(3);
                    Date yelp_since = resultSet.getDate(4);
                    double average_stars = resultSet.getDouble(5);
                    int useful = resultSet.getInt(6);
                    int funny = resultSet.getInt(7);
                    int cool = resultSet.getInt(8);
                    int fans = resultSet.getInt(9);
                    User user = new User(id, name, review_count, yelp_since, average_stars, useful, funny, cool, fans);
                    list.add(user);
                    int index = 0;
                    if (list.size() % 2000 == 0){
                        saveUser(list);
                        list.clear();
                        index ++;
                        System.out.println("save successful:" + index * 2000);
                    }
                }
            }
            saveUser(list);
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }

    private static boolean isRightCity(String usrId){
        Set<String> stateSet = new HashSet<String>();
        JDBC jdbc = new JDBC();
        String sql = "SELECT DISTINCT state FROM business WHERE id in (SELECT business_id from review where user_id ='" + usrId + "')";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()) {
                stateSet.add(resultSet.getString(1));
//                System.out.println(resultSet.getString(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
        if (stateSet.contains(StaticValue.STATE)){
            return true;
        }
        return false;
    }

    /**
     * 保存用户数据到oracle的user表中
     * @param list
     */
    private static void saveUser(List<User> list){
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        String insertSql = "insert into \"user_" + StaticValue.STATE + "\" values" + StaticMethod.nMark(9);
        try{
            Connection conn=ojdbc.getConnect();
            stmt = conn.prepareStatement(insertSql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (User user:list) {
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.setInt(3, user.getReview_count());
                stmt.setDate(4, (java.sql.Date) user.getYelp_since());
                stmt.setDouble(5, user.getAverage_stars());
                stmt.setInt(6, user.getUseful());
                stmt.setInt(7, user.getFunny());
                stmt.setInt(8, user.getCool());
                stmt.setInt(9, user.getFans());
                stmt.addBatch();
                stmt.executeBatch();
            }
            conn.commit();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(stmt!=null){
                try{
                    stmt.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            ojdbc.close();

        }
    }

    /**
     * 取出user_[ON,NV,AZ]表中有多次访问行为的用户数据。
     */
    private void loadUserData2(){
        OJDBC jdbc = new OJDBC();
        String sql = "SELECT * from \"user_" + StaticValue.STATE + "\"";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            List<User> list = new ArrayList<User>();
            while(resultSet.next()) {
                String id = resultSet.getString(1);
                int review_count = resultSet.getInt(3);
                if (review_count >= 10){
                    String name = resultSet.getString(2);
                    Date yelp_since = resultSet.getDate(4);
                    double average_stars = resultSet.getDouble(5);
                    int useful = resultSet.getInt(6);
                    int funny = resultSet.getInt(7);
                    int cool = resultSet.getInt(8);
                    int fans = resultSet.getInt(9);
                    User user = new User(id, name, review_count, yelp_since, average_stars, useful, funny, cool, fans);
                    list.add(user);
                    int index = 0;
                    if (list.size() % 2000 == 0){
                        save2OracleUser_Final(list);
                        list.clear();
                        index ++;
                        System.out.println("save successful:" + index * 2000);
                    }
                }
            }
            save2OracleUser_Final(list);
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }
    /**
     * 保存用户数据到oracle的user_final_[ON.NV,AZ]表中
     * @param list
     */
    private static void save2OracleUser_Final(List<User> list){
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        String insertSql = "insert into \"user_final_" + StaticValue.STATE + "\" values" + StaticMethod.nMark(9);
        try{
            Connection conn=ojdbc.getConnect();
            stmt = conn.prepareStatement(insertSql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (User user:list) {
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.setInt(3, user.getReview_count());
                stmt.setDate(4, (java.sql.Date) user.getYelp_since());
                stmt.setDouble(5, user.getAverage_stars());
                stmt.setInt(6, user.getUseful());
                stmt.setInt(7, user.getFunny());
                stmt.setInt(8, user.getCool());
                stmt.setInt(9, user.getFans());
                stmt.addBatch();
                stmt.executeBatch();
            }
            conn.commit();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(stmt!=null){
                try{
                    stmt.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            ojdbc.close();

        }
    }
    public static void main(String[] args) {
        SplitUserData splitUserData = new SplitUserData();
//        splitUserData.loadUserData();
        splitUserData.loadUserData2();
    }

}
