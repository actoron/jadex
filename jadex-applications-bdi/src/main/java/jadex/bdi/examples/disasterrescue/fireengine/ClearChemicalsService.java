package jadex.bdi.examples.disasterrescue.fireengine;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disasterrescue.IClearChemicalsService;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;

/**
 *   Clear chemicals service.
 */
public class ClearChemicalsService extends BasicService implements IClearChemicalsService
{
	//-------- attributes --------
	
	/** The agent. */
	protected IBDIExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public ClearChemicalsService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IClearChemicalsService.class, null);
		this.agent = (IBDIExternalAccess)agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture clearChemicals(final ISpaceObject disaster)
	{
		final Future ret = new Future();
		
		agent.getGoalbase().getGoals("clear_chemicals").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IEAGoal[] goals = (IEAGoal[])result;
				for(int i=0; i<goals.length; i++)
				{
					System.out.println("Dropping: "+goals[i]);
					goals[i].drop();
				}
				
				agent.createGoal("clear_chemicals").addResultListener(new DefaultResultListener()
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
