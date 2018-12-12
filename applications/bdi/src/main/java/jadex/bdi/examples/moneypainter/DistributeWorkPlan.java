package jadex.bdi.examples.moneypainter;


import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
		Future<Void> ret = new Future<Void>();
		
		int target = ((Integer)getBeliefbase().getBelief("target").getFact()).intValue();
		int money = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
		
		// Create a subgoal for each euro to get
		CounterResultListener<Void> lis = new CounterResultListener<Void>(target-money, new DelegationResultListener<Void>(ret));
		for(int i=0; i<target-money; i++)
		{
			createOneEuroSubgoal().addResultListener(lis);
		}
		
		ret.get();
//		waitForEver();
	}
	
	/**
	 * 
	 */
	public IFuture<Void> createOneEuroSubgoal()
	{
		Future<Void> ret = new Future<Void>();
		
		final IGoal getone = createGoal("getoneeuro");
		dispatchSubgoal(getone).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(exception);
			}
			
			public void resultAvailable(Void result)
			{
				if(getone.isSucceeded())
				{
					int money = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
					getBeliefbase().getBelief("money").setFact(Integer.valueOf(money+1));
					System.out.println("Has money: "+getBeliefbase().getBelief("money").getFact());
				}
				else
				{
					System.out.println("Get money goal failed: "+getone);
//					createOneEuroSubgoal();
				}
			}
		});
		
		return ret;
	}
}