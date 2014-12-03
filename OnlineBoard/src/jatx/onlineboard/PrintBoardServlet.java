package jatx.onlineboard;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PrintBoardServlet
 */
@WebServlet("/printboard")
public class PrintBoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintBoardServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String user_id = request.getParameter("user_id");
        String password = request.getParameter("password");
        String room = request.getParameter("room");
        
        if (user_id==null||password==null||room==null) {
        	out.println("Wrong param");
        	return;
        }
        
        String my = request.getParameter("my");
        boolean myBoard = (my!=null&&my.equals("1"));
        
        Integer room_id = Integer.parseInt(room);
        
        try {
			if (Authorization.checkRoom(user_id, password, room_id)) {
				out.println(Board.printBlackBoard(user_id, room_id, myBoard));
			} else {
				out.println("Wrong authorization");
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

}
