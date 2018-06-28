package jadex.bdi.examples.blocksworld;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Stack blocks according to the target configuration.
 */
public class ConfigureBlocksPlan	extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Table configuration	= (Table)getParameter("configuration").getValue();
//		getParameterSet("blocks").addValues(configuration.getAllBlocks());
		
		// Create set of blocks currently on the table.
		Table	table	= (Table)getBeliefbase().getBelief("table").getFact();
		Block[]	blocks	= table.getAllBlocks();
		Set<Block>	oldblocks	= new HashSet<Block>();
		for(int i=0; i<blocks.length; i++)
			oldblocks.add(blocks[i]);
		blocks	= (Block[])getBeliefbase().getBeliefSet("blocks").getFacts(); 

		// Move all blocks in configuration to desired target location.
		Block[][]	stacks	= configuration.getStacks();
		for(int i=0; i<stacks.length; i++)
		{
			for(int j=0; j<stacks[i].length; j++)
			{
				// Get blocks from beliefs.
				Color	blockcolor	= stacks[i][j].getColor();
				Color	targetcolor	= stacks[i][j].getLower().getColor();
				Block	block	= null;
				Block	target	= stacks[i][j].getLower()==configuration ? table : null;
				for(int k=0; (block==null || target==null) && k<blocks.length; k++)
				{
					if(block==null && blocks[k].getColor().equals(blockcolor))
					{
						block	= blocks[k];
					}
					if(target==null && blocks[k].getColor().equals(targetcolor))
					{
						target	= blocks[k];
					}
				}
				if(block==null || target==null)
					throw new RuntimeException("No such blocks: "+stacks[i][j]+", "+stacks[i][j].getLower());
	
				// Create stack goal.
				IGoal stack = createGoal("stack");
				stack.getParameter("block").setValue(block);
				stack.getParameter("target").setValue(target);
				dispatchSubgoalAndWait(stack);
	
				// Remove processed block from oldblocks.
				oldblocks.remove(block);
			}
		}

		// Move old blocks, which are not part of configuration, to bucket.
		Object	bucket	= getBeliefbase().getBelief("bucket").getFact();
		for(Iterator<Block> i=oldblocks.iterator(); i.hasNext(); )
		{
			// Create stack goal.
 			IGoal stack = createGoal("stack");
			stack.getParameter("block").setValue(i.next());
			stack.getParameter("target").setValue(bucket);
			dispatchSubgoalAndWait(stack);
		}
	}
}
