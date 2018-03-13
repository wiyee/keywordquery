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
        String driver="oracle.jdbc.driver.OracleDriver";
        String con="jdbc:oracle:thin:@10.1.18.155:1521:ORCL";
        try{
            Class.forName(driver);
            Connection connection= DriverManager.getConnection(con, "WYY", "wyy");
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
