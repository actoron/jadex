<jsp:include page="header.jsp" flush="true"/>

<h1>Game Rules</h1>
<p class="noborder">
	This is a puzzle game managed by one agent per game.
	It consists of a board with white and red
	pieces. The objective is to switch the positions
	of the pieces (i.e. move white pieces down-right and
	move red pieces up-left). The following rules for
	making a move exist:
	<ul class="noborder">
		<li> White pieces can only move right or down to an adjacent free field.</li>
		<li> white pieces can jump right or down over a red piece to a free field.</li>
		<li> Red pieces can only move up or left with the same restrictions as white pieces (moving or jumping).</li>
		<li> The color of a piece to move is not specified.</li>
	</ul>
</p>

<jsp:include page="footer.jsp" flush="true"/>