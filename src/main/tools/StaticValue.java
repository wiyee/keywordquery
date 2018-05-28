package main.tools;

import java.util.Date;

public class StaticValue {

	/**
	 * mysql 数据库
	 */
	public static final String name="root";
	public static final String pwd="123456";
    public static final String ip="10.1.18.70";
    public static final String dbName="yelp_db";
    public static String driver;
    public static String con;

    /**
     * oracle 数据库
     */
    public static final String oracle_name="WYY";
    public static final String oracle_pwd="wyy";
    public static final String oracle_ip="10.1.18.155";
    public static final String oracle_dbName="ORCL";
    public static String oracle_driver;
    public static String oracle_con;

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
		String[] mysqlArr=StaticMethod.setDataBase(StaticMethod.DbSystem.MYSQL,ip,dbName);
		driver=mysqlArr[0];
		con=mysqlArr[1];

		String[] oracleArr = StaticMethod.setDataBase(StaticMethod.DbSystem.ORACLE,oracle_ip,oracle_dbName);
		oracle_driver = oracleArr[0];
		oracle_con = oracleArr[1];
	}
}
