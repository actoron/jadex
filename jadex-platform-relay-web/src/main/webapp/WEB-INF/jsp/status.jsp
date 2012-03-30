<jsp:include page="header.jsp" flush="true"/>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="jadex.base.relay.*" %>
<%@ page import="java.util.*" %>

<h2><%= request.getAttribute("title") %></h2>
<table>
	<tr>
		<th>Platform</th>
		<th>Host (IP)</th>
		<th>Connected Since</th>
		<th>Scheme</th>
		<th>Received Messages</th>
		<th>Avg. Transfer Rate</th>
	</tr>
	<%
		PlatformInfo[]	infos	= (PlatformInfo[])request.getAttribute("platforms");
		for(int i=0; i<infos.length; i++)
		{%>
			<tr class="<%= i%2==0 ? "even" : "odd" %>">
				<td>
					<%= infos[i].getId() %> </td>
				<td>
					<%= infos[i].getHostName() %> (<%= infos[i].getHostIP() %>)</td>
				<td>
					<%= infos[i].getConnectTime() %></td>
				<td>
					<%= infos[i].getScheme() %></td>
				<td class="number">
					<%= infos[i].getMessageCount() %> (<%= infos[i].getByteCount() %>)</td>
				<td class="number">
					<%= infos[i].getTransferRate() %></td>
			</tr>
	<%	} %>
</table>

<jsp:include page="footer.jsp" flush="true"/>
