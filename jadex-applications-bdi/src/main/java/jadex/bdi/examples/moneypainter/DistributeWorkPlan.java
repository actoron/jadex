package jadex.bdi.examples.moneypainter;


import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;

/**
 * 
 */
public class DistributeWorkPlan extends Plan
{
	/**
	 * 
	 */
	public void body()
	{
		int target = ((Integer)getBeliefbase().getBelief("target").getFact()).intValue();
		int money = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
		
		// Create a subgoal for each euro to get
		for(int i=0; i<target-money; i++)
		{
			createOneEuroSubgoal();
		}
		
		waitForEver();
	}
	
	/**
	 * 
	 */
	public void createOneEuroSubgoal()
	{
		final IGoal getone = createGoal("getoneeuro");
		final Object handle = ((GoalFlyweight)getone).getHandle();
		getone.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(getone.isSucceeded())
				{
					int money = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
					getBeliefbase().getBelief("money").setFact(new Integer(money+1));
//					System.out.println("Succeeded: "+handle+"Has money: "+getBeliefbase().getBelief("money").getFact());
				}
				else
				{
					System.out.println("Get money goal failed: "+handle);
//					createOneEuroSubgoal();
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		dispatchSubgoal(getone);
	}
}