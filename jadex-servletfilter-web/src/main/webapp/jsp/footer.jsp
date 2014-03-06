<%@page import="jadex.bridge.VersionInfo"%>
<%@page session="false"%>
<%@page import="java.util.Date"%>

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
									<DIV id="xwikinavAC Tool Guide">
										<DIV id="xwikinavAC Tool GuideHeader"
											class="accordionTabTitleBar">
											<H1 id="HACToolGuide" class="wikigeneratedheader">
												<SPAN>Web Proxy</SPAN>
											</H1>
										</DIV>
										<DIV id="xwikinavAC Tool GuideContent"
											class="accordionTabContentBox">
											<UL>
												<LI>
													<%	if(request.getServletPath().indexOf("login")!=-1)
														{
													%>
														<STRONG>Login</STRONG>
													<%
														}
														else
														{
													%>
													<SPAN class="wikilink"><A href="login">
														<SPAN class="wikigeneratedlinkcontent">Login</SPAN>
													</A></SPAN>
													<%
														}
													%>
												</LI>
												<LI>
													<%	if(request.getServletPath().indexOf("logout")!=-1)
														{
													%>
														<STRONG>Logout</STRONG>
													<%
														}
														else
														{
													%>
													<SPAN class="wikilink"><A href="logout">
														<SPAN class="wikigeneratedlinkcontent">Logout</SPAN>
													</A></SPAN>
													<%
														}
													%>
												</LI>
												<LI>
													<%	if(request.getServletPath().indexOf("users")!=-1)
														{
													%>
														<STRONG>Manage Users</STRONG>
													<%
														}
														else
														{
													%>
													<SPAN class="wikilink"><A href="displayUsers">
														<SPAN class="wikigeneratedlinkcontent">Manage Users</SPAN>
													</A></SPAN>
													<%
														}
													%>
												</LI>
												<LI>
													<%	if(request.getServletPath().indexOf("mappings")!=-1)
														{
													%>
														<STRONG>Manage Mappings</STRONG>
													<%
														}
														else
														{
													%>
													<SPAN class="wikilink"><A href="displayMappings">
														<SPAN class="wikigeneratedlinkcontent">Manage Mappings</SPAN>
													</A></SPAN>
													<%
														}
													%>
												</LI>
												<!--  <LI><SPAN class="wikilink">
													<A href="export">
														<SPAN class="wikigeneratedlinkcontent">Data Export</SPAN>
													</A></SPAN>
												</LI>-->
												<LI><SPAN class="wikilink">
													<A href="http://www.activecomponents.org/">
														<SPAN class="wikigeneratedlinkcontent">Back To Main Page</SPAN>
													</A></SPAN>
												</LI>
											</UL>
										</DIV>
									</DIV>
									<!-- 
									<script type="text/javascript">
										var obj = {div:'xwikinav', no:-1, height:250};
										var acc = createAccordion(obj);
									</script>
									 -->
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
