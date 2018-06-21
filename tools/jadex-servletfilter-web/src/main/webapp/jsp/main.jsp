<jsp:include page="header.jsp">
	<jsp:param name="title" value="Info" />	
</jsp:include>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<% 
	if(session==null || session.getAttribute("authenticated")==null)
	{ %>
		Please log in to manage the web proxy. If you use the application the first time you can login with<br/>
		User name: admin<br/>
		Password: admin<br/>
		Be sure to change the password via the 'manage users' option.
<% 	}
%>

<jsp:include page="footer.jsp" flush="true"/>
