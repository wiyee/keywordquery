package main.query.preference.topicModel;

import main.jdbc.OJDBC;
import main.pojo.Label;
import main.tools.StaticMethod;
import main.tools.StaticValue;
import org.json.JSONObject;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wiyee on 2018/5/22.
 * 根据用户访问兴趣点行为，结合兴趣点主题向量，构建用户兴趣模型。
 * 1. 基于时间的兴趣遗忘
 * 2. 用户 + 访问行为 + 兴趣点主题向量 = 用户兴趣向量。
 * 3. 根据用户id，获取用户所有访问行为，根据所有访问行为及其对应的兴趣点主题向量，结合时间衰减因子，构建用户兴趣模型。
 */
public class UsrInterestModel {

    public static Map<String,String> resultMap = new HashMap<String, String>();
    public static int index = 0;
    private void loadReviewData(){
        String sql = "SELECT \"id\" FROM \"user_final_" + StaticValue.STATE + "\"";
        OJDBC ojdbc = new OJDBC();
        try{
            ResultSet rs = ojdbc.executeQuery(sql);
            while (rs.next()){
                String usr_id = rs.getString(1);
//                System.out.println(usr_id);
                // 计算用户兴趣
                String userInterestVector = getReviewId(usr_id);
//                System.out.println(userInterestVector);

//                // 保存到数据库
                resultMap.put(usr_id,userInterestVector);
                if (resultMap.size() % 500 == 0){
                    saveUserInterest2Oracle(resultMap);
                    resultMap.clear();
                    index ++;
                    System.out.println("save successful:" + index * 500);
                }
            }
            saveUserInterest2Oracle(resultMap);
            System.out.println("save all successfully!!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getReviewId(String usr_id){
        String sql = "SELECT \"stars\",\"date\",\"business_id\" FROM \"review\" WHERE \"user_id\" = '" + usr_id + "' ";
        OJDBC ojdbc = new OJDBC();
        Map<String,Double> cateMap = new HashMap<String, Double>();
        Map<String,Double> topicMap = new HashMap<String, Double>();
        try{
            ResultSet rs = ojdbc.executeQuery(sql);

            while (rs.next()){

                int stars = rs.getInt(1);
                Date dates = rs.getDate(2);
                String business_id = rs.getString(3);
                // 计算兴趣向量
//                System.out.println(business_id);
                calculateUserTopicVector(stars,dates,business_id,cateMap,topicMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 生成json格式主题向量
        JSONObject jsonObject = generateJsonData(cateMap,topicMap);

        return jsonObject.toString();
    }

    /**
     * 根据兴趣点id获取兴趣点label
     * @param business_id
     * @return
     */
    private Label getPOILabelModel(String business_id){
        // selected by id
        String sql = "SELECT * FROM \"business_topic_ON\" WHERE \"business_id\" = '" + business_id + "'";
        OJDBC ojdbc = new OJDBC();
        ArrayList<String> list = new ArrayList<String>();
        Map<String,String> map = new HashMap<String, String>();
        String id = "";
        try {
            ResultSet rs = ojdbc.executeQuery(sql);
            while (rs.next()){
                id = rs.getString(1);
                String cateLabel = rs.getString(2);
                String topicLabel = rs.getString(3);
                if (cateLabel != null){
                    if (cateLabel.contains(",")){
                        String[] tmpS = cateLabel.split(",");
                        for (String s: tmpS){
                            list.add(s);
                        }
                    } else {
                        list.add(cateLabel);
                    }
                }
                if (topicLabel != null){
                    if (topicLabel.contains(",")){
                        String[] splitS = topicLabel.split(",");
                        for (String s:splitS){
                            if (s.contains("-")){
                                String[] tmpS = s.split("-");
                                map.put(tmpS[0],tmpS[1]);
                            }
                        }
                    } else {
                        String[] tmpS = topicLabel.split("-");
                        map.put(tmpS[0],tmpS[1]);
                    }
                }
            }
        } catch (Exception e ){
            e.printStackTrace();
        }
        return new Label(id,list,map);
    }

    /**
     * 计算一条访问行为的用户主题向量
     * @param stars 兴趣点评分
     * @param date 访问时间
     * @param business_id 兴趣点id
     * @param cateMap 类型评分
     * @param topicMap 主题评分
     */
    private void calculateUserTopicVector(int stars, Date date, String business_id, Map<String,Double> cateMap, Map<String,Double> topicMap){
        // 读取poi主题向量
        Label poiLabel = getPOILabelModel(business_id);

        // 计算遗忘率
        double forgettenRate = calcuteTimeFunction(date);

        ArrayList<String> list = poiLabel.getCategoryLabel();
        Map<String,String> map = poiLabel.getTopicLabel();
        double score = stars * forgettenRate;
        for (String s: list){
            if (cateMap.containsKey(s)){
                double formerScore = cateMap.get(s);
                cateMap.put(s,score + formerScore);
            } else {
                cateMap.put(s,score);
            }
        }
        for (String key: map.keySet()){
            String s = key + "-" + map.get(key);
            if (topicMap.containsKey(s)){
                double formerScore = topicMap.get(s);
                topicMap.put(s,score + formerScore);
            } else {
                topicMap.put(s,score);
            }
        }
    }

    /**
     * 阻尼时间函数，计算兴趣遗忘情况
     *
     * @param date 访问记录时间
     * @return 遗忘率
     * @throws ParseException
     */
    private double calcuteTimeFunction(Date date){
        double forgettingRate = 0d;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date lastDay = df.parse(StaticValue.lastDate);
            int dayInteval = StaticMethod.daysBetween(date,lastDay);
            forgettingRate = (1 + StaticValue.lambd * dayInteval) * Math.exp((-1) * StaticValue.lambd * dayInteval);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return forgettingRate;
    }

    private JSONObject generateJsonData(Map<String,Double> cateMap, Map<String,Double> topicMap){
        JSONObject obj = new JSONObject();
        JSONObject cateObj = new JSONObject();
        JSONObject topicObj = new JSONObject();
        for (String key: cateMap.keySet()){
            cateObj.put(key,cateMap.get(key));
        }
        for (String key: topicMap.keySet()){
            topicObj.put(key,topicMap.get(key));
        }
        obj.put("category",cateObj);
        obj.put("topic",topicObj);

        return obj;
    }

    private void saveUserInterest2Oracle(Map<String,String> result){
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt = null;
        String sql = "INSERT INTO \"user_interest_ON\" VALUES" + StaticMethod.nMark(2);
        try {
            Connection connection = ojdbc.getConnect();
            stmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            for (String user_id:result.keySet()) {
                stmt.setString(1, user_id);
                stmt.setString(2, result.get(user_id));
                stmt.addBatch();
                stmt.executeBatch();
            }
            connection.commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
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
     * test
     * @param args
     */
    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        UsrInterestModel usrInterestModel = new UsrInterestModel();
        usrInterestModel.loadReviewData();
        Long end = System.currentTimeMillis();
        System.out.println("time:" + (end - start) / 1000f/60f + "min");

//        int dayInteval = 90;
//        double forgettingRate = (1 + StaticValue.lambd * dayInteval) * Math.exp((-1) * StaticValue.lambd * dayInteval);
//        System.out.println(forgettingRate);
    }

}
