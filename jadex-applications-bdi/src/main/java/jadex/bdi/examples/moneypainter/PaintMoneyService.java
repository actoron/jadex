package jadex.bdi.examples.moneypainter;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class PaintMoneyService implements IPaintMoneyService
{
	@ServiceComponent
	protected IBDIInternalAccess agent;
	
	/**
	 *  Paint one euro.
	 */
	public IFuture<Void> paintOneEuro()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(agent.getBeliefbase().getBelief("painter").getFact()==null)
		{
			final IGoal paint = agent.getGoalbase().createGoal("getoneeuro");
			final Object handle = ((GoalFlyweight)paint).getHandle();
			paint.addGoalListener(new IGoalListener()
			{
				public void goalFinished(AgentEvent ae)
				{
					if(paint.isSucceeded())
					{
//						System.out.println("painter success: "+handle);
						ret.setResult(null);
					}
					else
					{
//						System.out.println("painter failure: "+handle);
						ret.setException(paint.getException()!=null? paint.getException(): new RuntimeException());
					}
				}
				
				public void goalAdded(AgentEvent ae)
				{
				}
			});
			agent.getGoalbase().dispatchTopLevelGoal(paint);
		}
		else
		{
//			System.out.println("painter failure (busy)");
			ret.setException(new RuntimeException("Painter busy: "+agent.getConfigurationName()));
		}
	
		return ret;
	}
}
