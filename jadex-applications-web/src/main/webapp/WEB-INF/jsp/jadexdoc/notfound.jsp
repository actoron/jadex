<%
	String	model	= (String)request.getSession().getAttribute("model");
%>
<html>
	<head>
		<title>Jadexdoc: Error</title>
	</head>
	<body>
		<h1>Jadexdoc Problem</h1>
		<%= model %> could not be found.
	</body>	
</html>
