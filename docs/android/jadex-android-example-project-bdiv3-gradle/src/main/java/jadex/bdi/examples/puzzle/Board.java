package jadex.bdi.examples.puzzle;

import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.bdiv3.examples.puzzle.Move;
import jadex.bdiv3.examples.puzzle.Piece;
import jadex.bdiv3.examples.puzzle.Position;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *  The board containing places, pieces and played moves.
 */
public class Board implements IBoard, Serializable
{
	//-------- attributes --------

	/** The moves. */
	protected ArrayList moves;

	/** The pieces. */
	protected HashMap pieces;

	/** The size. */
	protected int size;

	/** The hole position. */
	public Position hole_pos;

	/** The helper object for bean events. */
	//public ConcurrentPropertyChangeSupport pcs;
	public SimplePropertyChangeSupport pcs;

	//-------- constructors --------

	/**
	 *  Create a new board.
	 */
	public Board()
	{
		this(5);
	}

	/**
	 *  Create a new board.
	 */
	public Board(int size)
	{
		this.moves = new ArrayList();
		this.pieces = new HashMap();
		this.size = size;
		//this.pcs = new ConcurrentPropertyChangeSupport(this);
		this.pcs = new SimplePropertyChangeSupport(this);

		// Initialize the board with pieces.
		int middle = size/2;
		this.hole_pos = new Position(middle, middle);
		for(int x=0; x<size; x++)
		{
			for(int y=0; y<size; y++)
			{
				if(!(x==middle && y==middle))
				{
					if(x<=middle && y<=middle)
					{
						//System.out.println("w: "+x+" "+y+" ");
						pieces.put(new Position(x, y), new Piece(true));
					}
					else if(x>=middle && y>=middle)
					{
						//System.out.println("b: "+x+" "+y+" ");
						pieces.put(new Position(x, y), new Piece(false));
					}
				}
			}
		}
	}

	/**
	 *  Get a piece for a location.
	 */
	public synchronized Piece getPiece(Position pos)
	{
		return (Piece)pieces.get(pos);
	}

	/**
	 *  Get possible moves.
	 *  @return Get all possible move.
	 */
	public synchronized List getPossibleMoves()
	{
		List ret = new ArrayList();

		int hx = hole_pos.getX();
		int hy = hole_pos.getY();

		int[] cols = new int[]{-1,-1,1,1,-1,-1,1,1};
		Position[] fig_pos = new Position[]{
			new Position(hx+1, hy), // b
			new Position(hx, hy+1), // b
			new Position(hx-1, hy), // w
			new Position(hx, hy-1), // w
			new Position(hx+2, hy), // b
			new Position(hx, hy+2), // b
			new Position(hx-2, hy), // w
			new Position(hx, hy-2)  // w
		};

		for(int i=0; i<fig_pos.length; i++)
		{
			int white = cols[i];
			boolean jump = i>=fig_pos.length/2;
			Piece piece = getPiece(fig_pos[i]);
			if(piece!=null)
			{
				int white_piece = piece.isWhite()?1 : -1;
				if(white_piece==white)
				{
					if(!jump)
					{
						assert isPossibleMove(new Move(fig_pos[i], hole_pos));
						ret.add(new Move(fig_pos[i], hole_pos));
					}
					else
					{
						Piece jp = getPiece(fig_pos[i-fig_pos.length/2]);
						if(jp!=null && jp.isWhite()!=piece.isWhite())
						{
							assert isPossibleMove(new Move(fig_pos[i], hole_pos));
							ret.add(new Move(fig_pos[i], hole_pos));
						}
					}
				}
			}
		}

		//assert ret.equals(getOldPossibleMoves()):ret+" "+getOldPossibleMoves();
		//System.out.println("PosMovs: "+ret);
		return ret;
	}

	/**
	 *  Do a move.
	 *  @param move The move.
	 */
	public boolean move(Move move)
	{
		synchronized(this)
		{
			if(!isPossibleMove(move))
				return false;
	
			Piece piece = getPiece(move.getStart());
			pieces.remove(move.getStart());
			pieces.put(move.getEnd(), piece);
			moves.add(move);
			hole_pos = move.getStart();
		}
		
		// Fire property change outside of synchronization to avoid deadlocks.
//		pcs.firePropertyChange(MOVE, null, move);
		pcs.firePropertyChange("solution", null, move);	// Hack!!! Change for every move required for GUI.
		return true;
	}

	/**
	 *  Takeback a move.
	 */
	public boolean takeback()
	{
		Move move;
		synchronized(this)
		{
			if(moves.size()==0)
				return false;
	
			move = (Move)moves.get(moves.size()-1);
			Piece piece = getPiece(move.getEnd());
			pieces.remove(move.getEnd());
			pieces.put(move.getStart(), piece);
			moves.remove(moves.size()-1);
			hole_pos = move.getEnd();
		}
		
		// Fire property change outside of synchronization to avoid deadlocks.
//		pcs.firePropertyChange(TAKEBACK, null, move);
		pcs.firePropertyChange("solution", null, move);	// Hack!!! Change for every move required for GUI.
		return true;
	}

	/**
	 *  Test if it is a solution.
	 *  @return True, if solution.
	 */
	public synchronized boolean isSolution()
	{
		int middle = size/2;
		if(!isFreePosition(new Position(middle, middle)))
			return false;

		boolean ret = true;
		for(int y=0; y<size && ret; y++)
		{
			for(int x=0; x<size && ret; x++)
			{
				if(!(x==middle && y==middle))
				{
					if(x<=middle && y<=middle && getPiece(new Position(x, y)).isWhite())
						ret = false;
					else if(x>=middle && y>=middle && !getPiece(new Position(x, y)).isWhite())
						ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get all moves made so far.
	 */
	public synchronized List getMoves()
	{
		return Collections.unmodifiableList(moves);
	}

	/**
	 *  Get all moves made so far.
	 */
	public synchronized Move getLastMove()
	{
		return moves.size()>0? (Move)moves.get(moves.size()-1): null;
	}

	/**
	 *  Test if the last move was with a white piece.
	 *  When no move was made, it return true.
	 *  @return True, is last move was with white piece.
	 */
	public synchronized boolean wasLastMoveWhite()
	{
		boolean ret = true;
		if(moves.size()>0)
		{
			Move last = (Move)moves.get(moves.size()-1);
			ret = getPiece(last.getEnd()).isWhite();
		}
		return ret;
	}

	/**
	 *  Test if aposition is free.
	 */
	public synchronized boolean isFreePosition(Position pos)
	{
		int middle = size/2;
		int x = pos.getX();
		int y = pos.getY();
		// There is no piece on the square and the square is
		// on the board and it is no forbidden position.
		return pieces.get(pos)==null
			&& (x>=0 && y>=0 && x<size && y<size)
			&& (x<=middle && y<=middle || x>=middle && y>=middle);
	}

	/**
	 *  Get a piece for a location.
	 */
	protected boolean isPossibleMove(Move move)
	{
		// Check if on start position is a piece.
		// Check if the end position is free.
		Piece piece = getPiece(move.getStart());
		if(piece==null || !isFreePosition(move.getEnd()))
			return false;

		int turn = piece.isWhite()? 1: -1;
		int xs = move.getStart().getX();
		int ys = move.getStart().getY();
		int xe = move.getEnd().getX();
		int ye = move.getEnd().getY();

		// Check if normal move and check if jump move.
		boolean normalmove = ((xe-xs)*turn==1 && ye-ys==0) || ((ye-ys)*turn==1 && xe-xs==0);
		Position xin = new Position(xs+turn, ys);
		Position yin = new Position(xs, ys+turn);
		boolean jumpmove = ((xe-xs)*turn==2 && ye-ys==0 && !isFreePosition(xin) && getPiece(xin).isWhite()!=piece.isWhite())
			|| ((ye-ys)*turn==2 && xe-xs==0 && !isFreePosition(yin) && getPiece(yin).isWhite()!=piece.isWhite());
		if(!normalmove && !jumpmove)
			return false;
		/*if(!(((xe-xs)*turn==1 && ye-ys==0) || ((ye-ys)*turn==1 && xe-xs==0))
			&& !(((xe-xs)*turn==2 && ye-ys==0 && isFreePosition(new Position(xs+turn, ys)))
			|| ((ye-ys)*turn==2 && xe-xs==0 && isFreePosition(new Position(xs, ys+turn)))))
			return false;*/
		return true;
	}

	/**
	 *  Get the board size.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 *  Get the current board position.
	 */
	public synchronized List getCurrentPosition()
	{
		List ret = new ArrayList();
		for(int y=0; y<size; y++)
		{
			for(int x=0; x<size; x++)
			{
				ret.add(getPiece(new Position(x, y)));
			}
		}
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 * /
	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		for(int y=0; y<size; y++)
		{
			for(int x=0; x<size; x++)
			{
				sbuf.append("x="+x+" y="+y+" :"+getPiece(new Position(x, y)));
			}
		}
		return sbuf.toString();
	}*/

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
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		Board board = new Board(5);
		//System.out.println(board);
		List moves = board.getPossibleMoves();
		System.out.println(moves);
		board.move((Move)moves.get(0));

		moves = board.getPossibleMoves();
		System.out.println(moves);
		board.move((Move)moves.get(0));

		System.out.println(board.isPossibleMove(new Move(new Position(2, 0), new Position(2, 2))));
		System.out.println(board.isPossibleMove(new Move(new Position(4, 2), new Position(5,2))));
		board.move((Move)moves.get(0));
		System.out.println(board.getPossibleMoves());
	}
}
