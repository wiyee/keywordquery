package main.clean;

import main.jdbc.JDBC;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wiyee on 2018/4/21.
 * 将表category根据城市分类
 * 将category表中的结果根据state分别保存到[category_AZ,category_ON,category_CV]中
 */
public class SplitCategoryData {
    public static Map<String,String> categoryAZMap = new LinkedHashMap<String, String>();
    public static Map<String,String> categoryONMap = new LinkedHashMap<String, String>();
    public static Map<String,String> categoryNVMap = new LinkedHashMap<String, String>();
    public static int[] stateCount = new int[]{0,0,0};
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
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()){
                String id = resultSet.getString(1);
                String category = resultSet.getString(2);
                String state = getState(id);
                if (!state.equals("")){
                    if (state.equals("ON")){
                        categoryONMap.put(id,category);
                    } else if (state.equals("NV")){
                        categoryNVMap.put(id,category);
                    } else {
                        categoryAZMap.put(id,category);
                    }
                    // save
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
    }

    private void saveSplitedCate2Oracle(){

    }

    public static void main(String[] args) {
        SplitCategoryData splitCategoryData = new SplitCategoryData();
        splitCategoryData.loadDate();
    }


}
