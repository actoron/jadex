package jadex.web.examples.puzzle.servlet;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.HighscoreEntry;
import jadex.web.examples.puzzle.IPuzzleService;
import jadex.web.examples.puzzle.Move;
import jadex.web.examples.puzzle.Position;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *  Front controller servlet for puzzle application.
 */
public class PuzzleDispatcherServlet extends HttpServlet
{
	//-------- attributes --------
	
	/** The platform. */
	protected IExternalAccess	platform;
	
	/** The puzzle service. */
	protected IPuzzleService	puzzle;
	
	//-------- constructors --------
	
	/**
	 *  Init the servlet by starting the Jadex platform
	 *  and fecthing the puzzle service.
	 */
	public void init() throws ServletException
	{
		String[]	args	= new String[]
		{
//			"-logging_level", "java.util.logging.Level.INFO",
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null",
			"-component", "jadex/web/examples/puzzle/agent/Sokrates.agent.xml"
		};
		int	timeout	= 300000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform	= Starter.createPlatform(args).get(sus, timeout);
		puzzle	= SServiceProvider.getService(platform.getServiceProvider(), IPuzzleService.class).get(sus, timeout);
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 300000;
		ThreadSuspendable	sus	= new ThreadSuspendable();
		platform.killComponent().get(sus, timeout);
	}
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession	session	= request.getSession();
		Board	board	= (Board)session.getAttribute("board");
		if(board==null)
		{
			board	= new Board();
			session.setAttribute("board", board);
		}
		String	view	= "/WEB-INF/jsp/puzzle/index.jsp"; 
		if("/gamerules".equals(request.getPathInfo()))
		{
			view	= "/WEB-INF/jsp/puzzle/gamerules.jsp";
		}
		else if("/highscore".equals(request.getPathInfo()))
		{
			view	= "/WEB-INF/jsp/puzzle/highscore.jsp";
			int	timeout	= 300000;
			ThreadSuspendable	sus	= new ThreadSuspendable();
			SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(board.getSize()).get(sus, timeout);
			request.setAttribute("highscore", entries.toArray(new HighscoreEntry[entries.size()]));
		}
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
		rd.forward(request, response);
	}
	
	/**
	 *  Called on each form submit.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession	session	= request.getSession();
		Board	board	= (Board)session.getAttribute("board");
		if(board==null)
		{
			board	= new Board();
			session.setAttribute("board", board);
		}
		String	view	= "/WEB-INF/jsp/puzzle/index.jsp"; 
		if("/move".equals(request.getPathInfo()))
		{
			String	start	= request.getParameter("start");
			Position	pos	= Position.fromString(start);
			List<Move>	moves	= board.getPossibleMoves();
			Move	move	= null;
			for(int i=0; move==null && i<moves.size(); i++)
			{
				if(moves.get(i).getStart().equals(pos))
				{
					move	= moves.get(i);
				}
			}
			board.move(move);
		}
		else if("/takeback".equals(request.getPathInfo()))
		{
			board.takeback();
		}
		else if("/new_game".equals(request.getPathInfo()))
		{
			int	size	= Integer.parseInt(request.getParameter("boardsize"));
			board	= new Board(size);
			session.setAttribute("board", board);
		}
		else if("/hint".equals(request.getPathInfo()))
		{
			int	timeout	= Integer.parseInt(request.getParameter("timeout"))*1000;
			ThreadSuspendable	sus	= new ThreadSuspendable();
			puzzle.hint(board, timeout).get(sus, timeout+500);
		}
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
		rd.forward(request, response);
	}
}
