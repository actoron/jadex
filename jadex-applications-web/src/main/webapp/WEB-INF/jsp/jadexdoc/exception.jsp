<%
	Exception	exception	= (Exception)request.getSession().getAttribute("exception");
%>
<html>
	<head>
		<title>Jadexdoc: Error</title>
	</head>
	<body>
		<h1>Jadexdoc Problem</h1>
		<%= exception %>
	</body>	
</html>
