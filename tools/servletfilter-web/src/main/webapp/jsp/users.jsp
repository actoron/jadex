<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="Manage Users" />	
</jsp:include>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%
	Map.Entry<String, String>[] users = (Map.Entry<String, String>[])request.getAttribute("users");
%>

	<table>
		<th>User Name</th>
		<th>Password</th>
		<th>Actions</th>
		<% 
			for(Map.Entry<String, String> user: users)
			{ %>
				<tr>
					<td><%= user.getKey() %> </td>
					<td><%= user.getValue() %></td>
					<td> 
						<form name="input" action="addUser" method="get">
					<% 	if(!user.getKey().equals("admin"))
						{	%>
							<a href="removeUser?user=" <%= user.getKey() %>">Remove</a>
					<%	} %>
							<input type="hidden" name="user" value="<%= user.getKey() %>"/>
							<input type="text" name="pass"/>
							<input type="submit" value="Change Pass"/>
						</form>
					</td>
				</tr>
		<% 	} %>
	</table>

	<h2>Add a User</h2>
	<form name="input" action="addUser" method="get">
		<table>
			<tr>
				<td>User name:</td>
				<td><input type="text" name="user"/></td>
			</tr>
			<tr>
				<td>User password:</td>
				<td><input type="text" name="pass"/></td>
			</tr>
			<tr>
				<td><input type="submit" value="Add"/>
				</td>
			</tr>
		</table>
	</form>
			
<jsp:include page="footer.jsp" flush="true"/>
