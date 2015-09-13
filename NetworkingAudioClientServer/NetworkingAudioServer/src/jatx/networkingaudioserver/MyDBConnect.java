package jatx.networkingaudioserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MyDBConnect {
	public static final int USERS_LIMIT = 100;
	
	private static final String user = "";
	private static final String password = "";
	private static final String db = "";
	
	public static Connection dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/" + db
		              + "?user="+user+"&password="+password);
		return connect;
	}
}
