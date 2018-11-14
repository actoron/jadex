package jadex.bdi.examples.blocksworld;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Stack a block on top of another.
 */
public class StackBlocksPlan	extends Plan
{
	//-------- constants --------

	/** The normal execution mode. */
	public static final String	MODE_NORMAL	= "Normal";

	/** The slow execution mode. */
	public static final String	MODE_SLOW	= "Slow";

	/** The step execution mode. */
	public static final String	MODE_STEP	= "Step";

	//-------- attributes --------

	/** The block to be moved. */
	protected Block	block;

	/** The block on to which to put the other block. */
	protected Block	target;

	/** The execution mode. */
	protected String	mode;

	/** The quiet flag (do not printout messages). */
	protected boolean	quiet;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		this.block	= (Block)getParameter("block").getValue();
		this.target	= (Block)getParameter("target").getValue();
		this.mode	= (String)getBeliefbase().getBelief("mode").getFact();
		this.quiet	= ((Boolean)getBeliefbase().getBelief("quiet").getFact()).booleanValue();
		
		// Clear blocks.
		IGoal clear = createGoal("clear");
		clear.getParameter("block").setValue(block);
		dispatchSubgoalAndWait(clear);

		clear = createGoal("clear");
		clear.getParameter("block").setValue(target);
		dispatchSubgoalAndWait(clear);

		// Maybe wait before moving block.
		if(mode.equals(MODE_SLOW))
		{
			waitFor(1000);
		}
		else if(mode.equals(MODE_STEP))
		{
			waitForInternalEvent("step");
		}

		// Now move block.
		if(!quiet)
			System.out.println("Moving '"+block+"' to '"+target+"'");

//		// This operation has to be performed atomic,
//		// because it fires bean changes on several affected blocks. 
		startAtomic();
		block.stackOn(target);
		endAtomic();
	}
}
