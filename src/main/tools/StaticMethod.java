package main.tools;

import java.util.*;
import java.util.Map.Entry;

public class StaticMethod {
	
	public enum DbSystem{
    	ORACLE, MYSQL, SQLSERVER, DB2, POINTBASE
    }
	
	/**
	 * 更改数据库系统
	 */
	public static String[] setDataBase(DbSystem dbSystem, String ip, String dbName)
	{
		String driver="";
		String con="";
		switch (dbSystem) {
		case ORACLE:
			driver="oracle.jdbc.driver.OracleDriver";
			con="jdbc:oracle:thin:@"+ip+":1521:"+dbName;
			break;
			
		case MYSQL:
			driver="com.mysql.jdbc.Driver";
			con="jdbc:mysql://"+ip+":3306/"+dbName + "?useSSL=false";
			break;
			
		case SQLSERVER:
			driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			con="jdbc:microsoft:sqlserver://"+ip+":1433;DatabaseName="+dbName;
			break;
			
		case DB2:
			driver="com.ibm.db2.jcc.DB2Driver";
			con="jdbc:db2://"+ip+":5000/"+dbName;
			break;
			
		case POINTBASE:
			driver="com.pointbase.jdbc.jdbcUniversalDriver";
			con="jdbc:pointbase:server://"+ip+":9092/"+dbName;
			break;
		}
		return new String[]{driver,con};
	}

	/**
	 * map根据value排序(desc)
	 * @param map
	 * @return
	 */
	public static LinkedHashMap<?,Integer> sortMapByValue(Map<?,Integer> map){
		List<Entry<?, Integer>> list = new ArrayList<Entry<?, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		LinkedHashMap<Object,Integer> resultMap = new LinkedHashMap<Object, Integer>();
		for(Entry<?, Integer> t:list){
			resultMap.put(t.getKey(),t.getValue());
		}
		return resultMap;
	}

	/**
	 *
	 * @param n sql语句中待写入数据量（insert into <table> values(...) 省略号中数据条数）
	 * @return
	 */
	public static String nMark(int n) {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < n; i++) {
			sb.append("?,");
		}
		return sb.substring(0, sb.length() - 1) + ")";
	}

	/**
	 * 计算两个日期之间的天数
	 *
	 * @param start 开始日期
	 * @param end   结束日期
	 * @return 两个日期之间的天数
	 * @author wiyee
	 */
	public static int daysBetween(Date start, Date end) {
		int difference;
		difference = (int) ((end.getTime() - start.getTime()) / 86400000);
		return difference;
	}
}
