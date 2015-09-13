package jatx.onlineboard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class InitDBServlet
 */
@WebServlet("/init")
public class InitDBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitDBServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Connection connect = MyDBConnect.dbConnect();
			
			Statement statement = connect.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS `users` ("
    				+ "`username` varchar(100) NOT NULL, "
    				+ "`password` varchar(100) NOT NULL, "
    				+ "`user_id` varchar(100) NOT NULL, "
    				+ "`email` varchar(100) NOT NULL DEFAULT '', "
    				+  "PRIMARY KEY (`email`)"
    				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
			
			statement.execute("CREATE TABLE IF NOT EXISTS `rooms` ("
    				+ "`user_id1` varchar(100) NOT NULL, "
    				+ "`user_id2` varchar(100) NOT NULL, "
    				+ "`room_id` INT PRIMARY KEY AUTO_INCREMENT,"
  					+ "`confirmed` varchar(100) DEFAULT 'no', "
    				+ "UNIQUE KEY `room` (`user_id1`,`user_id2`)) "
    				+ "ENGINE=InnoDB DEFAULT CHARSET=utf8");
			
			statement.execute("CREATE TABLE IF NOT EXISTS `messages` ("
					+ "`user_id` varchar(100) NOT NULL, "
					+ "`msg` TEXT NOT NULL, "
					+ "`room_id` INT NOT NULL, "
					+ "`time` TIMESTAMP NOT NULL) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8");
			
			
			statement.execute("CREATE TABLE IF NOT EXISTS `blackboards` ("
					+ "`user_id` varchar(100) NOT NULL, "
					+ "`boardtext` TEXT NOT NULL, "
					+ "`room_id` INT NOT NULL, "
					+ "`time` TIMESTAMP NOT NULL) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8");
			
			
			connect.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}
