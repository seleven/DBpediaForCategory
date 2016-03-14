package SQLServer.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import base.Document;
import SQLServer.ConnectionServer;

public class SQLforWDSNquery {
	// Connect locate SQL Server
	private Connection conn = null;	
	
	public SQLforWDSNquery(){
		conn = ConnectionServer.getConnection();
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
	 * @function get all category name by data set name as the initial openwords of WDSN
	 * @param datasetName
	 * @return all category name of data set which name given
	 */
	public ArrayList<String> getAllCategoryName(String dataSetName) {
		ArrayList<String> al = new ArrayList<String>();
		ResultSet rs = null; 
		
		// prepare query sql
		String sql = "SELECT DISTINCT CategoryName FROM DocumentSetDetail WHERE DataSetName=?";
		try {
			
			// transform sql as prepared statement and set parameter
			PreparedStatement	ps = conn.prepareStatement(sql);
			if(!dataSetName.equals(null)) {	ps.setString(1, dataSetName); }
			
			// execute query
			rs = ps.executeQuery();
			String categoryNameTemp = null;
			while(rs.next()){
				categoryNameTemp = rs.getString("CategoryName");
				al.add(categoryNameTemp);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			connClose();
		}
		return al;
	}
	
	/**
	 * @function get all document by given datasetcode
	 * @param dataSetCode
	 * @return
	 */
	public ArrayList<Document> getAllDocument(String dataSetCode) {
		ArrayList<Document> al = new ArrayList<Document>();
		ResultSet rs = null; 
		
		// prepare query sql
		String sql = "SELECT CategoryName,DocumentID,DocumentName,VectorPath FROM DocumentSetDetail WHERE DataSetName=?";
		try {
			
			// transform sql as prepared statement and set parameter
			PreparedStatement	ps = conn.prepareStatement(sql);
			if(!dataSetCode.equals(null)) {	ps.setString(1, dataSetCode); }
			
			// execute query
			rs = ps.executeQuery();
			String categoryName = null;

			int documentID = 0;
			String documentName = null;
			String vectorPath = null;
			while(rs.next()){
				categoryName = rs.getString("CategoryName");
				documentID = rs.getInt("DocumentID");
				documentName = rs.getString("DocumentName");
				vectorPath = rs.getString("vectorPath");
				al.add(new Document(categoryName,documentID,documentName,vectorPath));
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			connClose();
		}
		return al;
	}
	
	
	/**
	 * @function get all Subject by Object given
	 * @param object
	 * @return all Subject  of Object which name given
	 */
	public ArrayList<String> getSubjects(String tableName, String object) {
		ArrayList<String> al = new ArrayList<String>();
		ResultSet rs = null; 
		
		// prepare query sql
		String sql = "SELECT DISTINCT TOP 1000 Subject FROM " + tableName + " WHERE Object=?";
		try {

			// transform sql as prepared statement and set parameter
			PreparedStatement	ps = conn.prepareStatement(sql);
			if(!object.equals(null)) {	ps.setString(1, object); }
			
			// execute query
			rs = ps.executeQuery();
			String subjectTemp = null;
			while(rs.next()){
				subjectTemp = rs.getString("Subject");
				al.add(subjectTemp);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			connClose();
		}
		return al;
	}
	
	/**
	 * @function get all Subject by Object given
	 * @param object
	 * @return all Subject  of Object which name given
	 */
	public ArrayList<String> getObjects(String tableName, String subject) {
		ArrayList<String> al = new ArrayList<String>();
		ResultSet rs = null; 
		
		// prepare query sql
		String sql = "SELECT DISTINCT TOP 1000 Object FROM " + tableName + " WHERE Subject=?";
		try {

			// transform sql as prepared statement and set parameter
			PreparedStatement	ps = conn.prepareStatement(sql);
			if(!subject.equals(null)) {	ps.setString(1, subject); }
			
			// execute query
			rs = ps.executeQuery();
			String objectTemp = null;
			while(rs.next()){
				objectTemp = rs.getString("Object");
				al.add(objectTemp);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			connClose();
		}
		return al;
	}
}
