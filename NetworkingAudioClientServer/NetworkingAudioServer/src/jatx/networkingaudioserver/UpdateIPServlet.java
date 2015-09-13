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

/**
 * Servlet implementation class UpdateIPServlet
 */
@WebServlet("/updateip")
public class UpdateIPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateIPServlet() {
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
        if (email==null||password==null) {
        	out.println("Email and password required");
        	return;
        }
        
        String ip = request.getRemoteAddr();
        
        try {
        	Connection connect = MyDBConnect.dbConnect();
        	
        	PreparedStatement getUser =
        			connect.prepareStatement("SELECT `user_id`,`confirm` FROM `users` "
        					+ "WHERE `email`=? AND `password`=?");
        	getUser.setString(1, email);
        	getUser.setString(2, password);
        	ResultSet rs1 = getUser.executeQuery();
        	String user_id;
        	if (rs1.next()) {
        		user_id = rs1.getString("user_id");
        		String confirm = rs1.getString("confirm");
        		if (!confirm.equals("done")) {
        			out.println("Your email not confirmed yet");
        			return;
        		}
        	} else {
        		out.println("Wrong email or password");
        		return;
        	}
        	
        	Statement statement = connect.createStatement();
    		statement.execute("CREATE TABLE IF NOT EXISTS `userinfo` ("
    				+ "`email` varchar(100) NOT NULL PRIMARY KEY, "
    				+ "`user_id` varchar(100) NOT NULL, "
    				+ "`ip` varchar(100) NOT NULL, "
    				+ "`last_update` TIMESTAMP NOT NULL) "
    				+ "ENGINE=InnoDB DEFAULT CHARSET=utf8");
        	
    		PreparedStatement updateIP = 
    				connect.prepareStatement("REPLACE INTO `userinfo` (`email`,`user_id`,`ip`,`last_update`) "
    						+ "VALUES (?,?,?,now())");
    		updateIP.setString(1, email);
    		updateIP.setString(2, user_id);
    		updateIP.setString(3, ip);    		
    		updateIP.executeUpdate();
    		
    		connect.close();
    		
    		out.println("user_id:"+user_id);
        } catch (Exception e) {
        	e.printStackTrace(out);
        }
	}

}
