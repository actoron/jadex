<jsp:include page="header.jsp" flush="true"/>

<h1>Welcome to the Puzzle Application</h1>

<p>
	<table cellpadding="20">
		<tr>
			<td valign="top">
				<jsp:include page="board.jsp" flush="true"/>
			</td>
			<td align="center" valign="top">
				<jsp:include page="movehistory.jsp" flush="true"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<jsp:include page="boardcontrols.jsp" flush="true"/>
			</td>
		</tr>
	</table>
</p>

<jsp:include page="footer.jsp" flush="true"/>