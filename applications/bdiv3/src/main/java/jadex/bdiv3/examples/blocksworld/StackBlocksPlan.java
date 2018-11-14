package jadex.bdiv3.examples.blocksworld;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.blocksworld.BlocksworldBDI.ClearGoal;
import jadex.bdiv3.examples.blocksworld.BlocksworldBDI.StackGoal;
import jadex.bdiv3.runtime.IPlan;


/**
 *  Stack a block on top of another.
 */
@Plan
public class StackBlocksPlan	
{
	//-------- attributes --------

	@PlanCapability
	protected BlocksworldBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected Object goal;
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		// Clear blocks.
		
//		try
//		{
//		System.out.println(getClass().getName()+" "+getBlock()+" "+getTarget());
		
		ClearGoal clear = capa.new ClearGoal(getBlock());
		rplan.dispatchSubgoal(clear).get();
		
		clear = capa.new ClearGoal(getTarget());
		rplan.dispatchSubgoal(clear).get();

		
		// Maybe wait before moving block.
		if(capa.getMode().equals(BlocksworldBDI.Mode.SLOW))
		{
			rplan.waitFor(1000).get();
//			waitFor(1000);
		}
		else if(capa.getMode().equals(BlocksworldBDI.Mode.STEP))
		{
			capa.steps.getNextIntermediateResult();
//			waitForInternalEvent("step");
		}

		// Now move block.
		if(!capa.isQuiet())
			System.out.println("Moving '"+getBlock()+"' to '"+getTarget()+"'");

		// This operation has to be performed atomic,
		// because it fires bean changes on several affected blocks. 
//		startAtomic();
		getBlock().stackOn(getTarget());
//		endAtomic();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	/**
	 * 
	 */
	public Block getBlock()
	{
		return ((StackGoal)goal).getBlock();
	}
	
	/**
	 * 
	 */
	public Block getTarget()
	{
		return ((StackGoal)goal).getTarget();
	}
}
