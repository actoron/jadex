package jadex.bdiv3.examples.puzzle;

import java.util.Comparator;

/**
 *  Sort moves according to a strategy.
 */
public class MoveComparator implements Comparator<Move>
{
	//-------- constants --------

	/** No strategy: try moves in order of appearance. */
	public static final String STRATEGY_NONE	= "none";
	
	/** The strategy preferring jump moves, but ignoring colors. */
	public static final String STRATEGY_LONG	= "long";

	/** The strategy preferring jump moves of same color. */
	public static final String STRATEGY_SAME_LONG	= "same_long";
	
	/** The strategy preferring jump moves of different colors. */
	public static final String STRATEGY_ALTER_LONG	= "alter_long";
	
	//-------- attributes --------
	
	/** The board (required for checking which piece is in a given position). */
	protected IBoard	board;
	
	/** The strategy. */
	protected String	strategy;
	
	//-------- constructors --------
	
	/**
	 *  Create a move comparator.
	 */
	public MoveComparator(IBoard board, String strategy)
	{
		this.strategy	= strategy;
		this.board	= board;
	}
	
	//-------- Coparator interface --------

	/**
	 *  Compare two moves.
	 *  @return A negative number when the first move should come before the second.
	 */
	public int compare(Move move1, Move move2)
	{
		boolean same1 = board.wasLastMoveWhite()==board.getPiece(move1.getStart()).isWhite();
		boolean same2 = board.wasLastMoveWhite()==board.getPiece(move2.getStart()).isWhite();
		
		int compare_same	= same1 && !same2 ? -1
				: same2 && !same1 ? 1
				: 0;
		
		int	compare_long	= move1.isJumpMove() && !move2.isJumpMove() ? -1
							: move2.isJumpMove() && !move1.isJumpMove() ? 1
							: 0;
		
		int	ret	= 0;
		
		if(STRATEGY_LONG.equals(strategy))
		{
			ret	= compare_long;	
		}
		else if(STRATEGY_SAME_LONG.equals(strategy))
		{
			ret	= compare_same!=0 ? compare_same : compare_long;
//			ret	= compare_long!=0 ? compare_long : compare_same;
		}
		else if(STRATEGY_ALTER_LONG.equals(strategy))
		{
			ret	= compare_same!=0 ? -compare_same : compare_long;
//			ret	= compare_long!=0 ? compare_long : -compare_same;
		}
		
		return ret;
	}
}
