<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<jsp:include page="header.jsp" flush="true"/>
<%
	Exception	exception	= (Exception)request.getAttribute("exception");
	StringWriter	trace	= new StringWriter();
	exception.printStackTrace(new PrintWriter(trace));
%>
<h1>Jadexdoc Problem</h1>
<pre>
<%= trace %>
</pre>
<jsp:include page="footer.jsp" flush="true"/>