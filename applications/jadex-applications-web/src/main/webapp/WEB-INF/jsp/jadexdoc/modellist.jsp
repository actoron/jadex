<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="jadex.commons.collection.MultiCollection"%>
<%@page import="java.util.Collection"%>
<%@page import="jadex.commons.future.IIntermediateFuture"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%
	Collection<IModelInfo>	models	= ((IIntermediateFuture<IModelInfo>)request.getAttribute("models")).getIntermediateResults();
	
	MultiCollection	pmodels	= new MultiCollection();
	for(Iterator<IModelInfo> it=models.iterator(); it.hasNext(); )
	{
		IModelInfo	info	= it.next();
		pmodels.add(info.getPackage(), info);
	}
	String[]	packages	= (String[])pmodels.getKeys(String.class);
	Arrays.sort(packages);
	
%>

<table class="printtable">
	<% for(int i=0; i<packages.length; i++) { %>
		<tr id="idpackage_<%= packages[i] %>" class="package">
			<td colspan="2"><%= packages[i] %></td>
		</tr>
		
		<%
			IModelInfo[]	lmodels	= (IModelInfo[])pmodels.getCollection(packages[i]).toArray(new IModelInfo[0]);
			Arrays.sort(lmodels, new Comparator<IModelInfo>()
			{
				public int compare(IModelInfo o1, IModelInfo o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});
			for(int j=0; j<lmodels.length; j++) { 
		%>
				<tr name="namepackage_<%= lmodels[j].getPackage() %>" class="model">
					<td><img src="icon?type=<%= URLEncoder.encode(lmodels[j].getType(), "UTF-8") %>"/></td>
					<td><a href="view?model=<%= URLEncoder.encode(lmodels[j].getFilename(), "UTF-8") %>">
							<%= lmodels[j].getName() %>
						</a></td>
				</tr>
	<% 		} 
	} %>
</table>

