<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Online Board</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="ion/ion.sound.min.js"></script>
<%
String user_id = request.getParameter("user_id");
String password = request.getParameter("password");
String room = request.getParameter("room");

if (user_id!=null&&password!=null&&room!=null) {
%>
<script type="text/javascript">
window.user_id = '<%=user_id%>';
window.password = '<%=password%>';
window.room = '<%=room%>';
</script>
<% } %>  
<script type="text/javascript"
   src="http://tabatsky.ru:8080/MathJax/MathJax.js?config=TeX-AMS_HTML-full.js">
</script>
<script type="text/x-mathjax-config">
  MathJax.Hub.Config({
    tex2jax: {
		inlineMath: [["$inlineStart$","$inlineEnd$"]],
		displayMath: [["$jaxStart$","$jaxEnd$"]]
	}
  });
</script>
<script type="text/javascript" src="js/main.js"></script>
</head>
<body>

<div id="msgBoard">
<textarea id="msg">
</textarea>
<input type="button" id="send" value="Send!" />
<input type="button" id="choose_file" value="Choose File" />
<input type="text" id="filename" disabled />
<input type="file" id="my_file"></input>
<div id="msgList">
</div>
</div>

<div id="blackBoard1">
<input type="button" id="show" value="Show!" />
<input type="button" id="subm" value="Submit!" />
<input type="button" id="open_settings" value="Settings" />
<textarea id="myBoard">
</textarea>
</div>

<div id="img1"><p></p></div>

<div id="blackBoard2">
<pre></pre>
</div>

<div id="img2"><p></p></div>

<div id="settings">
<p>
<label for="blackboardBgColor">Blackboard background color:</label>
<input type="color" id="blackboardBgColor" />
</p>
<p>
<label for="blackboardTextColor">Blackboard text color:</label>
<input type="color" id="blackboardTextColor" />
</p>
<p>
<label for="msgBgColor">Interface color 1:</label>
<input type="color" id="msgBgColor" />
</p>
<p>
<label for="msgTextColor">Interface color 2:</label>
<input type="color" id="msgTextColor" />
</p>
<p>
<label for="blackBoardInterval">Blackboard update interval (seconds):</label>
<input type="number" id="blackBoardInterval" value="5" />
</p>
<p>
<label for="msgInterval">Message Board update interval (seconds):</label>
<input type="number" id="msgInterval" value="2" />
</p>
<p>
<label for="msgCount">Show last</label>
<input type="number" id="msgCount" value="30" />
<label>messages</label>
</p>
<p>
<label for="sounds">Sounds:</label>
<input type="checkbox" id="sounds" />
</p>
<input type="button" id="save" value="Save!" />
<input type="button" id="reset" value="Reset!" />
<input type="button" id="cancel" value="Cancel" />
</div>

</body>
</html>