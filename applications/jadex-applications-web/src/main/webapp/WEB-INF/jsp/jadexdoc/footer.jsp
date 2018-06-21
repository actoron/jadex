<%@page session="false"%>
<%@page import="java.util.Date"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="jadex.commons.collection.MultiCollection"%>
<%@page import="java.util.Collection"%>
<%@page import="jadex.commons.future.IIntermediateFuture"%>
<%@page import="jadex.bridge.modelinfo.IModelInfo"%>
<%@page import="jadex.bridge.VersionInfo"%>
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

							</DIV>
							<DIV class="clearfloats"></DIV>
						</DIV>
						<DIV id="xdocFooter">
							<DIV id="xdocAuthors">
								<DIV class="xdocCreation">
									Page generated on <%= new Date().toString() %><BR>
								</DIV>
							</DIV>
						</DIV>
					</DIV>
				</DIV>
				<DIV id="leftPanels" class="panels left">
					<DIV class="panel expanded Navigation">
						<H1 class="xwikipaneltitle">Navigation</H1>
						<DIV class="xwikipanelcontents">
							<DIV id="xwikinavcontainer">
								<DIV id="xwikinav" class="accordion">
								<%  for(int i=0; i<packages.length; i++)
									{ %>
										<DIV id="xwikinav<%= packages[i] %>">
											<DIV id="xwikinav<%= packages[i] %>Header" class="accordionTabTitleBar">
												<H1 id="H<%= packages[i] %>" class="wikigeneratedheader">
													<SPAN><%= packages[i].indexOf(".")==-1 ? packages[i] : packages[i].substring(packages[i].lastIndexOf(".")+1) %></SPAN>
												</H1>
											</DIV>
											<DIV id="xwikinav<%= packages[i] %>Content" class="accordionTabContentBox">
											
											<%	IModelInfo[]	lmodels	= (IModelInfo[])pmodels.getCollection(packages[i]).toArray(new IModelInfo[0]);
												Arrays.sort(lmodels, new Comparator<IModelInfo>()
												{
													public int compare(IModelInfo o1, IModelInfo o2)
													{
														return o1.getName().compareTo(o2.getName());
													}
												});
												for(int j=0; j<lmodels.length; j++)
												{ %>
													<UL>
														<LI>
															<%	/* if(request.getServletPath().indexOf("history")==-1)
																{
															% >
																<STRONG>Live Platforms</STRONG>
															<%
																}
																else
																{ */
															%>
															<SPAN class="wikilink"><A href="view?model=<%= URLEncoder.encode(lmodels[j].getFilename(), "UTF-8") %>">
																<img src="icon?type=<%= URLEncoder.encode(lmodels[j].getType(), "UTF-8") %>"/>
																<SPAN class="wikigeneratedlinkcontent"><%= lmodels[j].getName() %></SPAN>
															</A></SPAN>
															<%
																// }
															%>
														</LI>
													</UL>
											<% 	} %>
											</DIV>
										</DIV>											
								<% 	} %>
								</DIV>
							</DIV>
						</DIV>
					</DIV>
					<DIV class="clearfloats"></DIV>
				</DIV>
			</DIV>
			<P style="text-align: center; color: black;">
				Copyright (C) 2002-2014 Lars Braubach, Alexander Pokahr, Kai Jander</br>
				Version <%= VersionInfo.getInstance().getVersion()+" from "+VersionInfo.getInstance().getNumberDateString() %>
			</P>
		</DIV>
	</DIV>
</BODY>
</HTML>
