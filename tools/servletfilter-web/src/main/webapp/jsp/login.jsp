<jsp:include page="header.jsp">
	<jsp:param name="title" value="Login" />	
</jsp:include>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
			
<form name="input" action="login" method="get">
	<table>
		<tr>
			<td>User name:</td>
			<td><input type="text" name="user"/></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="text" name="pass"/></td>
		</tr>
		<tr>
			<td><% 
				if(request.getParameter("next")!=null)
				{ %>
				<input type="hidden" name="next" value="<%= request.getParameter("next") %>"/>
				<%} %>
				<input type="submit" value="Login"/>
			</td>
		</tr>
	</table>
</form>

<jsp:include page="footer.jsp" flush="true"/>
