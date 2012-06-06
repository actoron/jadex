<%@ page errorPage="500" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>Jadex Relay Transport Status</title>
		<% if(request.getAttribute("refresh")!=null)  { %>
		<meta http-equiv="refresh" content="<%= request.getAttribute("refresh") %>">
		<% } %>
		<style type="text/css">
		<%@ include file="style.css" %>
		</style>
	</head>

	<body bgcolor="#F0F0FF">
	<table width="100%" height="85%"  cellpadding="0" cellspacing="0">
	<tr height="15%">
		<td colspan="2">
			<jsp:include page="title.jsp" flush="true"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" valign="middle" height="16" class="horbar">&nbsp;</td>
	</tr>
	<tr valign="top">
		<td width="12%" class="menublock_top">
			<jsp:include page="navigation.jsp" flush="false"/>
		</td>
		<td class="textblock">
