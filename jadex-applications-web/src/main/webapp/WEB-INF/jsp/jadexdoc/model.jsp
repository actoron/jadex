<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%
	IModelInfo	model	= (IModelInfo)request.getAttribute("model");
%>
<html>
	<head>
		<title>Jadexdoc: <%= model.getFullName() %></title>
	</head>
	<body>
		<%= model.getDescription() %>
	</body>	
</html>
