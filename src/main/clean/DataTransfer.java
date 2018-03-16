package main.clean;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.pojo.Review;
import main.pojo.User;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by wiyee on 2018/3/16.
 */
public class DataTransfer {
    private void getReview(){
        JDBC jdbc = new JDBC();
        try {
            for (int i=0;i<4719799;i+=5000){
                String sql = "SELECT * from review LIMIT " + i + ",5000";
                ResultSet resultSet = jdbc.executeQuery(sql);
                List<Review> list = new ArrayList<Review>();
                while(resultSet.next()) {
                    String businessId = resultSet.getString(8);
                    if (isRightReview(businessId)){
                        String id = resultSet.getString(1);
                        double stars = resultSet.getDouble(2);
                        Date date = resultSet.getDate(3);
                        String text = resultSet.getString(4);
                        int useful = resultSet.getInt(5);
                        int funny = resultSet.getInt(6);
                        int cool = resultSet.getInt(7);
                        String userId = resultSet.getString(9);
                        Review review = new Review(id,stars,date,text,useful,funny,cool,businessId,userId);
                        list.add(review);
                    }
                }
                saveReview(list);
                list.clear();
                System.out.println("save successful:" + i);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }

    private boolean isRightReview(String id) {
        Set<String> stateSet = new HashSet<String>();
        JDBC jdbc = new JDBC();
        String sql = "SELECT state FROM business WHERE id='" + id + "'";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()) {
                stateSet.add(resultSet.getString(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
        for (String s: StaticValue.state){
            if (stateSet.contains(s)){
                return true;
            }
        }
        return false;
    }

    private void saveReview(List<Review> list ){
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        String insertSql = "insert into \"review\" values" + StaticMethod.nMark(9);
        try{
            Connection conn=ojdbc.getConnect();
            stmt = conn.prepareStatement(insertSql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (Review review:list) {
                stmt.setString(1, review.getReviewId());
                stmt.setDouble(2,review.getStars());
                stmt.setDate(3, (java.sql.Date) review.getDate());
                stmt.setString(4,review.getText());
                stmt.setInt(5,review.getUseful());
                stmt.setInt(6,review.getFunny());
                stmt.setInt(7,review.getCool());
                stmt.setString(8,review.getBusinessId());
                stmt.setString(9,review.getUserId());
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
        DataTransfer dataTransfer = new DataTransfer();
        dataTransfer.getReview();
    }
}
