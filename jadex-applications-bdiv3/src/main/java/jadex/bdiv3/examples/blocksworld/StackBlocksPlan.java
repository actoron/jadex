package jadex.bdiv3.examples.blocksworld;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPrecondition;
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
	protected StackGoal goal;
	
	//-------- methods --------

	/**
	 * 
	 */
	@PlanPrecondition
	public boolean checkExistsBlock()
	{
		boolean ret = false;
		for(Block block: capa.getBlocks())
		{
			if(block.getLower().equals(goal.getBlock()))
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
//<precondition>
//	(select one Block $block from $beliefbase.blocks
//	where $block.getLower()==$goal.block)!=null
//</precondition>
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		// Clear blocks.
		
		ClearGoal clear = capa.new ClearGoal(goal.getBlock());
		rplan.dispatchSubgoal(clear).get();
		
		clear = capa.new ClearGoal(goal.getTarget());
		rplan.dispatchSubgoal(clear).get();

		
		// Maybe wait before moving block.
		if(capa.getMode().equals(BlocksworldBDI.Mode.SLOW))
		{
			rplan.waitFor(1000).get();
//			waitFor(1000);
		}
		else if(capa.getMode().equals(BlocksworldBDI.Mode.STEP))
		{
//			waitForInternalEvent("step");
		}

		// Now move block.
		if(!capa.isQuiet())
			System.out.println("Moving '"+goal.getBlock()+"' to '"+goal.getTarget()+"'");

		// This operation has to be performed atomic,
		// because it fires bean changes on several affected blocks. 
//		startAtomic();
		goal.getBlock().stackOn(goal.getTarget());
//		endAtomic();
	}
}
