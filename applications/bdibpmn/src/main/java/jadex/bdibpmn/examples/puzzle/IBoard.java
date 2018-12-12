package jadex.bdibpmn.examples.puzzle;

import jadex.commons.beans.PropertyChangeListener;

import java.util.List;

/**
 *  The interface for the playing board.
 */
public interface IBoard
{
	/** Property event for move made. */
	public static final String MOVE= "move";

	/** Property event for move taken back. */
	public static final String TAKEBACK= "takeback";

	/**
	 *  Get a piece for a location.
	 */
	public Piece getPiece(Position pos);

	/**
	 *  Get possible moves.
	 *  @return Get all possible move.
	 */
	public List getPossibleMoves();

	/**
	 *  Do a move.
	 *  @param move The move.
	 */
	public boolean move(Move move);

	/**
	 *  Takeback a move.
	 */
	public boolean takeback();

	/**
	 *  Test if it is a solution.
	 *  @return True, if solution.
	 */
	public boolean isSolution();

	/**
	 *  Get all moves made so far.
	 */
	public List getMoves();

	/**
	 *  Get all moves made so far.
	 */
	public Move getLastMove();

	/**
	 *  Get the board size.
	 */
	public int getSize();

	/**
	 *  Get the current board position.
	 */
	public List getCurrentPosition();

	/**
	 *  Test if aposition is free.
	 */
	public boolean isFreePosition(Position pos);

	/**
	 *  Test if the last move was with a white piece.
	 *  When no move was made, it return true.
	 *  @return True, is last move was with white piece.
	 */
	public boolean wasLastMoveWhite();

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
