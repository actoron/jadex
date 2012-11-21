<%@page import="jadex.bridge.service.types.awareness.AwarenessInfo"%>
<%@page session="false"%>
<jsp:include page="header.jsp">
	<jsp:param name="title" value="Relay Transport - Live Platforms" />	
</jsp:include>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="jadex.base.relay.*" %>
<%@ page import="java.util.*" %>
<%
	PlatformInfo[]	infos	= (PlatformInfo[])request.getAttribute("platforms");
	PeerEntry[]	peers	= (PeerEntry[])request.getAttribute("peers");
%>

<%
if(infos.length>0)
{
	StringBuffer markers	= new StringBuffer();
	Set<String> positions	= new HashSet<String>();
	for(int i=0; i<infos.length && markers.length()+250<2048; i++)	// hack!!! make sure url length stays below 2048 character limit. 
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
<%	} %>

<table>
	<tr>
		<th>&nbsp;</th>
		<th>&nbsp;</th>
		<th>Platform</th>
		<th>Host</th>
		<th>Location</th>
		<th>Connected Since</th>
		<th>Received Messages</th>
		<th>Avg. Transfer Rate</th>
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
					<% if(infos[i].getScheme()!=null && infos[i].getScheme().endsWith("s")) {%>
						<img src="<%= request.getContextPath() %>/resources/lock.png"/>
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
				<td class="number">
					<%= infos[i].getMessageCount() %> (<%= infos[i].getByteCount() %>)</td>
				<td class="number">
					<%= infos[i].getTransferRate() %></td>
			</tr>
	<%	} %>

</table>

<% } else { %>

<p>Currently there are no platforms connected to this relay.</p>
<p>You can check the <a href="history">connection history</a> for more information.</p>

<% } %>

<H2>Alternative Relay Servers</H2>
<p>
	For redundancy and load balancing reasons there are several public relay servers available as stated below.
	You can also host you own relay server by deploying the relay web application in a servlet container such as Tomcat. 
	More information about the Jadex infrastructure can be found at the
	<a href="http://www.activecomponents.org/bin/view/Infrastructure/Overview">infrastructure overview</a>.
</p>
<table style="margin-left: 30px">
	<tr>
		<td style="border: 0; padding: 0">
			o<SPAN class="wikilink">
				<A href="http://jadex.informatik.uni-hamburg.de/relay"><SPAN class="wikigeneratedlinkcontent">http://jadex.informatik.uni-hamburg.de/relay</SPAN></A>
			</SPAN>
		</td>
		<td style="border: 0; padding: 0; text-align: center">
			<img src="resources/uhh-9e763cf9f3e.png"/>
		</td>
	</tr>
	<tr>
		<td style="border: 0; padding: 0">
			o<SPAN class="wikilink">
				<A href="http://www0.activecomponents.org/relay"><SPAN class="wikigeneratedlinkcontent">http://www0.activecomponents.org/relay</SPAN></A>
			</SPAN>
		</td>
		<td style="border: 0; padding: 0; text-align: center">
			<img src="resources/lars-server.png"/>
		</td>
	</tr>
	<tr>
		<td style="border: 0; padding: 0">
			o<SPAN class="wikilink">
				<A href="http://relay1.activecomponents.org/"><SPAN class="wikigeneratedlinkcontent">http://relay1.activecomponents.org/</SPAN></A>
			</SPAN>
		</td>
		<td style="border: 0; padding: 0; text-align: center">
			<img src="resources/Garfield.png"/>
		</td>
	</tr>
	<tr>
		<td style="border: 0; padding: 0">
			o<SPAN class="wikilink">
				<A href="http://toaster.activecomponents.org/relay"><SPAN class="wikigeneratedlinkcontent">http://toaster.activecomponents.org/relay</SPAN></A>
			</SPAN>
		</td>
		<td style="border: 0; padding: 0; text-align: center">
			<img src="resources/jadex-toaster.png"/>
		</td>
	</tr>
	<tr>
		<td style="border: 0; padding: 0">
			o<SPAN class="wikilink">
				<A href="http://www2.activecomponents.org/relay"><SPAN class="wikigeneratedlinkcontent">http://www2.activecomponents.org/relay</SPAN></A>
			</SPAN>
		</td>
		<td style="border: 0; padding: 0; text-align: center">
			<img src="resources/alex-OptiPlex-755.png"/>
		</td>
	</tr>
</table>
<!-- 
<span style="display: inline-block">
<UL>
	<LI style="clear: both; height: 32px; line-height: 32px">
		<SPAN class="wikilink">
			<A href="http://jadex.informatik.uni-hamburg.de/relay"><SPAN class="wikigeneratedlinkcontent">http://jadex.informatik.uni-hamburg.de/relay</SPAN></A></SPAN>
		<span style="float: right; width: 80px; text-align: center"><img src="resources/uhh-9e763cf9f3e.png"/></span>		
	</LI>
	<LI style="clear: both; height: 32px; line-height: 32px">
		<SPAN class="wikilink">
			<A href="http://www0.activecomponents.org/relay"><SPAN class="wikigeneratedlinkcontent">http://www0.activecomponents.org/relay</SPAN></A></SPAN>
		<span style="float: right; width: 80px; text-align: center"><img src="resources/lars-server.png"/></span>
	</LI>
	<LI style="clear: both; height: 32px; line-height: 32px">
		<SPAN class="wikilink">
			<A href="http://relay1.activecomponents.org/"><SPAN class="wikigeneratedlinkcontent">http://relay1.activecomponents.org/</SPAN></A></SPAN>
		<span style="float: right; width: 80px; text-align: center"><img src="resources/Garfield.png"/></span>
	</LI>
</UL>
</span>
 -->
<jsp:include page="footer.jsp" flush="true"/>
