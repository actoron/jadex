<%@page import="jadex.bridge.modelinfo.ConfigurationInfo"%>
<%@page import="jadex.commons.SUtil"%>
<%@page import="jadex.bridge.modelinfo.IArgument"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%
	IModelInfo	model	= (IModelInfo)request.getAttribute("model");
%>
<html>
	<head>
		<title>Jadexdoc: <%= model.getFullName() %></title>
	</head>
	<body>
		<h1><%= model.getName() %> (<%= model.getType() %>)</h1>
		<h2>Package <%= model.getPackage() %></h2>
		<%= model.getDescription() %>
		
		<% if(model.getConfigurations().length>0) { %>
		<h2>Configurations</h2>
		<% } %>
		<table>
			<tr>
				<th>Name</th>
				<th>Description</th>
			</tr>
		<%
			ConfigurationInfo[]	confs	=	model.getConfigurations();
			for(int i=0; i<confs.length; i++)
			{
		%>
			<tr>
				<td><%= confs[i].getName() %></td>
				<td><%= confs[i].getDescription() %></td>
			</tr>
		<%	} %>
		</table>
		
		<h2>Flags</h2>
		<table>
			<tr>
			<td>Auto Shutdown</td>
			<td><%= model.getAutoShutdown(null)!=null && model.getAutoShutdown(null).booleanValue() ? "true" : "false" %></td>
			<td>Destroy the component, when the last non-daemon subcomponent has been removed.</td>
			</tr><tr>			
			<td>Daemon</td>
			<td><%= model.getDaemon(null)!=null && model.getDaemon(null).booleanValue() ? "true" : "false" %></td>
			<td>A daemon component does not prevent auto-shutdown of its parent.</dd>
			</tr><tr>
			<td>Master</td>
			<td><%= model.getMaster(null)!=null && model.getMaster(null).booleanValue() ? "true" : "false" %></td>
			<td>Destroy the parent component, when this component is destroyed.</dd>
			</tr><tr>			
			<td>Suspend</td>
			<td><%= model.getSuspend(null)!=null && model.getSuspend(null).booleanValue() ? "true" : "false" %></td>
			<td>Start the component in suspended state.</td>
			</tr>
		</table>
		
		<% if(model.getArguments().length>0) { %>
		<h2>Arguments</h2>
		<table>
			<tr>
				<th>Name</th>
				<th>Type</th>
				<th>Description</th>
				<th>Default Value</th>
			</tr>
		<%
			IArgument[]	args	=	model.getArguments();
			for(int i=0; i<args.length; i++)
			{
		%>
			<tr>
				<td><%= args[i].getName() %></td>
				<td><%= args[i].getClazz().getTypeName() %></td>
				<td><%= args[i].getDescription() %></td>
				<td><%= SUtil.arrayToString(args[i].getDefaultValue()) %></td>
			</tr>
		<%	} %>
		</table>
		<% } %>
		
		<% if(model.getResults().length>0) { %>
		<h2>Results</h2>		
		<table>
			<tr>
				<th>Name</th>
				<th>Type</th>
				<th>Description</th>
				<th>Default Value</th>
			</tr>
		<%
			IArgument[]	args	=	model.getResults();
			for(int i=0; i<args.length; i++)
			{
		%>
			<tr>
				<td><%= args[i].getName() %></td>
				<td><%= args[i].getClazz().getTypeName() %></td>
				<td><%= args[i].getDescription() %></td>
				<td><%= SUtil.arrayToString(args[i].getDefaultValue()) %></td>
			</tr>
		<%	} %>
		</table>
		<% } %>		
	</body>	
</html>
