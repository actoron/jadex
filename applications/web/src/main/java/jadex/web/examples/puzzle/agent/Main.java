package jadex.web.examples.puzzle.agent;

import java.util.SortedSet;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.ThreadSuspendable;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.HighscoreEntry;
import jadex.web.examples.puzzle.IPuzzleService;
import jadex.web.examples.puzzle.Move;

/**
 *  Simple test for the puzzle agent.
 * @author Alex
 *
 */
public class Main
{
	public static void	main(String[] s)
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
		IExternalAccess	platform	= Starter.createPlatform(args).get(timeout);
		IPuzzleService	puzzle	= platform.searchService( new ServiceQuery<>( IPuzzleService.class)).get(timeout);
		
		Board	board	= new Board(11);
		int	hints	= 0;
		while( !board.isSolution())
		{
			Move	move	= puzzle.hint(board, 15000).get(timeout);
			hints++;
			System.out.println("Move "+hints+": "+move);
			board.move(move);
		}
		
		try
		{
			puzzle.addHighscore(new HighscoreEntry(platform.getIdentifier().getLocalName(), board.getSize(), hints)).get(timeout);
			System.out.println("New highscore entry!");
		}
		catch(RuntimeException e)
		{
			System.out.println("Sorry, no new highscore entry.");
		}
		
		SortedSet<HighscoreEntry>	entries	= puzzle.getHighscore(board.getSize()).get(timeout);
		int place	= 1;
		for(HighscoreEntry entry : entries)
		{
			System.out.println(""+(place++)+": "+entry.getName()+" used "+entry.getHintCount()+" hints on "+entry.getDate());
		}
		
		platform.killComponent().get(timeout);
	}
}
