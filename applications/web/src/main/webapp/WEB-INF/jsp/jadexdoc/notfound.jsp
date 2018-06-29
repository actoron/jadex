<jsp:include page="header.jsp" flush="true"/>
<%
	String	file	= (String)request.getAttribute("file");
%>
<h1>Jadexdoc Problem</h1>
<%= file %> could not be found.
<jsp:include page="footer.jsp" flush="true"/>