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
	Map<String, String> rmarkers	= new LinkedHashMap<String, String>();
	Map<String, String> pmarkers	= new LinkedHashMap<String, String>();
%>

<%
// Add marker for own location.
String	pos	= GeoIPService.getGeoIPService().getPosition(host);
if(pos!=null)
{
	String	marker	= GeoIPService.getGeoIPService().getLocation(host)+"\", \"<h3>"+GeoIPService.getGeoIPService().getLocation(host)+"</h3>Relay A: "+url;
	rmarkers.put(pos, marker);
}

// Add markers for relay servers
if(peers.length>0)
{
	for(int i=0; i<peers.length; i++) 
	{
		if(peers[i].getPosition()!=null)
		{
			String	marker	= rmarkers.get(peers[i].getPosition());
			if(marker==null)
			{
				marker	= peers[i].getLocation()+"\", \"<h3>"+peers[i].getLocation()+"</h3>";
			}
			else
			{
				marker	+= "<br/>";
			}
			marker	+= (i<25 ? "Relay "+(char)('B'+i)+": ": "Relay: ")
				+ RelayConnectionManager.httpAddress(peers[i].getUrl());
			rmarkers.put(peers[i].getPosition(), marker);
		}
	}
}

// Add markers for locally connected platforms
boolean	unlabelled	= false;
if(infos.length>0)
{
	for(int i=0; i<infos.length; i++) 
	{
		if(infos[i].getPosition()!=null)
		{
			String	marker	= pmarkers.get(infos[i].getPosition());
			if(marker==null)
			{
				marker	= infos[i].getLocation()+"\", \"<h3>"+infos[i].getLocation()+"</h3>";
			}
			else
			{
				marker	+= "<br/>";
			}
			marker	+= (i+1)+": "+infos[i].getId()+" ("+infos[i].getHostName()+")";
			pmarkers.put(infos[i].getPosition(), marker);
		}
	}
}
int	cnt	= infos.length;

// Add markers for remotely connected platforms
if(peers.length>0)
{
	for(int j=0; j<peers.length; j++) 
	{
		PlatformInfo[]	infos2	= peers[j].getPlatformInfos();
		for(int i=0; i<infos2.length; i++) 
		{
			if(infos2[i].getPosition()!=null)
			{
				String	marker	= pmarkers.get(infos2[i].getPosition());
				if(marker==null)
				{
					marker	= infos2[i].getLocation()+"\", \"<h3>"+infos2[i].getLocation()+"</h3>";
				}
				else
				{
					marker	+= "<br/>";
				}
				marker	+= (i+1)+": "+infos2[i].getId()+" ("+infos2[i].getHostName()+")";
				pmarkers.put(infos2[i].getPosition(), marker);
			}
		}
		cnt	+= infos2.length;
	}
}


if(rmarkers.size()>0 || pmarkers.size()>0)
{ %>
	<!-- map styles: examples.map-i86nkdio, examples.map-qfyrx5r8 -->
	<div id="mapcontainer">
		<div id="map">Please wait while the history is loading...</div>
	</div>
	<script type="text/javascript">
	var rmarkers = [
   	    <% for(Map.Entry<String, String> marker: rmarkers.entrySet()) { %>       
   			[<%= marker.getKey()%>, "<%= marker.getValue() %>"],
   		<% } %>
   		];
	
	var pmarkers = [
   	    <% for(Map.Entry<String, String> marker: pmarkers.entrySet()) { %>       
   			[<%= marker.getKey()%>, "<%= marker.getValue() %>"],
   		<% } %>
   		];
		
//		var tiles = L.tileLayer('http://{s}.tiles.mapbox.com/v3/examples.map-i86nkdio/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.light/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.dark/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.streets-satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.streets-basic/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('https://api.mapbox.com/v4/mapbox.outdoors/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicG9rYWhyIiwiYSI6ImFjNzdjOTc0MzVkODQwNDUxNDdiNTZlMWExNDU4MTA3In0.J_4j3K-Ydp5lPJiTWa6fsA', {
//		var tiles = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
				minZoom: 0,
				maxZoom: 19,
				attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, Imagery © <a href="http://mapbox.com">Mapbox</a>'
//				attribution: 'Map data &copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
			}),
			latlng = L.latLng(53.550556, 9.993333);

		var map = L.map('map', {center: latlng, zoom: 13, layers: [tiles]});

		var server_icon = L.icon({
		    iconUrl: 'resources/server.png',
		    shadowUrl: 'resources/server_shadow.png',

		    iconSize:     [17, 43], // size of the icon
		    shadowSize:   [32, 43], // size of the shadow
		    iconAnchor:   [-2, 41], // point of the icon which will correspond to marker's location
		    shadowAnchor: [-2, 41],  // the same for the shadow
		    popupAnchor:  [17, -45] // point from which the popup should open relative to the iconAnchor
		});
		
		for (var i=0; i<rmarkers.length; i++)
		{
			var a = rmarkers[i];
			var marker = L.marker(L.latLng(a[0], a[1]), { title: a[2], icon: server_icon });
			marker.bindPopup(a[3]);
			marker.addTo(map);
		}
		
		var pmarkergroup = L.markerClusterGroup({ chunkedLoading: true });
		
		for (var i = 0; i < pmarkers.length; i++) {
			var a = pmarkers[i];
			var title = a[2];
			var marker = L.marker(L.latLng(a[0], a[1]), { title: a[2] });
			marker.bindPopup(a[3]);
			pmarkergroup.addLayer(marker);
		}

		map.addLayer(pmarkergroup);
		map.fitBounds(pmarkergroup.getBounds());
		
		$("#mapcontainer").on("resizestop", function(event, ui)
		{
			var mapwidth	= $("#mapcontainer").outerWidth(true); // Map size with margins, padding, etc.
			var parentwidth	= $("#mapcontainer").parent().width();
			if(mapwidth>parentwidth)
			{
				var gap	= mapwidth - $("#mapcontainer").width(); // Calc. size of margins, padding, etc.
				$("#mapcontainer").width(parentwidth-gap);			
			}
			map.invalidateSize();
		});
	</script>
<%
}


String	cc	= GeoIPService.getGeoIPService().getCountryCode(host);
String	loc	= GeoIPService.getGeoIPService().getLocation(host);
%>

<table>
	<tr>
		<th colspan="3">Relay</th>
		<th>Location</th>
		<th>Connected</th>
		<th># of Platforms</th>
	</tr>
	
	<tr>
		<td>
			<img src="<%=request.getContextPath()%>/resources/server.png" style="vertical-align:middle"/>
			A
		</td>
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
				<img src="<%=request.getContextPath()%>/resources/server.png" style="vertical-align:middle"/>
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
			<th rowspan="2" colspan="3">Platform</th>
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
