package com.mandou.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;

import com.mysql.jdbc.MysqlParameterMetadata;

import httpTest.ResolveLoanApply;

public class JDBCUtil {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);

	//连接数据库
	public static Connection getconn() {
		String driver = PropertiesUtil.getValueBykey("database.driverClassName");
		String url = PropertiesUtil.getValueBykey("database.url");
		String username = PropertiesUtil.getValueBykey("database.userName");
		String password = PropertiesUtil.getValueBykey("database.password");
		Connection conn=null;
		
		try {
			//加载MySQL的驱动
			Class.forName(driver);
			//连接数据库
			conn=DriverManager.getConnection(url,username,password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
		
	}
	
	//insert
	public static int setInsert(String sql) {
		int i=0;
		
		Connection conn=getconn();
		try {
			//预编译
			PreparedStatement pstmt=conn.prepareStatement(sql);
			i=pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
		
	}
	
	//select
	@SuppressWarnings("null")
	public static List<String> getSelect(String sql,String colname) {
		ResultSet rs=null;
		List<String> a = null;
		Connection conn=getconn();
		int i=0;
		try {
			//预编译
			PreparedStatement pstmt=conn.prepareStatement(sql);
			log.info("sql:"+sql);
			rs=pstmt.executeQuery();
			int col=rs.getMetaData().getColumnCount();
			a = new ArrayList<String>();
			while(rs.next()) {
				log.info("param: "+rs.getString(colname)+"\t");
				a.add(rs.getString(colname));

				i=i+1;
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
	//update
	public static int setUpdate(String sql) {
		int i=0;
		Connection conn=getconn();
		try {
			PreparedStatement pstmt=conn.prepareStatement(sql);
			i=pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
		
	}
	
	//delete
	public static int setDelete(String sql) {
		Connection conn=getconn();
		int i=0;
		try {
			PreparedStatement pstmt=conn.prepareStatement(sql);
			log.info("sql:"+sql);
			i=pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
		
	}
	
	public static void main(String[] args) {
		String no="TZ201604010001";
				
		JDBCUtil.getSelect("select * from `order` where `no`="+"'"+no+"'","state");
		
	}
}
