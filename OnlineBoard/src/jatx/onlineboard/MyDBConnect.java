package jatx.onlineboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MyDBConnect {	
	public static Connection dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/" + ServerSettings.MYSQL_DB
		              + "?user=" + ServerSettings.MYSQL_USER
		              + "&password=" + ServerSettings.MYSQL_PASSWORD);
		return connect;
	}
}
