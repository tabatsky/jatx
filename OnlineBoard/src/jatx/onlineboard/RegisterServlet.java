package jatx.onlineboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Servlet implementation class AddUserServlet
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username==null||password==null) {
        	out.println("username and password need");
        	return;
        }
        
		try {
			Connection connect = MyDBConnect.dbConnect();
    		
			PreparedStatement getUser =
    				connect.prepareStatement("SELECT COUNT(*) AS `count` "
    						+ "FROM `users` WHERE "
    						+ "`email`=? AND `password`=?");
    		getUser.setString(1, email);
    		getUser.setString(2, password);
    		ResultSet rs = getUser.executeQuery();
    		if (rs.next()) {
    			Integer count = rs.getInt("count");
    			if (count>0) {
    				out.println("This email already registered");
    			}
    		} else {
    			out.println("error");
    		}
			
			PreparedStatement registerUser = 
    				connect.prepareStatement("INSERT INTO `users` (`email`,`username`,`password`,`user_id`) "
    						+ "VALUES (?,?,?,?)");
			registerUser.setString(1, email);
			registerUser.setString(2, username);
    		registerUser.setString(3, password);
    		String user_id = DigestUtils.md5Hex(email);
    		registerUser.setString(4, user_id);
    		registerUser.executeUpdate();
			
    		out.println(MailHelper.sendLoginLink(email, user_id, password));
    		
    		
			connect.close();
		} catch (Exception e) {
			out.println("error");
			e.printStackTrace(out);
		} 
	}

}
