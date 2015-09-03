<%@page session="false"%>
<%@page import="java.net.InetAddress"%>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<HTML lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<HEAD>
<META content="text/html; charset=UTF-8" http-equiv="Content-Type" />
<% if(request.getAttribute("refresh")!=null)  { %>
<!-- <meta http-equiv="refresh" content="<%= request.getAttribute("refresh") %>"> -->
<% } %>
<TITLE><%= request.getParameter("title") %></TITLE>
<LINK rel="shortcut icon" type="image/ico" href="resources/jadex_icon.ico" />
<% /*
<SCRIPT type="text/javascript">
	var _gaq = _gaq || [];
	_gaq.push([ '_setAccount', 'UA-33705718-1' ]);
	_gaq.push([ '_trackPageview' ]);

	(function() {
		var ga = document.createElement('script');
		ga.type = 'text/javascript';
		ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl'
				: 'http://www')
				+ '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0];
		s.parentNode.insertBefore(ga, s);
	})();
</SCRIPT>
<META name="google-site-verification"
	content="PSu8atqC7qDGHNZBfqQjVV8xM13xwyOUQs-4BmycCXc" />
*/ %>
<LINK rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/colibri.css" media="all" />
<!-- 
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/prototype.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/effects.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/scriptaculous.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/accordion.js' defer='defer'></script>
 -->
<%	/*if(request.getParameter("title")!=null && request.getParameter("title").toLowerCase().indexOf("history")!=-1) {*/ %>
	<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7/leaflet.css" />
	<script src="http://cdn.leafletjs.com/leaflet-0.7/leaflet.js"></script>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/markercluster/MarkerCluster.css" />
	<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/markercluster/MarkerCluster.Default.css" />
	<script src="<%= request.getContextPath() %>/resources/markercluster/leaflet.markercluster.js"></script>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/map.css" />
	
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
	<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	
	<script type="text/javascript">
		$(function() {
			$("#mapcontainer").resizable({
				helper: "ui-resizable-helper"
			});
		});
	</script>
<%	/*}*/ %>

</HEAD>
<BODY id="body"
	onload="createAccordion({div:'xwikinav', no:0, height:150});"
	class="wiki-xwiki space-AC_User_Guide viewbody hideright">
	<DIV id="xwikimaincontainer">
		<DIV id="xwikimaincontainerinner">
			<DIV id="headerglobal" class="layoutsection">
				<DIV class="minwidthb"></DIV>
				<DIV id="company">
					<DIV id="companylogo">
						<A title="Home" href="http://www.activecomponents.org/" rel="home"><IMG
							alt="Wiki Logo" src="<%= request.getContextPath() %>/resources/logo.png"></A>
					</DIV>
				</DIV>
				<DIV class="clearfloats"></DIV>
			</DIV>
			<DIV
				style="top: 60px; width: 100%; text-align: right; position: absolute;">
				<p>
					<a href="http://www.activecomponents.org/bin/view/Infrastructure/Overview">
						<IMG alt="<%= InetAddress.getLocalHost().getHostName() %>" src="<%= request.getContextPath() %>/resources/<%= InetAddress.getLocalHost().getHostName() %>.png"/>
					</a>
				</p>
			</DIV>
			<DIV id="contentcontainer" class="contenthideright">
				<DIV id="contentcontainerinner">
					<DIV class="leftsidecolumns">
						<DIV id="contentcolumn">
							<DIV class="main layoutsubsection">
								<DIV id="contentmenu" class="actionmenu">
									<DIV class="gradientfilterIE"></DIV>
								</DIV>
								<DIV id="mainContentArea">
									<DIV id="document-title">
										<H1><%= request.getParameter("title") %></H1>
									</DIV>
									<DIV id="document-info">

										<DIV class="clearfloats"></DIV>
									</DIV>
									<DIV id="xwikicontent">
										<H1 class="hidden"><%= request.getParameter("title") %></H1>
