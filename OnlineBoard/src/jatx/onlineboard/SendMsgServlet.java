package jatx.onlineboard;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SendMsgServlet
 */
@WebServlet("/sendmsg")
public class SendMsgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMsgServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	processRequest(request, response);
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String user_id = request.getParameter("user_id");
        String password = request.getParameter("password");
        String room = request.getParameter("room");
        String msg = request.getParameter("msg");
        
        if (user_id==null||password==null||room==null||msg==null) {
        	out.println("Wrong param");
        	return;
        }
        
        Integer room_id = Integer.parseInt(room);
        
        try {
			if (Authorization.checkRoom(user_id, password, room_id)) {
				Board.sendMsg(user_id, room_id, msg);
				out.println("ok");
			} else {
				out.println("Wrong authorization");
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}
