package SQLServer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
/**
 * @function Multiple thread in import WordsOnly data into Database
 * @author Administrator
 * @data 2015年5月31日
 */
public class InsertDataThread extends Thread {

	private String tableName = null;
	private SQLforWordsIntoDatabase sqlOperate = null;
	private BufferedReader br = null;
	//-----------------------------------------------------------------------------//
	/* Load JDBC driver */
	private static String  driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	/* Connection SQL Server and Database(Classification) */
	private static String URL = "jdbc:sqlserver://localhost:1433; DatabaseName=Classification"; 
	
	/* User name and password of SQL Server  */
	private static String userName = "sa"; 
	private static String userPwd = "30"; 
	
	/* Connection of SQL Server */
	public Connection conn=null;
	
	//----------------------------------------------------------------------------//
	public InsertDataThread(String tableName, BufferedReader br) {
		super();
		this.tableName = tableName;
		this.br = br;
		
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(URL, userName, userPwd);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.sqlOperate = new SQLforWordsIntoDatabase(conn);
		
		/* Print out tips of connect success */
		System.out.println( tableName + "-----Connection Successful! "); 
	}
	public void run(){
		
		long letsgo=Calendar.getInstance().getTimeInMillis();
		System.out.println(tableName + "Start!");
		
		String line = null;
		try {
			try {
				while((line = br.readLine()) != null){
					String[] str = line.split("\\|"); // Split line use | 
					if(str.length == 2){
						sqlOperate.addData(tableName, str[0], str[1]);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long terminal=Calendar.getInstance().getTimeInMillis();
		System.out.println("=======End" + tableName + "---SUCCESSFUL!-用时："+(double)(terminal-letsgo)/1000+"（秒）");
	}
	
}
