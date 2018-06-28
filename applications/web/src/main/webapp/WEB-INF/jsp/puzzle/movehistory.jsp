<%@page import="jadex.web.examples.puzzle.*" %>
<%@page import="java.util.List" %>
<jsp:useBean class="jadex.web.examples.puzzle.Board" id="board" scope="session"/>

<%
	if(board!=null)
	{
		List<Move>	moves	= board.getMoves();
%>
	<form action="<%= request.getAttribute("puzzlepath") %>/takeback" method="post">
		All Moves<br/>
		<select name="moves" size="<%= board.getSize()*3-3 %>">
<%
		for(int i=0; i<moves.size(); i++)
		{
			if(i==moves.size()-1)
			{
%>
			<option selected value="<%= moves.size()-i %>"><%= (i+1)+". "+moves.get(i) %></option>
<%
			}
			else
			{
%>
			<option value="<%= moves.size()-i %>"><%= (i+1)+". "+moves.get(i) %></option>
<%
			}
		}
%>
			<option value="0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
		</select>
		<br/><br/>
		<input type="submit" name="dummy" value="Take Back"/>
	</form>
<%
	}
%>