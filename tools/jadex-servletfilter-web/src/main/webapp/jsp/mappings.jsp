
<jsp:include page="header.jsp">
	<jsp:param name="title" value="Manage Mappings" />	
</jsp:include>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="jadex.ForwardFilter.ForwardInfo"%>

<%
	ForwardInfo[] infos = (ForwardInfo[])request.getAttribute("forwardinfos");
%>

<!-- <h2>Current Mappings</h2> -->

<table>
	<tr>
		<th>&nbsp;</th>
		<th>Local Name</th>
		<th>Remote Address</th>
		<th>Timestamp</th>
		<th>Actions</th>
	</tr>
	
	<%
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		if(infos!=null)
		{
			for(int i=0; i<infos.length; i++)
			{%>
				<tr class="<%= i%2==0 ? "even" : "odd" %>" title="<%= infos[i].toString() %>">
					<td>
						<%= i+1 %>
					</td>
					<td>
						<%= infos[i].getAppPath() %>
					</td>
					<td>
						<%= infos[i].getForwardPath() %>
					<td>
						<%= df.format(new Date(infos[i].getTime()))  %></td>
					<td>
						<a href="removeMapping?name= <%= infos[i].getAppPath() %> ">Remove</a>
						<a href="refreshMapping?name= <%= infos[i].getAppPath() %> ">Refresh</a>
					</td>
				</tr>
		<%	}
		}
	 %>
	 
	<h2>Add a Mapping</h2>
	<form name="input" action="addMapping" method="get">
		<table>
			<tr>
				<td>Application name:</td>
				<td><input type="text" name="name"/></td>
			</tr>
			<tr>
				<td>Remote server address:</td>
				<td><input type="text" name="target"/></td>
			</tr>
			<tr>
				<td><input type="submit" value="Add"/></td>
			</tr>
		</table>
	</form>
			
	<h2>Leasetime</h2>
	<form name="input" action="setLeasetime" method="get">
		<table>
			<tr>
				<td>Leasetime [mins]:</td>
				<td><input type="text" name="leasetime" value="<%= request.getAttribute("leasetime") %>"/></td>
			</tr>
			<tr>
				<td><input type="submit" value="Set"/></td>
			</tr>
		</table>
	</form>

</table>

<jsp:include page="footer.jsp" flush="true"/>
