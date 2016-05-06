package jadex.web.examples.puzzleng;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Server side for puzzle ng.
 *  Called directly from browser via jadex.js.
 */
@Service
public interface IPuzzleService
{
	/**
	 *  Announce that a new game has started.
	 */
	public IFuture<Void>	newGame(int size);

	/**
	 *  Announce that a move has been made.
	 */
	public IFuture<Void>	moved(int x, int y);
	
	/**
	 *  Announce that the last move has been taken back.
	 */
	public IFuture<Void>	takenBack();
}
