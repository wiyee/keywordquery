package main.jdbc;

import main.tools.StaticValue;

import java.sql.*;

public class JDBC {

	 private Connection con;
	 private Statement st;
	 private ResultSet rs;

	public Connection getConnect() {
		try{
			Class.forName(StaticValue.driver);
			con= DriverManager.getConnection(StaticValue.con, StaticValue.name, StaticValue.pwd);
			return con;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Statement getStatement() {
		try {
			st=getConnect().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return st;
	}

	public void execute(String sql) {
		try {
			getStatement().executeUpdate(sql);
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	public ResultSet executeQuery(String sql) {
		try {
			rs=getStatement().executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		try {
			if(rs!=null)
			{
				rs.close();
			}
			if(st!=null)
			{
				st.close();
			}
			if(con!=null)
			{
				con.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
