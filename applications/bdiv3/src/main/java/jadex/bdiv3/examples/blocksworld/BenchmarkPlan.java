package jadex.bdiv3.examples.blocksworld;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.blocksworld.BlocksworldBDI.ConfigureGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;


/**
 *  Measures Jadex performance by executing several configure goals.
 */
@Plan
public class BenchmarkPlan
{
	//-------- attributes --------

	/** The number of runs to be performed. */
	protected int	runs;

	/** The number of different goals to be executed per run. */
	protected int	goals;

	@PlanCapability
	protected BlocksworldBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected ConfigureGoal goal;

	
	/**
	 *  Create new plan.
	 *  @param runs
	 *  @param goals
	 */
	public BenchmarkPlan(int runs, int goals)
	{
		this.runs = runs;
		this.goals = goals;
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		System.out.println("Performing benchmark ("+runs+" runs with "+goals+" goals each).");
		long[]	times	= new long[runs];
		long	total	= 0;

		// Perform runs (first run isn't counted).
		for(int run=0; run<=runs; run++)
		{
			long	time	= getTime();

			// Initialize random number generator.
			Random	rand	= new Random(12345678L);

			Block[]	blocks	= (Block[])capa.getBlocks().toArray(new Block[0]);

			for(int i=1; i<=goals; i++)
			{
				// Create copies of existing blocks.
				ArrayList	newblocks	= new ArrayList();
				for(int j=0; j<blocks.length; j++)
				{
					newblocks.add(new Block(blocks[j].number, blocks[j].getColor(), null));
				}

				// Create random configuration of copied blocks.
				Table	table	= new Table();
				ArrayList	targets	= new ArrayList();
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

				Set<Block> bs = SUtil.arrayToSet(table.getAllBlocks());
				ConfigureGoal configure = capa.new ConfigureGoal(table, bs);
				rplan.dispatchSubgoal(configure).get();
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

//		killComponent();
	}
	
	/**
	 * 
	 */
	protected long getTime()
	{
		IClockService cs = (IClockService)capa.getAgent().getComponentFeature(IRequiredServicesFeature.class).getService("clock").get();
		return cs.getTime();
	}
}

