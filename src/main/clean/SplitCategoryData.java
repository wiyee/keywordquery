package main.clean;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.pojo.Category;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;


/**
 * Created by wiyee on 2018/4/21.
 * 将表category根据城市分类
 * 将category表中的结果根据state分别保存到[category_AZ,category_ON,category_CV]中
 */
public class SplitCategoryData {

    private static List<Category> list = new ArrayList<Category>();
    private static int cate_id = 10000;

    /**
     * 根据id判断poi所在的state，并将ON CV AZ三个州的数据保存
     * @param id
     */
    private String getState(String id){
        String state = "";
        JDBC jdbc = new JDBC();
        String sql = "SELECT state from business where id = '" + id + "'" ;
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            if (resultSet.next()){
                state = resultSet.getString(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
        return state;
    }

    /**
     * 读取数据
     */
    public void loadDate(){
        JDBC jdbc = new JDBC();
        String sql = "SELECT * from category";
//        int count = 0;
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()){
                String id = resultSet.getString(1);
                String category = resultSet.getString(2);
                String state = getState(id);
                if (!state.equals("")){
//                    count++;
                    if (state.equals(StaticValue.STATE)){
                        list.add(new Category(cate_id++,id,category));
                    }
                    if (list.size() > 2000){
                        saveSplitedCate2Oracle(list);
                        System.out.println("save...");
                        list.clear();
                    }
                }
            }
            saveSplitedCate2Oracle(list);
//            System.out.println(count);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }

    private void saveSplitedCate2Oracle(List<Category> list){
        String sql = "insert into \"category_"+ StaticValue.STATE + "\" values" + StaticMethod.nMark(3);
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        Connection conn= null;
        try{
            conn = ojdbc.getConnect();
            stmt = conn.prepareStatement(sql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (Category category:list) {
                stmt.setInt(1,category.getCategory_id());
                stmt.setString(2, category.getBusiness_id());
                stmt.setString(3, category.getCategory());
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
            if (conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ojdbc.close();
        }
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        System.out.println("load category data...");
        SplitCategoryData splitCategoryData = new SplitCategoryData();
        splitCategoryData.loadDate();
        System.out.println("successful...");
        Long end = System.currentTimeMillis();
        System.out.println("time:" + (end-start)/1000f/60f + "min");
    }

}
