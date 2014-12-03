package jatx.onlineboard;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;

public class Board {
	
	public static void sendMsg(String user_id, Integer room_id, String msg)
			throws ClassNotFoundException, SQLException {
		Connection connect = MyDBConnect.dbConnect();
		
		PreparedStatement ps = 
				connect.prepareStatement("INSERT INTO `messages` (`user_id`,`msg`,`room_id`,`time`) "
						+ "VALUES (?,?,?,now())");
		ps.setString(1, user_id);
		ps.setString(2, msg);
		ps.setInt(3, room_id);
		ps.executeUpdate();
		
		connect.close();
	}
	
	public static String printAllMsg(Integer room_id, Integer limit) throws ClassNotFoundException, SQLException {
		StringBuilder sb = new StringBuilder();
		
		Connection connect = MyDBConnect.dbConnect();
		PreparedStatement ps = 
				connect.prepareStatement("SELECT `username`,`msg`,`time` FROM "
						+ "`users` INNER JOIN `messages` "
						+ "ON (users.user_id=messages.user_id) "
						+ "WHERE messages.room_id=? ORDER BY "
						+ "messages.time DESC LIMIT ?");
		ps.setInt(1, room_id);
		ps.setInt(2, limit);
		ResultSet rs = ps.executeQuery();
		
		CleanerProperties props = new CleanerProperties();
		props.setOmitHtmlEnvelope(true);
		props.setOmitXmlDeclaration(true);
		HtmlCleaner cleaner = new HtmlCleaner(props);
		PrettyHtmlSerializer serializer = new PrettyHtmlSerializer(props); 
		
		while (rs.next()) {
			String username = rs.getString("username");
			String msg = rs.getString("msg");
			
			msg = msg.replace("\n", "<br>");
					
			TagNode node = cleaner.clean(msg);
			StringWriter sw = new StringWriter();
			try {
				serializer.write(node, sw, "UTF-8");
				msg = sw.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			sb.append("<p><b>");
			sb.append(username);
			sb.append("</b>:&nbsp;&nbsp;");
			sb.append(msg);
			sb.append("</p>");
		}
		
		connect.close();
		
		return sb.toString();
	}
	
	public static void submitBlackBoard(String user_id, Integer room_id, String boardtext) 
			throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		Connection connect = MyDBConnect.dbConnect();
		
		PreparedStatement ps = 
				connect.prepareStatement("INSERT INTO `blackboards` (`user_id`,`boardtext`,`room_id`,`time`) "
						+ "VALUES (?,?,?,now())");
		ps.setString(1, user_id);
		ps.setString(2, boardtext);
		ps.setInt(3, room_id);
		ps.executeUpdate();
		
		connect.close();
	}
	
	public static String printBlackBoard(String user_id, Integer room_id, boolean myBoard)
			throws SQLException, ClassNotFoundException {
		String result = "";
		
		String query = "SELECT * FROM `blackboards` "
				+ "WHERE `room_id`=? AND `user_id`<>? "
				+ "ORDER BY `time` DESC LIMIT 1";
		if (myBoard) {
			query = "SELECT * FROM `blackboards` "
				+ "WHERE `room_id`=? AND `user_id`=? "
				+ "ORDER BY `time` DESC LIMIT 1";
		}
		
		Connection connect = MyDBConnect.dbConnect();
		PreparedStatement ps = 
				connect.prepareStatement(query);
		ps.setInt(1, room_id);
		ps.setString(2, user_id);
		ResultSet rs = ps.executeQuery();
		
		if (rs.next()) {
			result = rs.getString("boardtext");
		}
		
		connect.close();
		
		return result;
	}
	
	public static List<String> getRoomsByUser(String user_id, String password) 
			throws ClassNotFoundException, SQLException {
		List<String> list = new ArrayList<String>();
		
		Connection connect = MyDBConnect.dbConnect();
		
		String query = 
				"SELECT "
				+ "u1.username1 AS `username1`, " 
				+ "u1.user_id1 AS `user_id1`, " 
				+ "u2.username2 AS `username2`, " 
				+ "u2.user_id2 AS `user_id2`, "
				+ "u1.room_id AS `room_id`, "
				+ "u1.confirmed AS `confirmed` "
				+ "FROM "
				+ "((SELECT users.username AS `username1`, "
				+ "users.user_id AS `user_id1`, rooms.room_id AS `room_id`, "
				+ "rooms.confirmed AS `confirmed` "
				+ "FROM users LEFT JOIN rooms ON users.user_id = rooms.user_id1) AS `u1` "
				+ "JOIN " 
				+ "(SELECT users.username AS `username2`, "
				+ "users.user_id AS `user_id2`, rooms.room_id AS `room_id` "
				+ "FROM users LEFT JOIN rooms ON users.user_id = rooms.user_id2) AS `u2` "
				+ "ON u1.room_id = u2.room_id) "
				+ "WHERE (`user_id1`=? OR `user_id2`=?) AND `confirmed`='yes'";
		
		PreparedStatement ps = 
				connect.prepareStatement(query);
		ps.setString(1, user_id);
		ps.setString(2, user_id);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String username1 = rs.getString("username1");
			String username2 = rs.getString("username2");
			String user_id1 = rs.getString("user_id1");
			String user_id2 = rs.getString("user_id2");
			Integer room_id = rs.getInt("room_id");
			
			String my_id = "";
			String his_name = "";
			
			if (user_id.equals(user_id1)) {
				his_name = username2;
				my_id = user_id1;
			} else {
				his_name = username1;
				my_id = user_id2;
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("<a href=\"./?");
			sb.append("user_id=");
			sb.append(my_id);
			sb.append("&password=");
			sb.append(password);
			sb.append("&room=");
			sb.append(room_id.toString());
			sb.append("\">");
			sb.append(his_name);
			sb.append("</a>");
			
			list.add(sb.toString());
		}
		
		connect.close();
		
		return list;
	}
}
