package jadex.web.examples.puzzle.servlet;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.future.ThreadSuspendable;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.HighscoreEntry;
import jadex.web.examples.puzzle.IPuzzleService;
import jadex.web.examples.puzzle.Move;
import jadex.web.examples.puzzle.Position;

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
		// Force AWT thread on system class loader instead of web app class loader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
		Toolkit.getDefaultToolkit();
		Thread.currentThread().setContextClassLoader(cl);
		
		String[]	args	= new String[]
		{
//			"-deftimeout", "-1",
			"-kernels", "\"micro, component, bdi\"",
			"-logging", "true",
			"-awareness", "false",
			"-gui", "false",
			"-extensions", "null",
			"-component", "jadex/web/examples/puzzle/agent/Sokrates.agent.xml"
		};
		int	timeout	= 30000;
		platform = Starter.createPlatform(args).get(timeout);
		puzzle = platform.searchService(new ServiceQuery<>(IPuzzleService.class, ServiceScope.PLATFORM)).get(timeout);
	}
	
	/**
	 *  Shut down the platform on exit.
	 */
	public void destroy()
	{
		int	timeout	= 30000;
		platform.killComponent().get(timeout);
	}
	
	//-------- methods --------
	
	/**
	 *  Called on each web request.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		System.out.println("getContextPath: "+request.getContextPath());
//		System.out.println("getServletPath: "+request.getServletPath());
//		System.out.println("getPathInfo: "+request.getPathInfo());
		request.setAttribute("puzzlepath", request.getContextPath()+request.getServletPath());
		
		HttpSession	session	= request.getSession();
		Board	board	= (Board)session.getAttribute("board");
		if(board==null)
		{
			board	= new Board();
			session.setAttribute("board", board);
			session.setAttribute("hint_count", Integer.valueOf(0));
		}
		String	view	= "/WEB-INF/jsp/puzzle/index.jsp"; 
		if("/gamerules".equals(request.getPathInfo()))
		{
			view	= "/WEB-INF/jsp/puzzle/gamerules.jsp";
		}
		else if("/highscore".equals(request.getPathInfo()))
		{
			view	= "/WEB-INF/jsp/puzzle/highscore.jsp";
			int	timeout	= 30000;
			SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(board.getSize()).get(timeout);
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
		request.setAttribute("puzzlepath", request.getContextPath()+request.getServletPath());
		HttpSession	session	= request.getSession();
		Board	board	= (Board)session.getAttribute("board");
		if(board==null)
		{
			board	= new Board();
			session.setAttribute("board", board);
			session.setAttribute("hint_count", Integer.valueOf(0));
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
			if(move!=null)
			{
				board.move(move);
			}
			
			if(board.isSolution())
			{
				int	timeout	= 30000;
				SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(board.getSize()).get(timeout);
				int	hint_count	= ((Integer)session.getAttribute("hint_count")).intValue();
				HighscoreEntry	entry	= new HighscoreEntry("dummy", board.getSize(), hint_count);
				if(entries.isEmpty() ||	entry.compareTo(entries.last())<0)
				{
					request.setAttribute("is_highscore", Boolean.TRUE);
				}
			}
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
			session.setAttribute("hint_count", Integer.valueOf(0));
		}
		else if("/highscore".equals(request.getPathInfo()))
		{
			view	= "/WEB-INF/jsp/puzzle/highscore.jsp";
			int	size	= Integer.parseInt(request.getParameter("boardsize"));
			int	timeout	= 30000;
			SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(size).get(timeout);
			request.setAttribute("highscore", entries.toArray(new HighscoreEntry[entries.size()]));
		}
		else if("/hint".equals(request.getPathInfo()))
		{
			Object hint;
			int	timeout	= Integer.parseInt(request.getParameter("timeout"))*1000;
			int	hint_count	= ((Integer)session.getAttribute("hint_count")).intValue();
			session.setAttribute("timeout", request.getParameter("timeout"));
			session.setAttribute("hint_count", Integer.valueOf(hint_count+1));
			ThreadSuspendable	sus	= new ThreadSuspendable();
			try
			{
				// It is safe to use the board object, as it is passed as a copy to the service automatically.
				Move	move	= puzzle.hint(board, timeout).get(timeout+500);
				hint	= move.getStart();
			}
			catch(Exception e)
			{
				hint	= "Sorry, no solution found.";
			}
			request.setAttribute("hint", hint);
		}
		else if("/addhighscore".equals(request.getPathInfo()))
		{
			int	hint_count	= ((Integer)session.getAttribute("hint_count")).intValue();
			String	player	= request.getParameter("player");
			session.setAttribute("player", player);
			HighscoreEntry	entry	= new HighscoreEntry(player, board.getSize(), hint_count);
			int	timeout	= 30000;
			try
			{
				puzzle.addHighscore(entry).get(timeout);
			}
			catch(Exception e)
			{
				request.setAttribute("error", "Sorry, your highscore entry was just replaced.");
			}
			
			// Save platform settings in case of server crash
			ISettingsService	settings	= platform.searchService( new ServiceQuery<>( ISettingsService.class, ServiceScope.PLATFORM)).get(timeout);
			settings.saveProperties().get(timeout);
			
			view	= "/WEB-INF/jsp/puzzle/highscore.jsp";
			SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(board.getSize()).get(timeout);
			request.setAttribute("highscore", entries.toArray(new HighscoreEntry[entries.size()]));
		}
		RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
		rd.forward(request, response);
	}
}
