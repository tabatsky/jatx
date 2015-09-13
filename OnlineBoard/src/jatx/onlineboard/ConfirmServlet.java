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

/**
 * Servlet implementation class ConfirmServlet
 */
@WebServlet("/confirm")
public class ConfirmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String user_id = request.getParameter("user_id");
        String password = request.getParameter("password");
        String room = request.getParameter("room");
        String action = request.getParameter("action");
        
        if (user_id==null||password==null||room==null||action==null) {
        	out.println("Wrong param");
        	return;
        }
        
        Integer room_id = Integer.parseInt(room);
        
        try {
        	if (!Authorization.checkRoom(user_id, password, room_id)) {
        		out.println("Wrong authorization");
        		return;
        	}
        	
        	Connection connect = MyDBConnect.dbConnect();        	
        	
        	PreparedStatement ps1 = 
        			connect.prepareStatement("SELECT `user_id1`,`confirmed` FROM `rooms` "
        					+ "WHERE `room_id`=? AND `user_id2`=?");
        	ps1.setInt(1, room_id);
        	ps1.setString(2, user_id);
        	ResultSet rs1 = ps1.executeQuery();
        	if (!rs1.next()) {
        		connect.close();
        		out.println("Room not found");
        		return;
        	}
        	String target_user_id = rs1.getString("user_id1");
        	String confirmed = rs1.getString("confirmed");
        	if (confirmed.equals("yes")) {
        		connect.close();
        		out.println("Room already confirmed");
        		return;
        	}
        	
        	PreparedStatement ps2 = 
        			connect.prepareStatement("SELECT `email`,`username` FROM `users` "
        					+ "WHERE `user_id`=?");
        	ps2.setString(1, user_id);
        	ResultSet rs2 = ps2.executeQuery();
        	if (!rs2.next()) {
        		connect.close();
        		out.println("error 1");
        	}
        	String my_email = rs2.getString("email");
        	String my_username = rs2.getString("username");
        	
        	PreparedStatement ps3 = 
        			connect.prepareStatement("SELECT `email` FROM `users` "
        					+ "WHERE `user_id`=?");
        	ps3.setString(1, target_user_id);
        	ResultSet rs3 = ps3.executeQuery();
        	if (!rs3.next()) {
        		connect.close();
        		out.println("error 2");
        	}
        	String target_email = rs3.getString("email");
        	
        	if (action.equals("confirm")) {
        		PreparedStatement ps = 
        				connect.prepareStatement("UPDATE `rooms` SET `confirmed`='yes' "
        						+ "WHERE `room_id`=?");
        		ps.setInt(1, room_id);
        		ps.executeUpdate();
        		out.println(MailHelper.sendConfirmNotify(my_email, 
        				my_username, target_email, true));
        	} else if (action.equals("decline")) {
        		PreparedStatement ps = 
        				connect.prepareStatement("DELETE FROM `rooms` WHERE `room_id`=?");
        		ps.setInt(1, room_id);
        		ps.executeUpdate();
        		out.println(MailHelper.sendConfirmNotify(my_email, 
        				my_username, target_email, false));
        	}
        	
        } catch (Exception e) {
        	out.println("error");
        	e.printStackTrace(out);
        }
	}
}
