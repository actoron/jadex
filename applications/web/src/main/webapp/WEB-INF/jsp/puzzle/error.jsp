<jsp:include page="header.jsp" flush="true"/>

<h1>Error</h1>

<p>
	An error occurred while processing the request.
<%
	if(request.getAttribute("error")!=null)
		out.println(request.getAttribute("error"));
%>
	<br/>
	Go back to <a href="<%= request.getAttribute("puzzlepath") %>/index">puzzle board</a>.
</p>

<jsp:include page="footer.jsp" flush="true"/>