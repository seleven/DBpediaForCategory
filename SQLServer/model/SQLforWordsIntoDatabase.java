package SQLServer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import SQLServer.ConnectionServer;
import SQLServer.SubjectObject;

public class SQLforWordsIntoDatabase {
	private Connection conn = null;
	public SQLforWordsIntoDatabase(){
		this.conn = ConnectionServer.getConnection();
	}
	
	public SQLforWordsIntoDatabase(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * close the connection of SQL Server database
	 */
	public void connClose(){
		try {
			conn.close();
			System.out.println("conn closed.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param tableName table name in Classification Database you want insert data into
	 * @param so An object of SubjectObject Class
	 * @throws SQLException
	 */
	public void addData(String tableName,SubjectObject so) throws SQLException{
		String sql = "insert into " + tableName + "(Subject,Object)" +"values(?,?)";
		PreparedStatement ptmt = conn.prepareStatement(sql);
		/* Prepare the parameter */
		ptmt.setString(1, so.getSubject());
		ptmt.setString(2, so.getObject());
		/* Execute sql */
		ptmt.execute();
	}

	/**
	 * @param tableName tableName table name in Classification Database you want insert data into
	 * @param subject subject of properties data
	 * @param object object of properties data
	 * @throws SQLException
	 */
	public void addData(String tableName,String subject,String object) throws SQLException {
		String sql = "insert into " + tableName + "(Subject,Object)" +"values(?,?)";
		PreparedStatement ptmt = conn.prepareStatement(sql);
		/* Prepare the parameter */
		ptmt.setString(1, subject);
		ptmt.setString(2, object);
		/* Execute sql */
		ptmt.execute();
	}
	
	/**
	 * @param tableName tableName table name in Classification Database you want insert data into
	 * @param subject subject of properties data
	 * @param object object of properties data
	 * @param br BufferedReader
	 * @throws SQLException
	 */
	public void addData(String tableName, InputStreamReader isr) 
			throws SQLException,IOException {
		if(conn.isClosed()) this.conn = ConnectionServer.getConn();
		String sql = "insert into " + tableName + "(Subject,Object)" +"values(?,?)";
		PreparedStatement ptmt = conn.prepareStatement(sql);
		
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		int count = 0;
		while((line = br.readLine()) != null){
			conn.setAutoCommit(false);
			String[] str = line.split("\\|"); // Split line use | 
			if(str.length == 2){
				/* Prepare the parameter */
				ptmt.setString(1, str[0]);
				ptmt.setString(2, str[1]);
				ptmt.addBatch();
				count++;
				if(count % 100000 == 0){
					ptmt.executeBatch();
					conn.commit();
				}
			}
		}
		
		/* Execute sql */
		ptmt.executeBatch();
		conn.commit();
		ptmt.close();
		conn.close();
	}
	
	/**
	 * @return list of SubjectObject
	 * @throws SQLException 
	 */
	public List<SubjectObject> query(String tableName) throws SQLException{
		Connection conn = ConnectionServer.getConnection();
		if(conn.isValid(5)) System.out.println("Database Connection success.");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select Subject,Object from " + tableName);
		
		ArrayList<SubjectObject> arrList = new ArrayList<SubjectObject>();
		SubjectObject so = null;
		while(rs.next()){
			so = new SubjectObject(rs.getString("Subject"),rs.getString("Object"));
			arrList.add(so);
		}
		return arrList;
	}
}
