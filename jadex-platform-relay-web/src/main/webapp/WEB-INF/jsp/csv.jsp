<%@page session="false"%>
<%@page import="java.net.InetAddress"%>
<%@ page language="java" contentType="text/comma-separated-values; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
%><%@ page import="jadex.base.relay.*"
%><%@ page import="java.util.*"
%><%
	response.setHeader("Content-Disposition", "attachment; filename=relay_statistics.csv");
%>Relay Statistics of <%= InetAddress.getLocalHost().getHostName() %> (<%= PlatformInfo.TIME_FORMAT_LONG.format(new Date()) %>)
ID;Platform;Host;IP;Scheme;Connected;Disconnected;Messages;Bytes;Transfer_Time 
<%
	Iterator<PlatformInfo>	infos	= (Iterator<PlatformInfo>)request.getAttribute("platforms");
	while(infos.hasNext())
	{
		PlatformInfo info	= infos.next();
		%><%= info.getDBId()
		%>;<%= info.getId()
		%>;<%= info.getHostName()
		%>;<%= info.getHostIP()
		%>;<%= info.getScheme()
		%>;<%= info.getConnectDate()!=null ? info.getConnectDate().getTime() : ""
		%>;<%= info.getDisconnectDate()!=null ? info.getDisconnectDate().getTime() : ""
		%>;<%= info.getMessageCount()
		%>;<%= info.getBytes()
		%>;<%= info.getTransferTime() %>
<%	}
%>