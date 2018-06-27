<%@page import="jadex.commons.future.IFuture"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="jadex.commons.collection.MultiCollection"%>
<%@page import="jadex.commons.future.IIntermediateFuture"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%@page import="java.util.Collection"%>
<%@page session="false"%>
<%@page import="java.net.InetAddress"%>
<%
	String	pkgid	= "0";
	if(request.getAttribute("model")!=null)
	{
		IModelInfo	model	= ((IFuture<IModelInfo>)request.getAttribute("model")).get();
		Collection<IModelInfo>	models	= ((IIntermediateFuture<IModelInfo>)request.getAttribute("models")).getIntermediateResults();
		
		MultiCollection	pmodels	= new MultiCollection();
		for(Iterator<IModelInfo> it=models.iterator(); it.hasNext(); )
		{
			IModelInfo	info	= it.next();
			pmodels.add(info.getPackage(), info);
		}
		String[]	packages	= (String[])pmodels.getKeys(String.class);
		Arrays.sort(packages);
		for(int i=0; i<packages.length; i++)
		{
			if(model.getPackage().equals(packages[i]))
			{
				pkgid	= Integer.toString(i);
				break;
			}
		}
	}
%>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
<% if(request.getAttribute("refresh")!=null)  { %>
	<meta http-equiv="refresh" content="<%= request.getAttribute("refresh") %>"/>
<% } %>
<title>Jadexdoc: <%= request.getAttribute("title") %></title>
<link rel="shortcut icon" type="image/ico" href="<%= request.getContextPath() %>/resources/jadexdoc/jadex_icon.ico" />
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
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/jadexdoc/colibri.css"/>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/jadexdoc/style.css"/>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/jadexdoc/print.css" media="print"/>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/jadexdoc/prototype.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/jadexdoc/effects.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/jadexdoc/scriptaculous.js' defer='defer'></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/resources/jadexdoc/accordion.js' defer='defer'></script>
<script type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>
</HEAD>
<BODY id="body"
	onload="createAccordion({div:'xwikinav', no:<%= pkgid %>, height:150});"
	class="wiki-xwiki space-AC_User_Guide viewbody hideright">
	<DIV id="xwikimaincontainer">
		<DIV id="xwikimaincontainerinner">
			<DIV id="headerglobal" class="layoutsection">
				<DIV class="minwidthb"></DIV>
				<DIV id="company">
					<DIV id="companylogo">
						<A title="Home" href="http://www.activecomponents.org/" rel="home"><IMG
							alt="Wiki Logo" src="<%= request.getContextPath() %>/resources/jadexdoc/logo.png"></A>
					</DIV>
				</DIV>
				<DIV class="clearfloats"></DIV>
			</DIV>
			<DIV
				style="top: 60px; width: 100%; text-align: right; position: absolute;">
				<p>
					<div class="g-plusone" data-size="medium" href="www.activecomponents.org">
					</div>
					<a href="http://www.activecomponents.org/bin/view/Infrastructure/Overview">
						<IMG alt="<%= InetAddress.getLocalHost().getHostName() %>" src="<%= request.getContextPath() %>/resources/jadexdoc/<%= InetAddress.getLocalHost().getHostName() %>.png"/>
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
										<H1><%= request.getAttribute("title") %></H1>
									</DIV>
									<DIV id="document-info">

										<DIV class="clearfloats"></DIV>
									</DIV>
									<DIV id="xwikicontent">
										<H1 class="hidden"><%= request.getAttribute("title") %></H1>
