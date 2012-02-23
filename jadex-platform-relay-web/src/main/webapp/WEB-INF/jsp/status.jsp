<jsp:include page="header.jsp" flush="true"/>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="jadex.base.relay.*" %>
<%@ page import="java.util.*" %>

<h2>Connected Platforms</h2>
<table>
	<tr>
		<th>Platform (Host)</th>
		<th>Connected Since</th>
		<th>Scheme</th>
		<th>Received Messages</th>
		<th>Avg. Transfer Rate</th>
	</tr>
	<%
		Map<Object, PlatformInfo>	platforms	= (Map<Object, PlatformInfo>)request.getAttribute("platforms");
		// Fetch array to avoid concurrency problems
		PlatformInfo[]	infos	= platforms.values().toArray(new PlatformInfo[0]);
		for(int i=0; i<infos.length; i++)
		{%>
			<tr class="<%= i%2==0 ? "even" : "odd" %>">
				<td>
					<%= infos[i].getId() %> (<%= infos[i].getHost() %>)</td>
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
