package jatx.onlineboard;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/fileupload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final String SAVE_DIR = ".."+File.separator+"uploads";
	private static final String HTTP_DIR = "../uploads";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * handles file upload
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/plain;charset=UTF-8");
	    PrintWriter out = response.getWriter();
	    
	    String user_id = request.getParameter("user_id");
        String password = request.getParameter("password");
        String room = request.getParameter("room");
        String msg = request.getParameter("msg");
        String fileName = request.getParameter("file_name");
        
        if (user_id==null||password==null||room==null||msg==null||fileName==null) {
        	out.println("error");
        	out.println("Wrong param");
        	return;
        }
        
        fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	String dt = dateFormat.format(new Date());
        fileName = dt+"_"+fileName;
        
        Integer room_id = Integer.parseInt(room);
        
        try {
			if (!Authorization.checkRoom(user_id, password, room_id)) {
				out.println("error: not authorized");
				return;
			}
		} catch (ClassNotFoundException | SQLException e) {
			out.println("error");
			e.printStackTrace(out);
			return;
		}
    	
        // gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR + File.separator + room;
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
                 
        try {    	
        	Part part = request.getPart("file");
           
            part.write(savePath + File.separator + fileName);
            
            String mime = Files.probeContentType(Paths.get(savePath + File.separator + fileName));
            
            StringBuilder fileLink = new StringBuilder();
            
            if (mime!=null&&mime.matches("^image/(gif|jpeg|pjpeg|png).*$")) {
            	fileLink.append("<br><img class=\"msgImg\"");
            	fileLink.append(" src=\"");
            	fileLink.append(HTTP_DIR+"/"+room+"/"+fileName);
            	fileLink.append("\"></img>");
            } else {
            	fileLink.append("<br><b>File:&nbsp;</b>");
            	fileLink.append("<a href=\"");
            	fileLink.append(HTTP_DIR+"/"+room+"/"+fileName);
            	fileLink.append("\" target=\"_blank\">"+fileName+"</a>");
            }
        	
			Board.sendMsg(user_id, room_id, 
					new String(msg.getBytes("ISO-8859-1"), "UTF-8")+fileLink.toString());
		} catch (Exception e) {
			out.println("error");
			e.printStackTrace(out);
		}
        
        out.println("ok");
    }
}
