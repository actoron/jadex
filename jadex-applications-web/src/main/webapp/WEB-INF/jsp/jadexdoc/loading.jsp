<jsp:include page="header.jsp" flush="true"/>
<%
	String	file	= (String)request.getAttribute("file");
%>
<div id="loading">
<h1>Loading Model</h1>
Please wait while loading <%= file %>.
<p align="center">
<img src="<%= request.getContextPath() %>/resources/jadexdoc/jadex_loading.gif"/>
</p>
</div>
<jsp:include page="footer.jsp" flush="true"/>