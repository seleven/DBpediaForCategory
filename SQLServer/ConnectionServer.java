package SQLServer;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @function Connection SQL Server 
 * @author Administrator
 * @data 2015年5月27日
 */
public class ConnectionServer {
	
	 /* Load JDBC driver */
	private static String  driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	/* Connection SQL Server and Database(Classification) */
	private static String URL = "jdbc:sqlserver://localhost:1433; DatabaseName=Classification"; 
	
	/* User name and password of SQL Server  */
	private static String userName = "sa"; 
	private static String userPwd = "30"; 
	
	/* Connection of SQL Server */
	public static Connection conn=null;
	static{
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(URL, userName, userPwd);
			/* Print out tips of connect success */
			System.out.println( "Database Connection Successful! \n"+"--------------------------------------"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return Classification Connection
	 */
	public static Connection getConnection(){
		return conn;
	}
	
	public static Connection getConn(){
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(URL, userName, userPwd);
			/* Print out tips of connect success */
			System.out.println( "Database Connection again! \n"+"-------------------------------"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
