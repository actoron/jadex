<%@ page isErrorPage="true" import="java.io.*" %>
<html>
	<head>
		<title>Ein Fehler ist aufgetreten!</title>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/puzzle/style.css">
	</head>

	<body>
		<h1 class="error">ERROR 404</h1>

		<p>The requested page: "<%= request.getRequestURL() %>"  has not been found.</p>
		<a href="<%= request.getContextPath() %>">Back to main page</a>

	</body>
</html>