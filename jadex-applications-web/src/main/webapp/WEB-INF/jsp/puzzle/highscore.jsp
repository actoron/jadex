<%@page import="jadex.web.examples.puzzle.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<jsp:include page="header.jsp" flush="true"/>

<%
	HighscoreEntry[] highscore = (HighscoreEntry[])request.getAttribute("highscore");
	Object	error = request.getAttribute("error");
	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
%>

<h1>Highscore <%= (highscore!=null && highscore.length>0)?"(Board Size "+highscore[0].getBoardSize()+")": "" %></h1>

<p>
	<table width="80%">
		<tr><th>Rank</th><th>Name</th><th>Hints</th><th>Date</th></tr>
<%
	for(int i=0; i<10; i++)
	{
%>
		<tr>
			<td><%= i+1 %></td>
			<td><%= highscore!=null && i<highscore.length? highscore[i].getName(): "---"%></td>
			<td><%= highscore!=null && i<highscore.length? ""+highscore[i].getHintCount(): "---"%></td>
			<td><%= highscore!=null && i<highscore.length? ""+df.format(highscore[i].getDate()): "---"%></td>
		</tr>
<%
	}
%>
	</table>
	<br><br>
	
	<%= error!=null ? error: "" %> 

	<form action="<%= request.getAttribute("puzzlepath") %>/highscore" method="post">
		<input type="submit" name="dummy" value="Show highscore"/> Size:<select name="boardsize">
<%
		for(int i=3; i<=11; i+=2)
		{
			if(highscore!=null && highscore.length>0 && i==highscore[0].getBoardSize())
			{
%>
				<option value="<%=i%>" selected><%=i%></option>
<%
			}
			else
			{
%>
				<option value="<%=i%>"><%=i%></option>
<%
			}
		}
%>
		</select>
	</form>
</p>

<jsp:include page="footer.jsp" flush="true"/>