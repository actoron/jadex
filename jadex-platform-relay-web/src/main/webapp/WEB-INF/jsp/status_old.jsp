<%@page import="java.net.URL"%>
<%@page import="jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager"%>
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
	PeerHandler[]	peers	= (PeerHandler[])request.getAttribute("peers");
	String	url	= RelayConnectionManager.httpAddress((String)request.getAttribute("url"));
	String	host	= new URL(url).getHost();
	StringBuffer markers	= new StringBuffer();
	String[]	colors	= new String[]{"black", "brown", "green", "purple", "yellow", "blue", "gray", "orange", "red", "white"};
	Set<String> positions	= new HashSet<String>();
%>

<%
// Add labelled marker A for own location.
String	pos	= GeoIPService.getGeoIPService().getPosition(host);
if(pos!=null)
{
	markers.append("&markers=label:");
	markers.append('A');
	markers.append("|color:");
	markers.append(colors[Math.abs(url.hashCode())%colors.length]);
	markers.append("|");
	markers.append(pos);
	positions.add(pos);
}

// Add markers for relay servers
if(peers.length>0)
{
	for(int i=0; i<peers.length && markers.length()+250<2048; i++)	// hack!!! make sure url length stays below 2048 character limit. 
	{
		if(peers[i].getPosition()!=null && !positions.contains(peers[i].getPosition()))
		{
			if(i<25)
			{
				// Add labelled markers for first B..Z entries
				markers.append("&markers=label:");
				markers.append((char)('B'+i));
				markers.append("|color:");
				markers.append(colors[Math.abs(peers[i].getUrl().hashCode())%colors.length]);
				markers.append("|");
				markers.append(peers[i].getPosition());
				positions.add(peers[i].getPosition());
			}
			else if(i==25)
			{
				// Add unlabelled markers for each unique position of remaining entries
				markers.append("&markers=color:");
				markers.append(colors[Math.abs(peers[i].getUrl().hashCode())%colors.length]);
				markers.append("|");
				markers.append(peers[i].getPosition());
				positions.add(peers[i].getPosition());
			}
			else
			{
				// Add unlabelled markers for each unique position of remaining entries
				markers.append("|");
				markers.append(peers[i].getPosition());
				positions.add(peers[i].getPosition());
			}
		}
	}
}

positions.clear();

// Add markers for locally connected platforms
boolean	unlabelled	= false;
if(infos.length>0)
{
	for(int i=0; i<infos.length && markers.length()+250<2048; i++)	// hack!!! make sure url length stays below 2048 character limit. 
	{
		if(infos[i].getPosition()!=null && !positions.contains(infos[i].getPosition()))
		{
			if(i<9)
			{
				// Add labelled markers for first 1..9 entries
				markers.append("&markers=size:mid|label:");
				markers.append(i+1);
				markers.append("|color:");
				markers.append(colors[Math.abs(url.hashCode())%colors.length]);
				markers.append("|");
				markers.append(infos[i].getPosition());
				positions.add(infos[i].getPosition());
			}
			else if(!unlabelled)
			{
				// Add first unlabelled marker for unique position of remaining entries
				markers.append("&markers=size:mid|color:");
				markers.append(colors[Math.abs(url.hashCode())%colors.length]);
				markers.append("|");
				markers.append(infos[i].getPosition());
				positions.add(infos[i].getPosition());
				unlabelled	= true;
			}
			else
			{
				// Add unlabelled markers for each unique position of remaining entries
				markers.append("|");
				markers.append(infos[i].getPosition());
				positions.add(infos[i].getPosition());
			}
		}
	}
}
int	cnt	= infos.length;

// Add markers for remotely connected platforms
if(peers.length>0)
{
	for(int j=0; j<peers.length && markers.length()+250<2048; j++)	// hack!!! make sure url length stays below 2048 character limit. 
	{
		PlatformInfo[]	infos2	= peers[j].getPlatformInfos();
		for(int i=0; i<infos2.length && markers.length()+250<2048; i++)	// hack!!! make sure url length stays below 2048 character limit. 
		{
			if(infos2[i].getPosition()!=null && !positions.contains(infos2[i].getPosition()))
			{
				if(i+cnt<9)
				{
					// Add labelled markers for first 1..9 entries
					markers.append("&markers=size:mid|label:");
					markers.append(i+cnt+1);
					markers.append("|color:");
					markers.append(colors[Math.abs(peers[j].getUrl().hashCode())%colors.length]);
					markers.append("|");
					markers.append(infos2[i].getPosition());
					positions.add(infos2[i].getPosition());
				}
				else if(!unlabelled)
				{
					// Add first unlabelled marker for unique position of remaining entries
					markers.append("&markers=size:mid|color:");
					markers.append(colors[Math.abs(peers[j].getUrl().hashCode())%colors.length]);
					markers.append("|");
					markers.append(infos2[i].getPosition());
					positions.add(infos2[i].getPosition());
					unlabelled	= true;
				}
				else
				{
					// Add unlabelled markers for each unique position of remaining entries
					markers.append("|");
					markers.append(infos2[i].getPosition());
					positions.add(infos2[i].getPosition());
				}
			}
		}
		cnt	+= infos2.length;
	}
}

if(markers.length()>0)
{
%>
	<img class="map" src="https://maps.googleapis.com/maps/api/staticmap?size=700x450&sensor=false<%=markers%>"/>
<%
}

String	cc	= GeoIPService.getGeoIPService().getCountryCode(host);
String	loc	= GeoIPService.getGeoIPService().getLocation(host);
%>

<table>
	<tr>
		<th>&nbsp;</th>
		<th>&nbsp;</th>
		<th>Relay</th>
		<th>Location</th>
		<th>Connected</th>
		<th># of Platforms</th>
	</tr>
	
	<tr>
		<td>A</td>
		<td>
			<%
				if(cc!=null)
				{
			%>
					<img src="<%=request.getContextPath()%>/resources/flags/flag-<%= cc %>.png"/>
			<%
				}
			%>
		</td>

		<td><a href="<%= url %>"><%= url %></a></td>
		<td><%= loc %></td>
		<td>true</td>
		<td><%= infos.length %></td>
	</tr>
	
	<%
		for(int i=0; i<peers.length; i++)
		{
	%>
		<tr title="<%= peers[i].getDebugText() %>">
			<td>
				<%= (char)('B'+i) %>
				</td>
			<td>
				<%
					if(peers[i].getCountryCode()!=null)
					{
				%>
						<img src="<%=request.getContextPath()%>/resources/flags/flag-<%=peers[i].getCountryCode()%>.png"/>
				<%
					}
				%>
			</td>
			<td>
				<a href="<%= RelayConnectionManager.httpAddress(peers[i].getUrl()) %>">
					<%=  RelayConnectionManager.httpAddress(peers[i].getUrl()) %></a>
			</td>
			<td><%= peers[i].getLocation() %></td>
			<td><%= peers[i].isConnected() %></td>
			<td><%= peers[i].getPlatformInfos().length %></td>
		</tr>
	<%
		}
	%>
</table>

<%
if(cnt>0)
{
%>
	<table>
		<tr>
			<th rowspan="2">&nbsp;</th>
			<th rowspan="2">&nbsp;</th>
			<th rowspan="2">Platform</th>
			<th rowspan="2">Host</th>
			<th rowspan="2">Location</th>
			<th rowspan="2">Connected Since</th>
			<th>Received Messages</th>
			<th>Avg. Transfer Rate</th>
		</tr>
		<tr>
			<th colspan="2">Relay</th>
		</tr>
		
		<%
			for(int i=0; i<infos.length; i++)
			{
		%>
			<tr class="<%=i%2==0 ? "even" : "odd"%>" title="<%=infos[i].toString()%>">
				<td>
					<%=i+1%>
					</td>
				<td>
					<%
						if(infos[i].getCountryCode()!=null) {
					%>
						<img src="<%=request.getContextPath()%>/resources/flags/flag-<%=infos[i].getCountryCode()%>.png"/>
					<%
						}
					%>
					<%
						if(infos[i].getScheme()!=null && infos[i].getScheme().endsWith("s")) {
					%>
						<img src="<%=request.getContextPath()%>/resources/lock.png"/>
					<%
						}
					%>
					</td>
				<td>
					<%=infos[i].getId()%> </td>
				<td>
					<%=infos[i].getHostName()%></td>
				<td>
					<%=infos[i].getLocation()%></td>
				<td>
					<%=infos[i].getConnectTime()%></td>
				<td class="number">
					<%=infos[i].getMessageCount()%> (<%=infos[i].getByteCount()%>)</td>
				<td class="number">
					<%=infos[i].getTransferRate()%></td>
			</tr>
	<%
		}
		
		cnt	= infos.length;
		for(int j=0; j<peers.length; j++)
		{
			PlatformInfo[]	infos2	= peers[j].getPlatformInfos();
	
			for(int i=0; i<infos2.length; i++)
			{%>
				<tr class="<%=(i+cnt)%2==0 ? "even" : "odd"%>" title="<%=infos2[i].toString()%>">
					<td>
						<%=i+cnt+1%>
						</td>
					<td>
						<%
							if(infos2[i].getCountryCode()!=null) {
						%>
							<img src="<%=request.getContextPath()%>/resources/flags/flag-<%=infos2[i].getCountryCode()%>.png"/>
						<%
							}
						%>
						<%
							if(infos2[i].getScheme()!=null && infos2[i].getScheme().endsWith("s")) {
						%>
							<img src="<%=request.getContextPath()%>/resources/lock.png"/>
						<%
							}
						%>
						</td>
					<td>
						<%=infos2[i].getId()%> </td>
					<td>
						<%=infos2[i].getHostName()%></td>
					<td>
						<%=infos2[i].getLocation()%></td>
					<td>
						<%=infos2[i].getConnectTime()%></td>
					<td colspan="2">
						<a href="<%= RelayConnectionManager.httpAddress(peers[j].getUrl()) %>">
						<%=  RelayConnectionManager.httpAddress(peers[j].getUrl()) %></a></td>
				</tr>
		<%	}
			cnt	+= infos2.length;
		} %>
	
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
