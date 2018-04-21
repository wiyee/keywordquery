package main.tools;

public class StaticValue {

	/**
	 * 数据库
	 */
	public static final String name="root";
	public static final String pwd="123456";
    public static final String ip="10.1.18.70";
    public static final String dbName="yelp_db";
    public static String driver;
    public static String con;

    public static final String[] stateList = new String[]{"AZ","ON","NV"};
    public static final String STATE = "ON";

	static{
		String[] strArr=StaticMethod.setDataBase(StaticMethod.DbSystem.MYSQL,ip,dbName);
		driver=strArr[0];
		con=strArr[1];
	}
	
}
