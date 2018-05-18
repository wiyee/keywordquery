package main.query.preference.topicModel;

import main.jdbc.OJDBC;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiyee on 2018/5/2.
 * 统计每个地区的Keyword种类和数量
 */
public class KeywordCount {

    public static Map<String,Integer> keyMap = new HashMap<String, Integer>(); //保存keyword 和count

    public static void main(String[] args){
        System.out.println("start...");
        Long start = System.currentTimeMillis();
        KeywordCount keywordCount = new KeywordCount();
        keywordCount.loadData();
//        System.out.println(keyMap);
        keyMap.remove("");
        keywordCount.saveData2Oracle();
        Long end = System.currentTimeMillis();
        System.out.println("success...");
        System.out.println("Time:" + (end - start)/1000f/60f + "min");
    }

    /**
     * 加载数据。
     * 读取category_[AZ、ON、NV]中的数据
     */
    public void loadData(){
        OJDBC ojdbc = new OJDBC();
        String sql = "SELECT \"category\" from \"category_"+ StaticValue.STATE + "\"";
        try {
            ResultSet resultSet = ojdbc.executeQuery(sql);
            while (resultSet.next()) {
                String keyword = resultSet.getString(1);
                analyseKeyword(keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
    }

    /**
     * 处理关键字信息，保存到keymap中
     * @param keyword
     */
    private void analyseKeyword(String keyword){
        if (keyword.equals(""))
            return;
        if (keyword.contains("&")){
            String[] tmp = keyword.split("&");
            for (int i = 0; i < tmp.length; i ++){
                analyseKeyword(tmp[i]);
            }
        } else {
            String key = keyword.trim();
            if (keyMap.containsKey(key)){
                int count = keyMap.get(key);
                keyMap.put(key,count + 1);
            } else {
                keyMap.put(key,1);
            }
        }
    }

    /**
     * 保存数据到oracle的keyset_[ON、AZ、ON]表
     */
    public void saveData2Oracle(){
        String sql = "insert into \"keyset_"+ StaticValue.STATE + "\" values" + StaticMethod.nMark(3);
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        Connection conn= null;
        try{
            conn = ojdbc.getConnect();
            stmt = conn.prepareStatement(sql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int id = 10000;
            for (String keyword:keyMap.keySet()) {
                stmt.setInt(1, id);
                stmt.setString(2, keyword);
                stmt.setInt(3,keyMap.get(keyword));
                stmt.addBatch();
                stmt.executeBatch();
                id++;
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

}
