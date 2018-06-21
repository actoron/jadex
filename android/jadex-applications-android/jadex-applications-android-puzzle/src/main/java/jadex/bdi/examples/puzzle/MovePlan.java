package jadex.bdi.examples.puzzle;

import jadex.android.puzzle.ui.GuiProxy;
import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.bdiv3.examples.puzzle.Move;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Make a move and dispatch a subgoal for the next.
 */
public class MovePlan extends Plan
{
	//-------- attributes --------

	/** The move to try. */
	protected Move move;

	/** The recursion depth. */
	protected int depth;

	/** The move delay. */
	protected long delay;

	/** The board. */
	protected IBoard board;

	private GuiProxy proxy;


	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		this.proxy = (GuiProxy) getBeliefbase().getBelief("gui_proxy").getFact();
		this.move = (Move)getParameter("move").getValue();
		this.depth = ((Integer)getParameter("depth").getValue()).intValue();
		this.delay = ((Long)getBeliefbase().getBelief("move_delay").getFact()).longValue();
		this.board = (IBoard)getBeliefbase().getBelief("board").getFact();
		
		int triescnt = ((Integer)getBeliefbase().getBelief("triescnt").getFact()).intValue()+1;
		getBeliefbase().getBelief("triescnt").setFact(Integer.valueOf(triescnt));
		print("Trying "+move+" ("+triescnt+") ", depth);

		// Atomic block is required, because a micro plan step occurs when property change event
		// from the board occurs. This means that no further bean listeners will be notified (e.g gui).
		startAtomic();
		board.move(move);
		endAtomic();
		
		waitFor(delay);
		
		//if(!board.isSolution()) // Comment out this line when using goal target condition in the adf.
		{
			IGoal mm = createGoal("makemove");
			mm.getParameter("depth").setValue(Integer.valueOf(depth+1));
			dispatchSubgoalAndWait(mm);
		}
	}

	/**
	 *  The plan failure code.
	 */
	public void failed()
	{
		print("Failed "+move, depth);
		assert board.getLastMove().equals(move): "Tries to takeback wrong move.";
		board.takeback();
		
		waitFor(delay);
	}

	/**
	 *  The plan passed code.
	 */
	public void passed()
	{
		print("Succeeded "+move, depth);
	}

	/**
	 *  The plan aborted code.
	 */
	public void aborted()
	{
		if(getBeliefbase().containsBelief("endmem"))
		{
			Long	endmem	= (Long) getBeliefbase().getBelief("endmem").getFact();
			if(endmem==null)
			{
				endmem	= Long.valueOf(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
				getBeliefbase().getBelief("endmem").setFact(endmem);
			}
		}
		
//		waitFor(300000);
		
		print("Aborted "+move, depth);
//		print("Aborted "+(isAbortedOnSuccess()?
//			"on success: ": "on failure: ")+move, depth);
	}

	/**
	 *  Print out an indented string.
	 *  @param text The text.
	 *  @param indent The number of cols to indent.
	 */
	protected void print(String text, int indent)
    {
		StringBuilder sb = new StringBuilder();
        for(int x=0; x<indent; x++) {
            sb.append(" ");
        }
        sb.append(text);

        proxy.showMessage(sb.toString());
    }
}
