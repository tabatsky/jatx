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
 * Servlet implementation class RoomInviteServlet
 */
@WebServlet("/roominvite")
public class RoomInviteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoomInviteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String my_user_id = request.getParameter("user_id");
        String my_password = request.getParameter("password");
        String target_email = request.getParameter("email");
        
        if (my_user_id==null||my_password==null||target_email==null) {
        	out.println("Wrong param");
        	return;
        }
        
        try {
        	if (!Authorization.checkPassword(my_user_id, my_password)) {
        		out.println("Wrong authorization");
        	}
        	
        	Connection connect = MyDBConnect.dbConnect();
        	
        	PreparedStatement getMyInfo = 
        			connect.prepareStatement("SELECT `username` FROM `users` "
        					+ "WHERE `user_id`=?");
        	getMyInfo.setString(1, my_user_id);
        	ResultSet rs1 = getMyInfo.executeQuery();
        	if (!rs1.next()) {
        		connect.close();
        		return;
        	}
        	String my_username = rs1.getString("username");
        	
        	PreparedStatement getTargetInfo = 
        			connect.prepareStatement("SELECT `user_id`,`password` FROM `users` "
        					+ "WHERE `email`=?");
        	getTargetInfo.setString(1, target_email);
        	ResultSet rs2 = getTargetInfo.executeQuery();
        	if(!rs2.next()){
        		connect.close();
        		out.println("This e-mail not registered yet");
        		return;
        	}
        	String target_user_id = rs2.getString("user_id");
        	String target_password = rs2.getString("password");
        	
        	PreparedStatement checkRoom = 
        			connect.prepareStatement("SELECT COUNT(*) AS `count` FROM `rooms` WHERE "
        					+ "(`user_id1`=? AND `user_id2`=?) OR "
        					+ "(`user_id1`=? AND `user_id2`=?)");
        	checkRoom.setString(1, my_user_id);
        	checkRoom.setString(2, target_user_id);
        	checkRoom.setString(3, target_user_id);
        	checkRoom.setString(4, my_user_id);
        	ResultSet rs3 = checkRoom.executeQuery();
        	if (rs3.next()&&rs3.getInt("count")>0) {
        		connect.close();
        		out.println("Room with that user already exists");
        		return;
        	}
        	
        	PreparedStatement createRoom = 
        			connect.prepareStatement("INSERT IGNORE INTO `rooms` "
        					+ "(`user_id1`,`user_id2`) "
    						+ "VALUES (?,?)");
        	createRoom.setString(1, my_user_id);
        	createRoom.setString(2, target_user_id);
        	createRoom.executeUpdate();
        	
        	PreparedStatement getRoom = 
    				connect.prepareStatement("SELECT `room_id` FROM `rooms` "
    						+ "WHERE `user_id1`=? AND `user_id2`=?");
    		getRoom.setString(1, my_user_id);
    		getRoom.setString(2, target_user_id);
    		ResultSet rs = getRoom.executeQuery();
    		if (!rs.next()) {
    		    connect.close();
    			out.println("error 1");
    			return;
    		}        	
    		Integer room_id = rs.getInt("room_id");
        	
        	connect.close();
        	
        	out.println(MailHelper.sendInvite(target_email, target_user_id, 
        			target_password, my_username, room_id));
        } catch (Exception e) {
        	out.println("error");
        	e.printStackTrace(out);
        }
	}

}
