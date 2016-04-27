<%@page import="jadex.web.examples.puzzle.*" %>
<jsp:useBean class="jadex.web.examples.puzzle.Board" id="board" scope="session"/>

<%
	Object	hint_count	= request.getSession().getAttribute("hint_count");
	Object	hint	= request.getAttribute("hint");
	Object	error	= request.getAttribute("error");
	Object	is_highscore	= request.getAttribute("is_highscore");

	if(board!=null && board.isSolution())
	{
%>
		<h2>Congratulations!</h2>
		<p class="noborder">
			You solved the puzzle.
		</p>
<%
	}
	else if(board!=null && board.getPossibleMoves().isEmpty())
	{
%>
		<h2>Failed</h2>
		<p class="noborder">
			No more possible moves.
		</p>
<%
	}
	else if(hint instanceof String)
	{
%>
		<h2>Hint</h2>
		<p class="noborder">
			<%= hint %>
		</p>
<%
	}
	else if(error instanceof String)
	{
%>
		<h2>Error</h2>
		<p class="noborder">
			<%= error %>
		</p>
<%
	}
	if(is_highscore!=null)
	{
%>
		<form action="<%= request.getAttribute("puzzlepath") %>/addhighscore" method="post">
			<input type="submit" name="dummy" value="Add to Highscore"/>
			<input type="text" value="<%= request.getSession().getAttribute("player")!=null?
				""+request.getSession().getAttribute("player")
				: ""%>" name="player"/>
			<input type="hidden" name="hint_count" value="<%=hint_count%>"/>
			<input type="hidden" name="boardsize" value="<%=board.getSize()%>"/>
		</form>
<%
	}
%>

	<table width=100% cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<form action="<%= request.getAttribute("puzzlepath") %>/new_game" method="post">
					<input type="submit" name="dummy" value="New Game"/>
					Size:<select name="boardsize">
<%
		for(int i=3; i<=11; i+=2)
		{
			if(board!=null && i==board.getSize())
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
			</td>
		</tr>
		<tr>
			<td>
				<form action="<%= request.getAttribute("puzzlepath") %>/hint" method="post">
					<input type="submit" name="dummy" value="Hint"/>
					Timeout [secs]:<input type="text" size="2" value="<%= request.getSession()
						.getAttribute("timeout")!=null?""+request.getSession().getAttribute("timeout")
						: "15"%>" name="timeout"/>
					<br>Hints used: <%=hint_count%>
				</form>
			</td>
		</tr>
	</table>
