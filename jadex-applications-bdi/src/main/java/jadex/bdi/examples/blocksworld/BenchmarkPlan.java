package jadex.bdi.examples.blocksworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Measures Jadex performance by executing several configure goals.
 */
public class BenchmarkPlan	extends Plan
{
	//-------- attributes --------

	/** The number of runs to be performed. */
	protected int	runs;

	/** The number of different goals to be executed per run. */
	protected int	goals;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		this.runs = ((Integer)getParameter("runs").getValue()).intValue();
		this.goals = ((Integer)getParameter("goals").getValue()).intValue();
		System.out.println("Performing benchmark ("+runs+" runs with "+goals+" goals each).");
		long[]	times	= new long[runs];
		long	total	= 0;

		// Perform runs (first run isn't counted).
		for(int run=0; run<=runs; run++)
		{
			long	time	= getTime();

			// Initialize random number generator.
			Random	rand	= new Random(12345678L);

			Block[]	blocks	= (Block[])getBeliefbase().getBeliefSet("blocks").getFacts();

			for(int i=1; i<=goals; i++)
			{
				// Create copies of existing blocks.
				List<Block>	newblocks	= new ArrayList<Block>();
				for(int j=0; j<blocks.length; j++)
				{
					newblocks.add(new Block(blocks[j].number, blocks[j].getColor(), null));
				}

				// Create random configuration of copied blocks.
				Table	table	= new Table();
				List<Block>	targets	= new ArrayList<Block>();
				targets.add(table);
				while(newblocks.size()>0)
				{
					Block	source	= (Block)newblocks.remove(rand.nextInt(newblocks.size()));
					Block	target	= (Block)targets.get(rand.nextInt(targets.size()));
					source.stackOn(target);
					targets.add(source);
					if(!target.isClear())
					{
						targets.remove(target);
					}
				}

				IGoal	achieve	= createGoal("configure");
				achieve.getParameter("configuration").setValue(table);
				achieve.getParameterSet("blocks").addValues(table.getAllBlocks());
				dispatchSubgoalAndWait(achieve);
			}

			if(run!=0)
			{
				// Print intermediate result.
				times[run-1]	= getTime()-time;
				total	+= times[run-1];
				System.out.println("Run "+run+" took "+times[run-1]+" milliseconds.");
			}
			else
				System.out.println("Random generator test: "+rand.nextInt(1234));
			
		}

		// Print total result.
		System.out.println("Benchmark completed.");
		System.out.println("Total time (millis): "+total);
		System.out.println("Avg. time per run (millis): "+(total/runs));
		System.out.println("Avg. time per goal (millis): "+(total/runs/goals));

		// Calculate deviation (= quality of measurement).
		double	deviation	= 0;
		double	avg	= total/(double)runs;
		for(int i=0; i<runs; i++)
		{
			deviation	+= (times[i]-avg) * (times[i]-avg);
		}
		deviation	= Math.sqrt(deviation/runs);
		System.out.println("Standard deviation of runs (millis): "+Math.round(deviation));

//		killAgent();
	}
}

