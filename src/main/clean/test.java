package main.clean;

import main.jdbc.JDBC;
import main.jdbc.OJDBC;
import main.pojo.POI;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.sql.*;
import java.util.*;

/**
 * Created by wiyee on 2018/3/9.
 * 将兴趣点拆分 保留三个城市及其附近的poi信息
 */
public class test {
    public static void main(String[] args) {
        Map<POI,Integer> poiMapNV = new LinkedHashMap<POI, Integer>();
        Map<POI,Integer> poiMapON = new LinkedHashMap<POI, Integer>();
        Map<POI,Integer> poiMapAZ = new LinkedHashMap<POI, Integer>();
        List<POI> nvList = new ArrayList<POI>();
        JDBC jdbc = new JDBC();
        String sql = "SELECT * from business where state='AZ'";
        try {
            ResultSet resultSet = jdbc.executeQuery(sql);
            while(resultSet.next()){
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String neighborhood = resultSet.getString(3);
                String address = resultSet.getString(4);
                String city = resultSet.getString(5);
                String state = resultSet.getString(6);
                String postalCode = resultSet.getString(7);
                double lat = resultSet.getDouble(8);
                double lon = resultSet.getDouble(9);
                double star = resultSet.getDouble(10);
                int reviewCount = resultSet.getInt(11);
                int isOpen = resultSet.getInt(12);
                if (state.equals("AZ"))
                    nvList.add(new POI(id,name,neighborhood,address,city,state,postalCode,lat,lon,star,reviewCount,isOpen));
//                else if (state.equals("AZ"))
//                    poiMapAZ.put(new POI(id,name,neighborhood,address,city,state,postalCode,lat,lon,star,reviewCount,isOpen),count);
//                else if (state.equals("ON"))
//                    poiMapON.put(new POI(id,name,neighborhood,address,city,state,postalCode,lat,lon,star,reviewCount,isOpen),count);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jdbc.close();
        }
//        LinkedHashMap<?,Integer> map = StaticMethod.sortMapByValue(poiMapON);
//        for (Object entry:map.keySet()){
//            POI poi = (POI)entry;
//            System.out.println(poi.getCity() + " " + poi.getState() + " " + map.get(entry));
//        }
        OJDBC ojdbc = new OJDBC();
        PreparedStatement stmt=null;
        String insertSql = "insert into \"business_AZ\" values" + StaticMethod.nMark(12);
        try{
            Connection conn=ojdbc.getConnect();
            stmt = conn.prepareStatement(insertSql);
            // 方式2：批量提交
            conn.setAutoCommit(false);
            int n = 0;
            for (POI poi:nvList) {
                stmt.setString(1, poi.getId());
                stmt.setString(2, poi.getName());
                stmt.setString(3, poi.getNeighborhood());
                stmt.setString(4, poi.getAddress());
                stmt.setString(5, poi.getCity());
                stmt.setString(6, poi.getState());
                stmt.setString(7, poi.getPostalCode());
                stmt.setDouble(8, poi.getLat());
                stmt.setDouble(9, poi.getLon());
                stmt.setDouble(10, poi.getStars());
                stmt.setInt(11,poi.getReviewCount());
                stmt.setInt(12,poi.getIsOpen());
                stmt.addBatch();
                n++;
                if(n%5000==0){
                    stmt.executeBatch();
                    n=0;
                }
            }
            stmt.executeBatch();
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


}
