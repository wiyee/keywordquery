package main.jdbc;

import main.tools.StaticValue;

import java.sql.*;

/**
 * Created by wiyee on 2018/3/9.
 */
public class OJDBC {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public Connection getConnect() {

        try{
            Class.forName(StaticValue.oracle_driver);
            Connection connection= DriverManager.getConnection(StaticValue.oracle_con, StaticValue.oracle_name, StaticValue.oracle_pwd);
            return connection;
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
                rs.close();
            if(st!=null)
                st.close();
            if(con!=null)
                con.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
