<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="jadex.base.relay.*" %>
<%@ page import="java.util.*" %>

<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
<title>Relay Transport - Connection History</title>
<link rel="shortcut icon" type="image/ico" href="resources/jadex_icon.ico" />
<link rel="stylesheet" type="text/css" href="resources/colibri.css" media="all" />

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/leaflet.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/leaflet.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link rel="stylesheet" href="resources/markercluster/MarkerCluster.css" />
<link rel="stylesheet" href="resources/markercluster/MarkerCluster.Default.css" />
<script src="resources/markercluster/leaflet.markercluster.js"></script>
<link rel="stylesheet" href="resources/map.css" />
	
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css" />
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	
<script type="text/javascript">
	$(function() {
		$("#mapcontainer").resizable({
			helper: "ui-resizable-helper"
		});
	});
</script>

</head>
<body>
<%
PlatformInfo[]	infos	= (PlatformInfo[])request.getAttribute("platforms");

if(infos.length>0)
{
	Map<String, String> markers	= new LinkedHashMap<String, String>();
	for(int i=0; i<infos.length; i++) 
	{
		if(infos[i].getPosition()!=null)
		{
			String	marker	= markers.get(infos[i].getPosition());
			if(marker==null)
			{
				marker	= "<h3>"+infos[i].getLocation()+"</h3>";
			}
			else
			{
				marker	+= "<br/>";
			}
			marker	+= (i+1)+": "+infos[i].getId()+" ("+infos[i].getHostName()+")";
			markers.put(infos[i].getPosition(), marker);
		}
	}
	
	if(markers.size()>0)
	{ %>
		<!-- map styles: examples.map-i86nkdio, examples.map-qfyrx5r8 -->
		<div id="mapcontainer">
			<div id="map"/>
		</div>
		<script type="text/javascript">
			var addressPoints = [
		    <% for(Map.Entry<String, String> marker: markers.entrySet()) { %>       
				[<%= marker.getKey()%>, "<%= marker.getValue() %>"],
			<% } %>
			];
			var tiles = L.tileLayer('http://{s}.tiles.mapbox.com/v3/examples.map-qfyrx5r8/{z}/{x}/{y}.png', {
					minZoom: 0,
					maxZoom: 19,
					attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>'
				}),
				latlng = L.latLng(53.550556, 9.993333);
	
			var map = L.map('map', {center: latlng, zoom: 13, layers: [tiles]});
	
			var markers = L.markerClusterGroup({ chunkedLoading: true });
			
			for (var i = 0; i < addressPoints.length; i++) {
				var a = addressPoints[i];
				var title = a[2];
				var marker = L.marker(L.latLng(a[0], a[1]), { title: title });
				marker.bindPopup(title);
				markers.addLayer(marker);
			}
	
			map.addLayer(markers);
			map.fitBounds(markers.getBounds());
			
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
</table>

</body>
</html>