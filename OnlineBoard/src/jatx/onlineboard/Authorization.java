package jatx.onlineboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorization {
	public static boolean checkPassword(String user_id, String password) 
			throws SQLException, ClassNotFoundException {
		boolean result = true;
		
		Connection connect = MyDBConnect.dbConnect();
		
		PreparedStatement passCheck = 
				connect.prepareStatement("SELECT COUNT(*) AS `count` FROM `users` "
						+ "WHERE `user_id`=? AND `password`=?");
		passCheck.setString(1, user_id);
		passCheck.setString(2, password);
		
		ResultSet rs = passCheck.executeQuery();
		if (rs.next()) {
			result = result&&(rs.getInt("count")>0);
		} else {
			result = result&&false;
		}
		
		connect.close();
		
		return result;	
	}
	
	public static boolean checkRoom(String user_id, String password, Integer room_id) 
			throws ClassNotFoundException, SQLException {
		boolean result = true;
		
		Connection connect = MyDBConnect.dbConnect();
		
		PreparedStatement passCheck = 
				connect.prepareStatement("SELECT COUNT(*) AS `count` FROM `users` "
						+ "WHERE `user_id`=? AND `password`=?");
		passCheck.setString(1, user_id);
		passCheck.setString(2, password);
		
		ResultSet rs = passCheck.executeQuery();
		if (rs.next()) {
			result = result&&(rs.getInt("count")>0);
		} else {
			result = result&&false;
		}
		
		PreparedStatement roomCheck = 
				connect.prepareStatement("SELECT COUNT(*) AS `count` FROM `rooms` "
						+ "WHERE (`user_id1`=? OR `user_id2`=?) AND `room_id`=?");
		roomCheck.setString(1, user_id);
		roomCheck.setString(2, user_id);
		roomCheck.setInt(3, room_id);
		
		rs = roomCheck.executeQuery();
		if (rs.next()) {
			result = result&&(rs.getInt("count")>0);
		} else {
			result = result&&false;
		}
		
		connect.close();
		
		return result;		
	}
}
