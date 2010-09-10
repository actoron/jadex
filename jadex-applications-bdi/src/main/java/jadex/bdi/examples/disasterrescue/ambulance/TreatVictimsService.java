package jadex.bdi.examples.disasterrescue.ambulance;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disasterrescue.IFireExtinguishService;
import jadex.bdi.examples.disasterrescue.ITreatVictimsService;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;

/**
 *   Treat victims service.
 */
public class TreatVictimsService extends BasicService implements ITreatVictimsService
{
	//-------- attributes --------
	
	/** The agent. */
	protected IBDIExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public TreatVictimsService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IFireExtinguishService.class, null);
		this.agent = (IBDIExternalAccess)agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Treat victims.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture treatVictims(final ISpaceObject disaster)
	{
		final Future ret = new Future();
		
		agent.getGoalbase().getGoals("treat_victims").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IEAGoal[] goals = (IEAGoal[])result;
				for(int i=0; i<goals.length; i++)
				{
					System.out.println("Dropping: "+goals[i]);
					goals[i].drop();
				}
				
				agent.createGoal("treat_victims").addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IEAGoal exfire = (IEAGoal)result;
						exfire.setParameterValue("disaster", disaster);
						agent.dispatchTopLevelGoalAndWait(exfire).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								ret.setResult(null);
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ClearChemicalsService, "+agent.getComponentIdentifier();
	}
}
