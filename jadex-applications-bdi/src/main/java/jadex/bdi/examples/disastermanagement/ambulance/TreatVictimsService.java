package jadex.bdi.examples.disastermanagement.ambulance;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.DeliverPatientTask;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;

/**
 *   Treat victims service.
 */
public class TreatVictimsService extends BasicService implements ITreatVictimsService
{
	//-------- attributes --------
	
	/** The agent. */
	protected ICapability agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public TreatVictimsService(ICapability agent)
	{
		super(agent.getServiceProvider().getId(), ITreatVictimsService.class, null);
		this.agent = agent;
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
		
		IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("treat_victims");
		if(goals.length>0)
		{
			ret.setException(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
		}
		else
		{
			final IGoal tv = (IGoal)agent.getGoalbase().createGoal("treat_victims");
			tv.getParameter("disaster").setValue(disaster);
			tv.addGoalListener(new IGoalListener()
			{
				public void goalFinished(AgentEvent ae)
				{
//					System.out.println("tv fin: "+agent.getAgentName());
					if(tv.isSucceeded())
						ret.setResult(null);
					else
						ret.setException(tv.getException());
				}
				
				public void goalAdded(AgentEvent ae)
				{
				}
			});
//			System.out.println("tv start: "+agent.getAgentName());
			agent.getGoalbase().dispatchTopLevelGoal(tv);
		}
		
		return ret;
	}
	
	/**
	 *  Abort extinguishing fire.
	 *  @return Future, null when done.
	 */
	public IFuture abort()
	{
		final Future ret = new Future();
		
		ISpaceObject myself	= (ISpaceObject)agent.getBeliefbase().getBelief("myself").getFact();
		if(((Boolean)myself.getProperty(DeliverPatientTask.PROPERTY_PATIENT)).booleanValue())
		{
			ret.setException(new IllegalStateException("Can not abort with patient on board."));			
		}
		else
		{
			IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("treat_victims");
			for(int i=0; i<goals.length; i++)
			{
//				System.out.println("Dropping: "+goals[i]);
				goals[i].drop();
			}
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "TreatVictimsService, "+agent.getComponentIdentifier();
	}
}
