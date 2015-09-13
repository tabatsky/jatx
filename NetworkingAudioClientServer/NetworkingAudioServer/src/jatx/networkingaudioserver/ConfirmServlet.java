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
		
		String confirm = request.getParameter("confirm");
	
		if (confirm==null||confirm.equals("done")) {
			out.println("No confirmation token");
			return;
		}
		
		try {
			Connection connect = MyDBConnect.dbConnect();
			
			PreparedStatement findToken = 
					connect.prepareStatement("SELECT COUNT(*) AS `count` FROM `users` "
							+ "WHERE `confirm`=?");
			findToken.setString(1, confirm);
			ResultSet rs = findToken.executeQuery();
			if (rs.next()) {
				Integer count = rs.getInt("count");
				if (count==0) {
					out.println("Confirmation token not found");
					return;
				}
			}
			
			PreparedStatement confirmEmail = 
					connect.prepareStatement("UPDATE `users` SET "
							+ "`confirm`='done' WHERE `confirm`=?");
			confirmEmail.setString(1, confirm);
			confirmEmail.executeUpdate();
			
			connect.close();
			
			out.println("E-mail confirmed succesfully");
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}
