package main.query.irtree;


import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import main.jdbc.OJDBC;
import main.pojo.MBR;
import main.pojo.Review;
import main.pojo.User;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 构造R树，返回MBR矩阵和矩阵中的兴趣点
public class RStarTree {
    /**
     * 读取兴趣点数据，并构造r*-tree
     */
    public RTree<String, Geometry> loadPOIData() {
        RTree<String, Geometry> tree = RTree.star().maxChildren(6).create();
        OJDBC ojdbc = new OJDBC();
        String sql = "SELECT \"id\",\"longitude\",\"latitude\" from \"business_"+ StaticValue.STATE + "\"";
        try {
            ResultSet resultSet = ojdbc.executeQuery(sql);
            List<User> list = new ArrayList<User>();
            int i = 1;
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                double lon = resultSet.getDouble(2);//经度
                double lat = resultSet.getDouble(3);//维度
                Point point = Geometries.point(lon, lat);
                tree = tree.add(id, point);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ojdbc.close();
        }
        return tree;
    }

    /**
     * 对poi数据构建ir树，解析it树的mbr矩阵树结构，将ir树的mbr结构以及叶子结点所在的mbr信息存到oracle
     * @param tree
     */
    public void analyseIRtree(RTree<String, Geometry> tree){
        int maxDepth = tree.calculateDepth();
        Map<String,Integer> POIMap = new HashMap<String, Integer>(); // 保存poi信息，长度超过5000时保存到数据库
        List<MBR> list = new ArrayList<MBR>(); // 保存mbr信息，长度超过5000时保存到数据库
        int id = 10000;
        ArrayList<Stack<Integer>> mbrList = new ArrayList<Stack<Integer>>();
        for(int i = 0; i < maxDepth; i++){
            Stack<Integer> stack = new Stack<Integer>();
            stack.push(-1);
            mbrList.add(stack);
        }
        String[] treeString = tree.asString().split("\n");
        for (int i = 0; i < treeString.length; i ++){
//            System.out.println(treeString[i]);
            if (treeString[i].contains("mbr=")){ // 判断是否为mbr
                int depth = treeString[i].indexOf("mbr")/2;

                int fatherNodeId = 0;
                if (depth==0){
                    fatherNodeId = -1;
                    mbrList.get(depth).push(id);
                } else {
                    fatherNodeId = mbrList.get(depth-1).peek();
                    mbrList.get(depth).push(id);
                }
                MBR mbr = new MBR(id,fatherNodeId,depth,getLocation("x1",treeString[i]),getLocation("y1",treeString[i]),getLocation("x2",treeString[i]),getLocation("y2",treeString[i]));
                list.add(mbr);
                if (list.size() % 2000 == 0){
                    saveMBR2Oracle(list);
                    list.clear();
                }
                id ++;
            }
            else { // 否则为poi实体  保存poi id 和所属mbr到数据库
                String poiId = getPOIId(treeString[i]); // 获取poi id
                int mbr = mbrList.get(maxDepth-1).peek();
                POIMap.put(poiId,mbr);
            }
            if (POIMap.size() % 5000 == 0){
                savePoi2Oracle(POIMap);
                POIMap.clear();
            }
        }
        // 保存list和map中剩余的数据
        saveMBR2Oracle(list);
        savePoi2Oracle(POIMap);
    }

    /**
     * 获取mbr的坐标位置，实参：[x1,y1,x2,y2]分别表示最小经度，最小维度，最大经度，最大维度
     * @param regex
     * @param content
     * @return
     */
    private Double getLocation(String regex,String content){
        String pattern = regex+"=[^,|\\]]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        if (m.find()){
            return Double.parseDouble(m.group(0).replaceFirst(regex+"=",""));
        }
        return 0.0;
    }

    /**
     * 保存mbr数据
     * @param list
     */
    private void saveMBR2Oracle(List<MBR> list){
        String sql = "insert into \"mbr_"+ StaticValue.STATE + "\" values" + StaticMethod.nMark(7);
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        Connection conn= null;
        try{
            conn = ojdbc.getConnect();
            stmt = conn.prepareStatement(sql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (MBR mbr:list) {
                stmt.setInt(1,mbr.getId());
                stmt.setInt(2, mbr.getFatherNodeId());
                stmt.setInt(3,mbr.getDepth());
                stmt.setDouble(4,mbr.getMinLon());
                stmt.setDouble(5,mbr.getMinLat());
                stmt.setDouble(6,mbr.getMaxLon());
                stmt.setDouble(7,mbr.getMaxLat());
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
     * 保存mbr叶子结点的poi数据
     * @param map
     */
    private void savePoi2Oracle(Map<String,Integer> map){
        String sql = "insert into \"mbr_contain_poi_"+ StaticValue.STATE + "\" values" + StaticMethod.nMark(2);
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        Connection conn= null;
        try{
            conn = ojdbc.getConnect();
            stmt = conn.prepareStatement(sql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (String id:map.keySet()) {
                stmt.setString(1, id);
                stmt.setInt(2, map.get(id));
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
     * 获取mbr树结构中poi的id信息
     * @param content
     * @return
     */
    private String getPOIId(String content){
        String pattern = "value=[^,]+";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        if (m.find()){
            return m.group(0).replaceFirst("value=","");
        }
        return "";
    }


    /**
     * test
     * @param args
     */
    public static void main(String[] args) {

//        RStarTree rStarTree = new RStarTree();
//        RTree<String, Geometry> tree = rStarTree.loadPOIData();
//        System.out.println(tree.calculateDepth());
//        System.out.println(tree.root().isPresent());
//        tree.visualize(600,600).save("F:\\论文实验\\文档\\mbr.png");
    }
}
