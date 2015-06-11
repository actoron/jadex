package jadex.bdi.examples.moneypainter;


import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

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
		
		Future<Void> fut = new Future<Void>();
		fut.get();
//		waitForEver();
	}
	
	/**
	 * 
	 */
	public void createOneEuroSubgoal()
	{
		final IGoal getone = createGoal("getoneeuro");
		dispatchSubgoal(getone).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
			}
			
			public void resultAvailable(Void result)
			{
				if(getone.isSucceeded())
				{
					int money = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
					getBeliefbase().getBelief("money").setFact(Integer.valueOf(money+1));
//					System.out.println("Succeeded: "+handle+"Has money: "+getBeliefbase().getBelief("money").getFact());
				}
				else
				{
					System.out.println("Get money goal failed: "+getone);
//					createOneEuroSubgoal();
				}
			}
		});
	}
}