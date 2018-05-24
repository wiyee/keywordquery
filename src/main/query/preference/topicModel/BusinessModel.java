package main.query.preference.topicModel;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.pojo.Label;
import main.pojo.MBR;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by wiyee on 2018/5/18.
 *
 */
public class BusinessModel {

    /**
     * 加载兴趣点数据
     */
    public void loadBusinessData(){
        OJDBC ojdbc = new OJDBC();
        String sql = "SELECT \"id\" from \"business_"+ StaticValue.STATE + "\"";
        List<Label> resultList = new ArrayList<Label>();
        try {
            ResultSet resultSet = ojdbc.executeQuery(sql);
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                // 提取id数据
//                String id = "-6VXNsPHyYtHV71JBAdsIA";
                ArrayList<String> labelList = getCategoryLabelByBusinessId(id);
                Map<String,String> topicMap = getTopicLabelByBusinessId(id);
//                if (labelList.size() == 0){
//                    System.out.println(labelList.toString().replace("[","").replace("]",""));
//                }
//                if (topicMap.size() == 0){
//                    System.out.println(map2String(topicMap));
//                }
                resultList.add(new Label(id,labelList,topicMap));
                if (resultList.size() % 2000 == 0){
                    saveLabel2Oracle(resultList);
                    resultList.clear();
                }
            }
            saveLabel2Oracle(resultList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
    }


    /**
     * 获取兴趣点主题标签信息
     * @param id
     * @return
     */
    private Map<String, String> getTopicLabelByBusinessId(String id){
        Map<String,String> topicLabelMap = new HashMap<String, String>();
        JDBC jdbc = new JDBC();
        String sql = "select name,value from attribute where business_id = '" + id + "'";
        try {
            ResultSet rs = jdbc.executeQuery(sql);
            while (rs.next()){
                String labelName = rs.getString(1);
                String labelValue = rs.getString(2);
                if (labelValue.equals("0"))
                    continue;
                analyseTopicValue(labelName,labelValue,topicLabelMap);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            jdbc.close();
        }

        return topicLabelMap;
    }

    /**
     * 获取兴趣点类别标签信息
     * @param id
     * @return
     */
    private ArrayList<String> getCategoryLabelByBusinessId(String id){
        OJDBC ojdbc = new OJDBC();
        ArrayList<String> categoryList = new ArrayList<String>();
        String sql = "SELECT \"category\" from \"category_"+ StaticValue.STATE + "\" where \"business_id\" = '" + id +"'";
//        System.out.println(sql);
        try {
            ResultSet resultSet = ojdbc.executeQuery(sql);
            while (resultSet.next()) {
                String category = resultSet.getString(1);
                // 处理保存类型标签
                analyseKeyword(category,categoryList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
        return categoryList;
    }

    /**
     * 兴趣点类别标签处理
     * @param keyword
     * @param list
     */
    private void analyseKeyword(String keyword, ArrayList<String> list){
        if (keyword.equals(""))
            return;
        if (keyword.contains("&")){
            String[] tmp = keyword.split("&");
            for (int i = 0; i < tmp.length; i ++){
                analyseKeyword(tmp[i],list);
            }
        } else {
            String key = keyword.toLowerCase().trim();
            list.add(key);
        }
    }

    /**
     * 兴趣点主题标签处理
     * @param labelName
     * @param labelValue
     * @param resultMap
     */
    private void analyseTopicValue(String labelName, String labelValue, Map<String,String> resultMap){
        if (labelValue.charAt(0) == '{'){
            boolean index = false;
            StringBuilder sb = new StringBuilder();
            JSONObject jsonObject=new JSONObject(labelValue);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                boolean value = jsonObject.getBoolean(key);
                if (value == true){
                    if (index == true)
                        sb.append(",");
                    sb.append(key);
                    index = true;
                }
            }
            if (index == false){
                resultMap.put(labelName.toLowerCase(),"1");
            } else {
                resultMap.put(labelName.toLowerCase(),sb.toString());
            }
        } else {
            resultMap.put(labelName.toLowerCase(),labelValue);
        }
    }


    /**
     * 保存结果到oracle数据库的
     * @param resultList
     */
    private void saveLabel2Oracle(List<Label> resultList){
        String sql = "insert into \"business_topic_"+ StaticValue.STATE + "\" values" + StaticMethod.nMark(3);

        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        Connection conn= null;
        try{
            conn = ojdbc.getConnect();
            stmt = conn.prepareStatement(sql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            for (Label label: resultList){
                stmt.setString(1,label.getBusiness_id());
                stmt.setString(2,label.getCategoryLabel().toString().replace("[","").replace("]",""));
                stmt.setString(3,map2String(label.getTopicLabel()));
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

    /**
     * topicMap -> String
     * <key,value> -> key-value
     * @param resultMap
     * @return
     */
    private String map2String(Map<String,String> resultMap){
        if (resultMap.size() == 0)
            return new String();
        StringBuilder sb = new StringBuilder();
        for (String key: resultMap.keySet()){
            sb.append(key + "-" + resultMap.get(key) + ",");
        }
        return sb.toString().substring(0,sb.toString().length() - 1);
    }


    /**
     * test
     * @param args
     */
    public static void main(String[] args) {

        BusinessModel businessModel = new BusinessModel();
        businessModel.loadBusinessData();


//        ArrayList<String> list = new ArrayList<String>();
//        list.add("shops");
//        list.add("gototo");
//        Map<String,String> map = new HashMap<String, String>();
//        String id = "-afasdfassas";
//        Label label = new Label(id,list,map);
//        List<Label> res = new ArrayList<Label>();
//        res.add(label);
//        businessModel.saveLabel2Oracle(res);

//        String str = "{\"garage\": false, \"street\": false, \"validated\": false, \"lot\": false, \"valet\": false}";
//        JSONObject jsonObject=new JSONObject(str);
//        Iterator iterator = jsonObject.keys();
//
//        while (iterator.hasNext()){
//            String key = (String)iterator.next();
//            boolean value = jsonObject.getBoolean(key);
//            System.out.println(key + ":" + value);
//        }
    }

}
