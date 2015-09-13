<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Online Board Menu</title>
<style>
  body {text-align:center}
</style>
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<%
String user_id = request.getParameter("user_id");
String password = request.getParameter("password");
%>
<script type="text/javascript">
$(document).ready(function() {
	$('#create_room').click(function(){
		var email = $('#email').val();
		var url = 'roominvite?user_id=<%=user_id%>'
				 + '&password=<%=password%>'
				 + '&email='+email;
	    $.get(url, function(data){
	    	alert(data);
	    });
	});
});
</script>
</head>
<body>
<%@ page import="jatx.onlineboard.Authorization"%>
<%@ page import="jatx.onlineboard.Board"%>
<%@ page import="java.util.List"%>
<%
if (user_id==null||password==null||!Authorization.checkPassword(user_id, password)) {
%>
<h1>Wrong authorization</h1>
<%	
} else {
%>
<h1>Create Room With User</h1>
<label for="email">Target e-mail:</label>
<input type="text" id="email" />
<input type="submit" id="create_room" value="Create Room" />
<h1>My Rooms:</h1><%
	List<String> links = Board.getRoomsByUser(user_id, password); 
	for (String link: links) {
	%>
		<h2><%=link%></h2>
	<%
	}
}
%>
</body>
</html>