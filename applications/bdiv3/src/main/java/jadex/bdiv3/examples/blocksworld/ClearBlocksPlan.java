package jadex.bdiv3.examples.blocksworld;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.examples.blocksworld.BlocksworldAgent.ClearGoal;

/**
 *  Clear a block.
 */
@Plan
public class ClearBlocksPlan extends StackBlocksPlan
{
    /**
     *
     */
    @PlanPrecondition
    public boolean checkExistsBlock()
    {
        boolean ret = ((ClearGoal)goal).getBlock().getUpper()!=null;
//        System.out.println("do clear: "+ret);
        return ret;
    }

    /**
     *
     */
    public Block getBlock()
    {
        return ((ClearGoal)goal).getBlock().getUpper();
    }

    /**
     *
     */
    public Block getTarget()
    {
        return ((ClearGoal)goal).getTarget();
    }
}

