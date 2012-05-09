<jsp:include page="header.jsp" flush="true"/>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="jadex.base.relay.*" %>
<%@ page import="java.util.*" %>
<%
	PlatformInfo[]	infos	= (PlatformInfo[])request.getAttribute("platforms");
%>

<h2>Platform Connection History</h2>
<%
if(infos.length>0)
{
	StringBuffer markers	= new StringBuffer();
	Set<String> positions	= new HashSet<String>();
	for(int i=0; i<infos.length; i++)
	{
		if(infos[i].getPosition()!=null)
		{
			if(i<9)
			{
				// Add labelled markers for first 1..9 entries
				markers.append("&markers=");
				markers.append("label:");
				markers.append(i+1);
				markers.append("|");
				markers.append(infos[i].getPosition());
			}
			else if(i==9)
			{
				// Add unlabelled markers for each unique position of remaining entries
				markers.append("&markers=");
				markers.append(infos[i].getPosition());
				positions.add(infos[i].getPosition());
			}
			else if(!positions.contains(infos[i].getPosition()))
			{
				// Add unlabelled markers for each unique position of remaining entries
				markers.append("|");
				markers.append(infos[i].getPosition());
				positions.add(infos[i].getPosition());
			}
		}
	}
	if(markers.length()>0)
	{ %>
		<img class="map" src="http://maps.googleapis.com/maps/api/staticmap?size=700x450&sensor=false<%= markers %>"/>
<%	}
} %>

<table>
	<tr>
		<th>&nbsp;</th>
		<th>&nbsp;</th>
		<th>Platform</th>
		<th>Host</th>
		<th>Location</th>
		<th>Last Seen</th>
		<th>First Appeared</th>		
		<th># Seen</th>
	</tr>
	
	<%
		for(int i=0; i<infos.length; i++)
		{%>
			<tr class="<%= i%2==0 ? "even" : "odd" %>" title="<%= infos[i].toString() %>">
				<td>
					<%= i+1 %>
					</td>
				<td>
					<% if(infos[i].getCountryCode()!=null) {%>
						<img src="<%= request.getContextPath() %>/resources/flags/flag-<%= infos[i].getCountryCode() %>.png"/>
					<% } %>
					</td>
				<td>
					<%= infos[i].getId() %> </td>
				<td>
					<%= infos[i].getHostName() %></td>
				<td>
					<%= infos[i].getLocation() %></td>
				<td>
					<%= infos[i].getConnectTime() %></td>
				<td>
					<%= infos[i].getDisconnectTime() %></td>
				<td class="number">
					<%= infos[i].getMessageCount() %>
			</tr>
	<%	} %>
</table>

<jsp:include page="footer.jsp" flush="true"/>
