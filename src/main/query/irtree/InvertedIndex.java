package main.query.irtree;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.tools.StaticMethod;
import main.tools.StaticValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by wiyee on 2018/4/23.
 * construct inverted index
 * 1. 从叶子结点开始向根节点计算
 * 2. 获取叶子结点的mbr_id以及所包含的business_id和business_id所对应的标签 -> 获取叶子结点的倒排索引
 */
public class InvertedIndex {

    /**
     *  获取mbr树的最大的深度
     * @return mbr树深度（根节点深度为0）
     */
    private int getMbrDepth(){
        OJDBC ojdbc = new OJDBC();
        String sql = "SELECT MAX(\"depth\") FROM \"mbr_"+ StaticValue.STATE+"\"";
        int depth = 0;
        try {
            ResultSet rs = ojdbc.executeQuery(sql);
            if (rs.next()){
                depth = rs.getInt(1);
            }
            rs.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
        return depth;
    }

    /**
     * 根据深度查询查询mbr_id
     * @param depth
     * @return
     */
    private List<Integer> getMbrIdByDepth(int depth){
        List<Integer> reslist = new ArrayList<Integer>();
        String sql = "SELECT \"id\" FROM \"mbr_" + StaticValue.STATE + "\" WHERE \"depth\" = " + depth;
        OJDBC ojdbc = new OJDBC();
        try {
            ResultSet rs = ojdbc.executeQuery(sql);
            while (rs.next()){
                reslist.add(rs.getInt(1));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
        return reslist;
    }

    /**
     * 根据mbr_father_node查询查询mbr_id
     * 查询一个节点的子节点
     * @param fatherNodeId
     * @return
     */
    private List<Integer> getMbrIdByFatherNodeId(int fatherNodeId){
        List<Integer> reslist = new ArrayList<Integer>();
        String sql = "SELECT \"id\" FROM \"mbr_" + StaticValue.STATE + "\" WHERE \"father_node_id\" = " + fatherNodeId;
        OJDBC ojdbc = new OJDBC();
        try {
            ResultSet rs = ojdbc.executeQuery(sql);
            while (rs.next()){
                reslist.add(rs.getInt(1));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
        return reslist;
    }

    /**
     * 叶子结点倒排索引
     * @param mbr_id
     * @return
     */
    private Map<String,List<String>> getKeywordByBusinessId(int mbr_id){
        String sql = "SELECT \"business_id\",\"category_label\" FROM \"business_topic_" + StaticValue.STATE +
                "\" WHERE \"business_id\" in (SELECT \"poi_id\" FROM \"mbr_contain_poi_" + StaticValue.STATE +
                "\" WHERE \"mbr_id\" = '" + mbr_id + "')";
        OJDBC ojdbc = new OJDBC();

        Map<String,List<String>> invertedMap = new HashMap<String, List<String>>();
        try {
            ResultSet rs = ojdbc.executeQuery(sql);

            while (rs.next()){
                String business_id = rs.getString(1);
                String cate = rs.getString(2);
                if (cate == null){
                    continue;
                }
                if (cate.contains(",")){
                    String[] splitString = cate.split(",");
                    for (String s: splitString){
                        String s1 = s.trim();
                        if (invertedMap.containsKey(s1)){
                            List<String> list = invertedMap.get(s1);
                            list.add(business_id);
                            invertedMap.put(s1,list);
                        }else {
                            List<String> list = new ArrayList<String>();
                            list.add(business_id);
                            invertedMap.put(s1,list);
                        }
                    }
                } else {
                    String s1 = cate.trim();
                    if (invertedMap.containsKey(s1)){
                        List<String> list = invertedMap.get(s1);
                        list.add(business_id);
                        invertedMap.put(s1,list);
                    }else {
                        List<String> list = new ArrayList<String>();
                        list.add(business_id);
                        invertedMap.put(s1,list);
                    }
                }
            }
            rs.close();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            ojdbc.close();
        }
        return invertedMap;
    }

    /**
     * map2json
     * @param invertedMap
     * @return
     */
    private JSONObject map2Json(Map<String,List<String>> invertedMap){
        JSONObject invertedJson = new JSONObject();
        for (String cate:invertedMap.keySet()){
            JSONArray jsonArray = new JSONArray(invertedMap.get(cate));
            invertedJson.put(cate,jsonArray);
        }
        return invertedJson;
    }

    /**
     * json2map
     * @param invJson
     * @return
     */
    private Map<String,List<String>> json2Map(String invJson){
        Map<String,List<String>> res = new HashMap<String, List<String>>();
        JSONObject invertedJson = new JSONObject(invJson);
        Iterator iterator = invertedJson.keys();

        while (iterator.hasNext()){
            String key = (String)iterator.next();
            JSONArray jsonArray = invertedJson.getJSONArray(key);
            res.put(key, (List<String>)(List)jsonArray.toList());
        }
        return res;
    }

    /**
     * 构造mbr树非根节点倒排索引
     * @param invMap
     * @return
     */
    private Map<String,List<String>> mergeMap(Map<Integer,String> invMap){

        Map<String,List<String>> resInvMap = new HashMap<String, List<String>>();
        for (int id:invMap.keySet()){
            Map<String,List<String>> childNodeInvMap = json2Map(invMap.get(id));
            for (String s: childNodeInvMap.keySet()){
                if (resInvMap.containsKey(s)){
                    List<String> idList = resInvMap.get(s);
                    idList.add(String.valueOf(id));
                    resInvMap.put(s,idList);
                } else {
                    List<String> idList = new ArrayList<String>();
                    idList.add(String.valueOf(id));
                    resInvMap.put(s,idList);
                }
            }
        }
        return resInvMap;
    }


    /**
     * 根据mbr_id获取该mbr节点的倒排索引
     * @param mbr_id
     * @return
     */
    private String getInvertedIndexByMbrId(int mbr_id){
        String sql = "SELECT inverted_index_string FROM inverted_index_" + StaticValue.STATE + " WHERE id = " + mbr_id;
        JDBC jdbc = new JDBC();
        String invertedIndexString = null;
        try {
            ResultSet rs = jdbc.executeQuery(sql);
            if (rs.next()){
                invertedIndexString = rs.getString(1);
            }
            rs.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
        return invertedIndexString;
    }

    /**
     * 保存数据
     * @param depth
     * @param map
     */
    private void saveInvertedIndex2Oracle(int depth, Map<Integer,String> map){
        String sql = "INSERT INTO inverted_index_" + StaticValue.STATE + " VALUES" + StaticMethod.nMark(3);
        JDBC jdbc = new JDBC();
        PreparedStatement stmt = null;
        try {
            Connection connection = jdbc.getConnect();
            stmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            for (int id:map.keySet()) {
                stmt.setInt(1,id);
                stmt.setInt(2,depth);
                stmt.setString(3,map.get(id));
                stmt.addBatch();
                stmt.executeBatch();
            }
            connection.commit();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if(stmt!=null){
                try{
                    stmt.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            jdbc.close();
        }
    }

    public static void Run() {
        System.out.println("start...");
        InvertedIndex invertedIndex = new InvertedIndex();
        int depth = invertedIndex.getMbrDepth();
        for (int i = depth; i >= 0; i --){
            // 读取深度为i的mbr树节点
            List<Integer> mbrIdList = invertedIndex.getMbrIdByDepth(i);
            if (i == depth){
                // 保存每个节点的倒排文件
                Map<Integer,String> invMap = new HashMap<Integer, String>();
                int index = 0;
                for (int mbrId:mbrIdList){
                    // 对每个mbr节点构造倒排索引，并保存为json格式
                    JSONObject invJson = invertedIndex.map2Json(invertedIndex.getKeywordByBusinessId(mbrId));
                    invMap.put(mbrId,invJson.toString());

                    // 保存数据
                    if (invMap.size() % 500 == 0){
                        index ++;
                        invertedIndex.saveInvertedIndex2Oracle(i,invMap);
                        invMap.clear();
                        System.out.println("save inverted index to WYY.inverted_index_" + StaticValue.STATE + " depth:" + i + " successful:" + index * 500);
                    }
                }
                invertedIndex.saveInvertedIndex2Oracle(i,invMap);
                System.out.println("save inverted index to WYY.inverted_index_" + StaticValue.STATE + " depth:" + i + " successful");
                invMap.clear();
            } else {
                Map<Integer,String> invMap = new HashMap<Integer, String>();
                int index = 0;
                for (int mbrId:mbrIdList){
                    // 查找mbr_id的所有子节点id
                    List<Integer> childNodeList = new ArrayList<Integer>();
                    childNodeList = invertedIndex.getMbrIdByFatherNodeId(mbrId);
                    // 遍历所有子节点及其倒排索引
                    Map<Integer,String> childMap = new HashMap<Integer, String>();
                    for (int id:childNodeList){
                        childMap.put(id,invertedIndex.getInvertedIndexByMbrId(id));
                    }
                    // 生成父节点倒排索引
                    Map<String,List<String>> fatherInvMap = invertedIndex.mergeMap(childMap);
                    // 保存为json格式
                    JSONObject jsonObject = invertedIndex.map2Json(fatherInvMap);
                    invMap.put(mbrId,jsonObject.toString());
                    // 保存数据
                    if (invMap.size() % 500 == 0){
                        index ++;
                        invertedIndex.saveInvertedIndex2Oracle(i,invMap);
                        invMap.clear();
                        System.out.println("save inverted index to WYY.inverted_index_" + StaticValue.STATE + " depth:" + i + " successful:" + index * 500);
                    }
                }
                invertedIndex.saveInvertedIndex2Oracle(i,invMap);
                System.out.println("save inverted index to WYY.inverted_index_" + StaticValue.STATE + " depth:" + i + " successful:" + index * 500 + " last:" + invMap.size());
                invMap.clear();
            }
        }
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        Run();
        Long end = System.currentTimeMillis();
        System.out.println("end...");
        System.out.println("Time:" + (end - start)/1000f/60f + "min");

//        InvertedIndex invertedIndex = new InvertedIndex();
//        System.out.println(invertedIndex.map2Json(invertedIndex.getKeywordByBusinessId(10724)));
//        Map<Integer,String> map = new HashMap<Integer, String>();
//        map.put(10724,invertedIndex.map2Json(invertedIndex.getKeywordByBusinessId(10724)).toString());
//        invertedIndex.saveInvertedIndex2Oracle(7,map);

//        System.out.println(invertedIndex.getMbrIdByFatherNodeId(10722));
//        System.out.println(invertedIndex.getInvertedIndexByMbrId(121));

//        List<Integer> childNodeList = invertedIndex.getMbrIdByFatherNodeId(10722);
//        Map<Integer,String> childMap = new HashMap<Integer, String>();
//        for (int id:childNodeList){
//            childMap.put(id,invertedIndex.getInvertedIndexByMbrId(id));
//        }
//        Map<String,List<String>> fatherInvMap = invertedIndex.mergeMap(childMap);
//        JSONObject jsonObject = invertedIndex.map2Json(fatherInvMap);
//        System.out.println(jsonObject.toString());

//        String json = "{\"tea\":[\"fHHQ9s6wWPkTMyNVq-0SHQ\"],\"bakeries\":[\"fHHQ9s6wWPkTMyNVq-0SHQ\"],\"smoothies\":[\"ZtEdhtbmBEoZN--g2h2mxA\"],\"coffee\":[\"fHHQ9s6wWPkTMyNVq-0SHQ\"],\"coffee roasteries\":[\"fHHQ9s6wWPkTMyNVq-0SHQ\"],\"restaurants\":[\"ZtEdhtbmBEoZN--g2h2mxA\"],\"sandwiches\":[\"ZtEdhtbmBEoZN--g2h2mxA\"],\"food\":[\"fHHQ9s6wWPkTMyNVq-0SHQ\",\"ZtEdhtbmBEoZN--g2h2mxA\"],\"juice bars\":[\"ZtEdhtbmBEoZN--g2h2mxA\"]}";
//        System.out.println(invertedIndex.json2Map(json));
    }
}
