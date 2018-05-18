//package main.clean;
//
//import com.github.davidmoten.rtree.geometry.Geometries;
//import com.github.davidmoten.rtree.geometry.Point;
//import main.jdbc.OJDBC;
//import main.pojo.User;
//import main.tools.StaticValue;
//
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by wiyee on 2018/5/17.
// * 统计城市中包含的所有关键词。
// * table: category_ON
// */
//public class KeywordSet {
//
//    private static Set<String> keySet = new HashSet<String>();
//
//    private void loadCategoryData(){
//        OJDBC ojdbc = new OJDBC();
//        String sql = "SELECT \"category\" from \"category_"+ StaticValue.STATE + "\"";
//        try {
//            ResultSet resultSet = ojdbc.executeQuery(sql);
//            List<User> list = new ArrayList<User>();
//            while (resultSet.next()) {
//                String category = resultSet.getString(1);
//                analyseString(category);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            ojdbc.close();
//        }
//        System.out.println(keySet);
//        System.out.println(keySet.size());
//    }
//
//    private void analyseString(String keyString){
//        if (keyString.equals(""))
//            return;
//        if (keyString.contains("&")){
//            String[] subString = keyString.split("&");
//            for (String aSubString : subString) {
//                analyseString(aSubString);
//            }
//        } else if (keyString.contains("/")){
//            String[] subString = keyString.split("/");
//            for (String aSubString : subString) {
//                analyseString(aSubString);
//            }
//        } else {
//            keyString = keyString.toLowerCase().trim();
//            if (!keySet.contains(keyString)){
//                keySet.add(keyString);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        KeywordSet keywordSet = new KeywordSet();
//        keywordSet.loadCategoryData();
//    }
//}
