<%@ page errorPage="500" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>Jadexdoc: <%= request.getAttribute("title") %></title>
		<% /*if(((Boolean)request.getAttribute("jaxcent")).booleanValue()) { % >
		<script type="text/javascript" src="<%= request.getContextPath() % >/resources/jadexdoc/jaxcent21.js"></script>
		<% } */ %>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/jadexdoc/style.css" type="text/css">
		<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/jadexdoc/print.css" type="text/css" media="print"> 
	</head>

	<body bgcolor="#F0F0FF">
	<table width="100%" height="85%"  cellpadding="0" cellspacing="0">
	<tr height="15%" class="titleblock">
		<td colspan="2">
			<jsp:include page="title.jsp" flush="true"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" valign="middle" height="16" class="horbar">&nbsp;</td>
	</tr>
	<tr valign="top">
		<td width="20%" class="navblock">
			<jsp:include page="modellist.jsp" flush="false"/>
			<!--<jsp:include page="navigation.jsp" flush="false"/>-->
		</td>
		<td class="textblock">
