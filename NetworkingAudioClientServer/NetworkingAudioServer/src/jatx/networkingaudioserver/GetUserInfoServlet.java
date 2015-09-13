package jatx.networkingaudioserver;

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

/**
 * Servlet implementation class GetUserInfoServlet
 */
@WebServlet("/getuserinfo")
public class GetUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserInfoServlet() {
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
        String user_id = request.getParameter("user_id");

        try {
        	Connection connect = MyDBConnect.dbConnect();
        	
        	String query = "SELECT `email`,`user_id`,`ip`, "
				+ "(UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(last_update)) AS `time_diff` FROM `userinfo` ";
        	
        	PreparedStatement getInfo;
        	
        	if (email!=null) {
        		query += "WHERE `email`=?";
        		getInfo = connect.prepareStatement(query);
        		getInfo.setString(1, email);
        	} else if (user_id!=null) {
        		query += "WHERE `user_id`=?";
        		getInfo = connect.prepareStatement(query);
        		getInfo.setString(1, user_id);
        	} else {
        		out.println("Error: email or user_id required");
        		return;
        	}
        	
        	ResultSet rs = getInfo.executeQuery();
        	if (rs.next()) {
        		email = rs.getString("email");
        		user_id = rs.getString("user_id");
        		String ip = rs.getString("ip");
        		Integer time_diff = rs.getInt("time_diff");
        		if (time_diff>60*3) {
        			ip = "offline";
        		}
        		out.println("email:"+email);
        		out.println("user_id:"+user_id);
        		out.println("ip:"+ip);
        	} else {
        		out.println("Error: not found");
        	}
        	
        	connect.close();
        } catch (Exception e) {
        	e.printStackTrace(out);
        }
	}
}
