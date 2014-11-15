package jatx.networkingaudioserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if (email==null||email.equals("")||password==null||password.equals("")) {
			out.println("Wrong email or password");
			return;
		}
		
		try {
			Connection connect = MyDBConnect.dbConnect();
			
			Statement statement = connect.createStatement();
    		statement.execute("CREATE TABLE IF NOT EXISTS `users` ("
    				+ "`email` varchar(100) NOT NULL PRIMARY KEY, "
    				+ "`password` varchar(100) NOT NULL, "
    				+ "`confirm` varchar(100) NOT NULL,"
    				+ "`user_id` varchar(100) NOT NULL) "
    				+ "ENGINE=InnoDB DEFAULT CHARSET=utf8");
    		
    		ResultSet rs0 = 
    					statement.executeQuery("SELECT COUNT(*) as `users_count` "
    							+ "FROM `users` WHERE `confirm`='done'");
    		rs0.next();
    		int usersCountTotal = rs0.getInt("users_count");
    		if (usersCountTotal>=MyDBConnect.USERS_LIMIT) {
    			out.println("Too many registered users on server. Try to install your own");
    			return;
    		}
    		
    		PreparedStatement checkEmailPS = 
    				connect.prepareStatement("SELECT COUNT(*) AS `users_count` "
    						+ "FROM `users` WHERE `email`=?");
    		checkEmailPS.setString(1, email);
    		ResultSet rs = checkEmailPS.executeQuery();
    		if (rs.next()) {
    			Integer usersCount = rs.getInt("users_count");
    			if (usersCount>0) {
    				out.println("This email already registered");
    				return;
    			}
    		}
    		
    		String confirm = RandomStringUtils.randomAlphanumeric(32);
    		
    		PreparedStatement registerUser = 
    				connect.prepareStatement("INSERT INTO `users` (`email`,`password`,`confirm`,`user_id`) "
    						+ "VALUES (?,?,?,?)");
    		registerUser.setString(1, email);
    		registerUser.setString(2, password);
    		registerUser.setString(3, confirm);
    		registerUser.setString(4, DigestUtils.md5Hex(email));
    		registerUser.executeUpdate();
    		
    		Runtime runtime = Runtime.getRuntime();
    		String[] command = {"/common_scripts/sendConfirm",email,confirm};
    		Process process = runtime.exec(command);
			int exitCode = process.waitFor();
			if (exitCode==0) {
				out.println("Check your email for confirmation");
			} else {
				out.println("Error sending email");
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}
