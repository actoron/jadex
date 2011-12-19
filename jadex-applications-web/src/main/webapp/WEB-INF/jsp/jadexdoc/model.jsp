<%@page import="jadex.bridge.modelinfo.ConfigurationInfo"%>
<%@page import="jadex.commons.SUtil"%>
<%@page import="jadex.bridge.modelinfo.IArgument"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%
	IModelInfo	model	= (IModelInfo)request.getSession().getAttribute("model");
%>
<html>
	<head>
		<title>Jadexdoc: <%= model.getFullName() %></title>
		<script type="text/javascript" src="<%= request.getContextPath() %>/resources/jadexdoc/jaxcent21.js"></script>
		<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/jadexdoc/style.css">
	</head>
	<body>
		<h1><%= model.getName() %> (<%= model.getType() %>)</h1>
		<h2>Package <%= model.getPackage() %></h2>
		<div class="desc"><%= model.getDescription() %></div>
		
		<% if(model.getConfigurations().length>0) { %>
		<h2>Configurations</h2>
		<div class="desc">Click on a configuration to show corresponding argument and result values.</div>
		<% } %>
		<form>
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
				<tr id="<%= "config"+i %>">
					<td><%= confs[i].getName() %></td>
					<td class="desc"><%= confs[i].getDescription() %></td>
				</tr>
			<%	} %>
			</table>
		</form>
		
		<h2 id="flags">Flags</h2>
		<table>
			<tr class="even">
			<td>Auto Shutdown</td>
			<td id="autoshutdown"><%= model.getAutoShutdown(null)!=null && model.getAutoShutdown(null).booleanValue() ? "true" : "false" %></td>
			<td class="desc">Destroy the component, when the last non-daemon subcomponent has been removed.</td>
			</tr><tr class="odd">			
			<td>Daemon</td>
			<td id="daemon"><%= model.getDaemon(null)!=null && model.getDaemon(null).booleanValue() ? "true" : "false" %></td>
			<td class="desc">A daemon component does not prevent auto-shutdown of its parent.</dd>
			</tr><tr class="even">
			<td>Master</td>
			<td id="master"><%= model.getMaster(null)!=null && model.getMaster(null).booleanValue() ? "true" : "false" %></td>
			<td class="desc">Destroy the parent component, when this component is destroyed.</dd>
			</tr><tr class="odd">			
			<td>Suspend</td>
			<td id="suspend"><%= model.getSuspend(null)!=null && model.getSuspend(null).booleanValue() ? "true" : "false" %></td>
			<td class="desc">Start the component in suspended state.</td>
			</tr>
		</table>
		
		<% if(model.getArguments().length>0) { %>
		<h2>Arguments</h2>
		<table>
			<tr>
				<th>Name</th>
				<th>Type</th>
				<th>Description</th>
				<th id="argdef">Default Value</th>
			</tr>
		<%
			IArgument[]	args	=	model.getArguments();
			for(int i=0; i<args.length; i++)
			{
		%>
			<tr class="<%= i%2==0 ? "even" : "odd"%>">
				<td><%= args[i].getName() %></td>
				<td><%= args[i].getClazz().getTypeName() %></td>
				<td class="desc"><%= args[i].getDescription() %></td>
				<td id="<%= "arg"+i %>"><%= SUtil.arrayToString(args[i].getDefaultValue()) %></td>
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
				<th id="resdef">Default Value</th>
			</tr>
		<%
			IArgument[]	args	=	model.getResults();
			for(int i=0; i<args.length; i++)
			{
		%>
			<tr class="<%= i%2==0 ? "even" : "odd"%>">
				<td><%= args[i].getName() %></td>
				<td><%= args[i].getClazz().getTypeName() %></td>
				<td class="desc"><%= args[i].getDescription() %></td>
				<td id="<%= "res"+i %>"><%= SUtil.arrayToString(args[i].getDefaultValue()) %></td>
			</tr>
		<%	} %>
		</table>
		<% } %>		
	</body>	
</html>
