<%@page import="jadex.commons.future.IFuture"%>
<%@page import="jadex.bridge.modelinfo.ConfigurationInfo"%>
<%@page import="jadex.commons.SUtil"%>
<%@page import="jadex.bridge.modelinfo.IArgument"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%
	IModelInfo	model	= ((IFuture<IModelInfo>)request.getAttribute("model")).get();
	boolean	jaxcent	= ((Boolean)request.getAttribute("jaxcent")).booleanValue();
%>
<h2>Package <%= model.getPackage() %></h2>
<div class="desc"><%= model.getDescription()!=null ? model.getDescription() : "No description." %></div>

<% 	ConfigurationInfo[]	confs	=	model.getConfigurations();
	if(confs.length>0) { %>
<h2>Configurations</h2>
<%/* <div class="desc">Click on a configuration to show corresponding argument and result values.</div> */%>
<table class="printtable">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
<%
	for(int i=0; i<confs.length; i++)
	{
%>
	<tr <%= jaxcent ? "id=\"config"+i+"\"" : "class=\""+ (i%2==0 ? "even\"" : "odd\"") %>>
		<td class="name" <%/* style="cursor:pointer;" */%>><%= confs[i].getName() %></td>
		<td class="desc" <%/* style="cursor:pointer;" */%>><%= confs[i].getDescription() %></td>
	</tr>
<%	} %>
</table>
<% } %>

<%
	boolean	hasflags	= model.getSuspend(null)!=null;
	for(int i=0; !hasflags && i<confs.length; i++)
	{
		hasflags	= confs[i].getSuspend()!=null
			 || confs[i].getSynchronous()!=null || confs[i].getPersistable()!=null;		
	}
	if(hasflags) {
%>
<h2 id="flags">Flags</h2>
<table class="printtable">
	<tr class="even">
	<td class="name">Synchronous</td>
	<td class="value"><div id="synchronous"><%= model.getSynchronous(null)!=null && model.getSynchronous(null).booleanValue() ? "true" : "false" %></div></td>
	<td class="desc">Execute the component synchronous to its parent.</td>
	</tr><tr class="odd">			
	<td class="name">Persistable</td>
	<td class="value"><div id="persistable"><%= model.getPersistable(null)!=null && model.getPersistable(null).booleanValue() ? "true" : "false" %></div></td>
	<td class="desc">Is the component persistable?</td>
	</tr><tr class="even">			
	<td class="name">Suspend</td>
	<td class="value"><div id="suspend"><%= model.getSuspend(null)!=null && model.getSuspend(null).booleanValue() ? "true" : "false" %></div></td>
	<td class="desc">Start the component in suspended state.</td>
	</tr>
</table>
<% } %>

<% if(model.getArguments().length>0) { %>
<h2>Arguments</h2>
<table class="printtable">
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th id="argdef">Default Value</th>
	</tr>
<%
	IArgument[]	args	=	model.getArguments();
	for(int i=0; i<args.length; i++)
	{
%>
	<tr class="<%= i%2==0 ? "even" : "odd"%>">
		<td class="desc" colspan="3"><%= args[i].getDescription() %></td>
	</tr>
	<tr class="<%= i%2==0 ? "even" : "odd"%>">
		<td class="name"><%= args[i].getName() %></td>
		<td class="type"><%= args[i].getClazz()!=null ? args[i].getClazz().getTypeName() : "undefined" %></td>
		<td class="value"><div id="<%= "arg"+i %>"><%= args[i].getDefaultValue().getValue() %></div></td>
	</tr>
<%	} %>
</table>
<% } %>

<% if(model.getResults().length>0) { %>
<h2>Results</h2>		
<table class="printtable">
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
		<td class="desc" colspan="3"><%= args[i].getDescription() %></td>
	</tr>
	<tr class="<%= i%2==0 ? "even" : "odd"%>">
		<td class="name"><%= args[i].getName() %></td>
		<td class="type"><%= args[i].getClazz()!=null ? args[i].getClazz().getTypeName() : "undefined" %></td>
		<td class="value"><div id="<%= "arg"+i %>"><%= args[i].getDefaultValue().getValue() %></div></td>
	</tr>
<%	} %>
</table>
<% } %>		
