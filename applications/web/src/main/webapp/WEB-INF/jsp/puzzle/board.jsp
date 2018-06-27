<%@ page import="jadex.web.examples.puzzle.*" %>
<%@ page import="java.util.*" %>
<jsp:useBean class="jadex.web.examples.puzzle.Board" id="board" scope="session"/>

		<table cellpadding="0" cellspacing="0" align="center">
<%
		Object	hint	= request.getAttribute("hint");
		Position hint_pos = hint instanceof Position? (Position)hint: null;

			List<Move> moves = board.getPossibleMoves();
			HashSet<Position> posmoves = new HashSet<Position>();
			for(int i=0; i<moves.size(); i++)
			{
				posmoves.add(moves.get(i).getStart());
			}

			for(int row=0; row<board.getSize(); row++)
			{
%>
		<tr>
<%
				for(int col=0; col<board.getSize(); col++)
				{
					Position	pos	= new Position(col, row);
					if(col==0)
					{
						if(row==0)
						{
%>
			<td>&nbsp;</td>
<%

							for(int i=0; i<board.getSize(); i++)
							{
								Position tst	= new Position(i, row);
%>
			<td align="center" class="boardlabel"><%= tst.getPrintableX() %></td>
<%
							}
%>
		</tr>
		<tr>
<%
						}
%>
			<td valign="middle" class="boardlabel"><%= pos.getPrintableY() %>&nbsp;</td>
<%
					}
					String	pic	= null;
					Piece	piece	= board.getPiece(pos);
					if(piece!=null)
					{
						if(pos.equals(hint_pos))
						{
							pic	= piece.isWhite() ? "white_piece_hint.png" : "red_piece_hint.png";
						}
						else if(posmoves.contains(pos))
						{
							pic	= piece.isWhite() ? "white_piece_glow.png" : "red_piece_glow.png";
						}
						else
						{
							pic	= piece.isWhite() ? "white_piece.png" : "red_piece.png";
						}
					}
					else if(board.isFreePosition(pos))
					{
						pic	= "empty_field.png";
					}

					if(posmoves.contains(pos))
					{
%>
			<form action="<%= request.getAttribute("puzzlepath") %>/move" method="post">
				<td><input type="image" src="<%= request.getContextPath() %>/resources/puzzle/<%= pic%>"/></td>
				<input type="hidden" src="<%= request.getContextPath() %>/resources/puzzle/<%= pic%>" name="start" value="<%=pos%>"/>
			</form>
			<!-- <a href="move?start=<%= ""+pos%>"><img src="<%= request.getContextPath() %>/resources/puzzle/<%= pic%>" border="0"/></a></td> -->
<%
					}
					else if(pic!=null)
					{
%>
			<td><img border="0" src="<%= request.getContextPath() %>/resources/puzzle/<%= pic %>"></td>
<%
					}
					else
					{
%>
			<td>&nbsp;</td>
<%
					}
				}
%>
		</tr>
<%
			}
%>
	</table>
	