package org.activecomponents.examples.puzzleng;

import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Simple test implementation of puzzle server side.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class PuzzleAgent	implements IPuzzleService
{
	/**
	 *  Announce that a new game has started.
	 */
	public IFuture<Void>	newGame(int size)
	{
		System.out.println("New Game: "+size);
		return IFuture.DONE;
	}

	/**
	 *  Announce that a move has been made.
	 */
	public IFuture<Void>	moved(int x, int y)
	{
		System.out.println("Moved: "+x+", "+y);
		return IFuture.DONE;		
	}
	
	/**
	 *  Announce that the last move has been taken back.
	 */
	public IFuture<Void>	takenBack()
	{
		System.out.println("Taken Back");
		return IFuture.DONE;
	}
}
