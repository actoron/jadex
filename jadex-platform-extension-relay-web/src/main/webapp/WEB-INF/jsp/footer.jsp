<%@page contentType="text/html; charset=UTF-8" %>
<%@page session="false"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="jadex.bridge.VersionInfo"%>
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
												<SPAN>Relay Transport</SPAN>
											</H1>
										</DIV>
										<DIV id="xwikinavAC Tool GuideContent"
											class="accordionTabContentBox">
											<UL>
												<LI>
													<%	if(request.getServletPath().indexOf("history")==-1)
														{
													%>
														<STRONG>Live Platforms</STRONG>
													<%
														}
														else
														{
													%>
													<SPAN class="wikilink"><A href=".">
														<SPAN class="wikigeneratedlinkcontent">Live Platforms</SPAN>
													</A></SPAN>
													<%
														}
													%>
												</LI>
												<LI>
													<%	if(request.getServletPath().indexOf("history")!=-1)
														{
													%>
														<STRONG>Connection History</STRONG>
													<%
														}
														else
														{
													%>
													<!-- <SPAN class="wikilink"><A href="history">
														<SPAN class="wikigeneratedlinkcontent">Connection History</SPAN>
													</A></SPAN> -->
													<%
														}
													%>
												</LI>
												<LI><SPAN class="wikilink">
													<A href="export">
														<SPAN class="wikigeneratedlinkcontent">Data Export</SPAN>
													</A></SPAN>
												</LI>
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
				Copyright (C) 2002-<%= new SimpleDateFormat("yyyy").format(new Date()) %> Lars Braubach, Alexander Pokahr, Kai Jander<br>
				Version <%= VersionInfo.getInstance().getVersion()+" from "+VersionInfo.getInstance().getNumberDateString() %>
			</P>
		</DIV>
	</DIV>
		</DIV>
	</DIV>
 </BODY>
</HTML>
