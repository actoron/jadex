package jadex.bdiv3.examples.puzzle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;

/**
 * The View Board represents the puzzle board and the pegs.
 */
public class JackBoard implements IBoard, Serializable
{
	protected Piece white_piece = new Piece(true);
	protected Piece black_piece = new Piece(false);
	protected List<Move> moves = new ArrayList<Move>();
	public SimplePropertyChangeSupport pcs = new SimplePropertyChangeSupport(this);

	/**
	 * Get a piece for a location.
	 */
	public Piece getPiece(Position pos)
	{
		int piece = get(pos.getX(), pos.getY());
		if(piece==1)
			return white_piece;
		else if(piece==-1)
			return black_piece;
		else
			return null;
	}

	/**
	 * Get possible moves.
	 * @return Get all possible move.
	 */
	public List<Move> getPossibleMoves()
	{
		List<Move> ret = new ArrayList<Move>();
		List<Position> ts = moves(the_hole);
		for(int i = 0; i<ts.size(); i++)
			ret.add(new Move(ts.get(i), the_hole));
		return ret;
	}

	/**
	 * Do a move.
	 * @param move The move.
	 */
	public boolean move(Move move)
	{
		// todo: check?
		int p = get(move.getStart());
		set(0, move.getStart());
		the_hole = move.getStart();
		set(p, move.getEnd());
		moves.add(move);
		pcs.firePropertyChange("solution", null, move);
//		pcs.firePropertyChange(IBoard.MOVE, null, move);
		return true;
	}

	/**
	 * Takeback a move.
	 */
	public boolean takeback()
	{
		if(moves.size()==0)
			return false;

		Move move = (Move)moves.get(moves.size()-1);
		int p = get(move.getEnd());
		set(p, move.getStart());
		set(0, move.getEnd());
		the_hole = move.getEnd();
		moves.remove(moves.size()-1);
		pcs.firePropertyChange("solution", null, move);
//		pcs.firePropertyChange(IBoard.TAKEBACK, null, move);
		return true;
	}

	/**
	 * Test if it is a solution.
	 * @return True, if solution.
	 */
	public boolean isSolution()
	{
		return solution();
	}

	/**
	 * Get all moves made so far.
	 */
	public List<Move> getMoves()
	{
		return moves;
	}

	/**
	 * Get all moves made so far.
	 */
	public Move getLastMove()
	{
		return moves.size()>0? (Move)moves.get(moves.size()-1): null;
	}

	/**
	 * Test if the last move was with a white piece.
	 * When no move was made, it return true.
	 * @return True, is last move was with white piece.
	 */
	public boolean wasLastMoveWhite()
	{
		Move move = getLastMove();
		return move==null || get(move.getEnd())==1;
	}

	/**
	 *  Get the board size.
	 */
	public int getSize()
	{
		return 5;
	}

	/**
	 *  Get the current board position.
	 */
	public List<Piece> getCurrentPosition()
	{
		List<Piece> ret = new ArrayList<Piece>();
		for(int y=0; y<5; y++)
		{
			for(int x=0; x<5; x++)
			{
				ret.add(getPiece(new Position(x, y)));
			}
		}
		return ret;
	}

	/**
	 *  Test if a position is free.
	 */
	public boolean isFreePosition(Position pos)
	{
		int x = pos.getX();
		int y = pos.getY();
		return get(x,y)==0;
	}

	/**
	 * The int [][] board represents the game board, with 0 marking
	 * the hole, and 4 marking out-of-limit coordinates. Otherwise
	 * there are "1" pieces and "-1" pieces. Note that we don't
	 * distinguish pieces individually in this representation.
	 */
	int[][] board = {
		{
			1,
			1,
			1,
			4,
			4},
		{
			1,
			1,
			1,
			4,
			4},
		{
			1,
			1,
			0,
			-1,
			-1},
		{
			4,
			4,
			-1,
			-1,
			-1},
		{
			4,
			4,
			-1,
			-1,
			-1}};
	int last = 4;
	Position the_hole = new Position(2, 2);
	/**
	 * The static int [][] move_check_table represents available
	 * moves, as coordinate offsets and piece colour. E.g., with the
	 * hole in <x:y>, then a "-1" piece in <x+1:y> is eligible, as is
	 * a "-1" piece in <x+2,y> if there also is a "1" piece in
	 * <x+1:y>.
	 */
	// x, y, piece_col
	static int[][] move_check_table = {
		{
			1,
			0,
			-1},
		{
			0,
			1,
			-1},
		{
			-1,
			0,
			1},
		{
			0,
			-1,
			1},
		{
			2,
			0,
			-1,
			1,
			0,
			1},
		{
			0,
			2,
			-1,
			0,
			1,
			1},
		{
			-2,
			0,
			1,
			-1,
			0,
			-1},
		{
			0,
			-2,
			1,
			0,
			-1,
			-1}};

	/**
	 * The moves() method computes possible moves to <x:y>,
	 * represented by the Positiones of pieces to move.
	 */
	List<Position> moves(int x, int y)
	{
		List<Position> v = new ArrayList<Position>();
		for(int i = 0; (i<move_check_table.length); i++)
			check(v, move_check_table[i], x, y)
					;
		return v;
	}

	/**
	 * The check() method processes a move_check_table entry, and adds
	 * a Position to the Vector when the entry applies.
	 */
	void check(List<Position> v, int[] m, int x, int y)
	{
		int x1 = (x+m[0]);
		int y1 = (y+m[1]);
		if((get(x1, y1)==m[2]))
		{
			if(((m.length==3) || (get((x+m[3]), (y+m[4]))==m[5])))
			{
				Position s = new Position(x1, y1);
				v.add(s);
			}
		}
	}

	int get(int x, int y)
	{
		if(((((x<0) || (x>= 5)) || (y<0)) || (y>= 5)))
			return 4;
		return board[x][y];
	}

	int get(Position s)
	{
		return get(s.x, s.y);
	}

	void set(int v, int x, int y)
	{
		board[x][y] = v;
	}

	void set(int v, Position s)
	{
		set(v, s.x, s.y);
	}

	boolean solution()
	{
		if((board[2][2]!=0))
			return false;
		for(int i = 0; (i<3); i++)
		{
			for(int j = 0; (j<3); j++)
			{
				if((board[i][j]>0))
					return false;
			}
		}
		return true;
	}

	List<Position> moves(Position hole)
	{
		return moves(hole.x, hole.y);
	}

	boolean isJumpMove(int x, int y)
	{
		int dx = Math.abs((the_hole.x-x));
		int dy = Math.abs((the_hole.y-y));
		return ((dx==2) || (dy==2));
	}

	boolean isJumpMove(Position s)
	{
		return isJumpMove(s.x, s.y);
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

	/** 
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "JackBoard(moves=" + moves + ", the_hole=" + the_hole + ")";
	}
}

