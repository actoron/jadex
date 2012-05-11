package jadex.bdi.examples.disastermanagement.ambulance;

import jadex.bdi.examples.disastermanagement.DeliverPatientTask;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *   Treat victims service.
 */
@Service
public class TreatVictimsService implements ITreatVictimsService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IBDIInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Treat victims.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> treatVictims(final ISpaceObject disaster)
	{
		final IGoal tv = (IGoal)agent.getGoalbase().createGoal("treat_victims");
		
		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public boolean checkTermination(Exception reason)
			{
				ISpaceObject myself	= (ISpaceObject)agent.getBeliefbase().getBelief("myself").getFact();
				return !((Boolean)myself.getProperty(DeliverPatientTask.PROPERTY_PATIENT)).booleanValue();
			}
			
			public void terminated(Exception reason)
			{
				tv.drop();
			}
		});
		
		tv.getParameter("disaster").setValue(disaster);
		tv.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(tv.isSucceeded())
				{
					ret.setResultIfUndone(null);
				}
				else
				{
					ret.setExceptionIfUndone(
						tv.getException()!=null ? tv.getException() : new RuntimeException("aborted"));
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		agent.getGoalbase().dispatchTopLevelGoal(tv);
		
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

	