package main.tools;

import java.util.Date;

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

    // 数据库中review表的最后数据的时间
    public static final String lastDate = "2017-07-26";
    // 遗忘函数半衰期
    public static final int halfPeriod = 90;
    // 遗忘函数衰减率
    public static final double lambd = 1 / (halfPeriod * 1.0);

//==========================================score paramater=============================================
	public static double beta = 0.5;



	static{
		String[] strArr=StaticMethod.setDataBase(StaticMethod.DbSystem.MYSQL,ip,dbName);
		driver=strArr[0];
		con=strArr[1];
	}
}
