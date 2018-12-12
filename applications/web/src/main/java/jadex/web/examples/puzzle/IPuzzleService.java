package jadex.web.examples.puzzle;

import java.util.SortedSet;

import jadex.commons.future.IFuture;

/**
 *  Operations provided by the puzzle agent.
 */
public interface IPuzzleService
{
	/**
	 *  Solve the game and give a hint on the next move.
	 *  @param board	The current board state.
	 *  @param timeout	A timeout to stop, when no solution is found in time (-1 for no timeout).
	 *  @return The tile to move next.
	 *  @throws Exception in future, when puzzle can not be solved in time.
	 */
	public IFuture<Move>	hint(Board board, long timeout);
	
	/**
	 *  Add a highscore entry and save the highscore list.
	 *  @param entry	The highscore entry.
	 */
	public IFuture<Void>	addHighscore(HighscoreEntry entry);
	
	/**
	 *  Get the highscore entries for a given board size.
	 *  @param size	The board size (e.g. 3, 5, ...).
	 *  @return	The sorted set of highscore entries (highest entry first).
	 */
	public IFuture<SortedSet<HighscoreEntry>>	getHighscore(int size);
}
