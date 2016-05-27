package jadex.bdi.examples.disastermanagement.ambulance;

import jadex.bdi.examples.disastermanagement.DeliverPatientTask;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IResultListener;
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
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Treat victims.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> treatVictims(final ISpaceObject disaster)
	{
		final IBDIXAgentFeature capa = agent.getComponentFeature(IBDIXAgentFeature.class);
		
		final IGoal tv = (IGoal)capa.getGoalbase().createGoal("treat_victims");
		
		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public boolean checkTermination(Exception reason)
			{
				ISpaceObject myself	= (ISpaceObject)capa.getBeliefbase().getBelief("myself").getFact();
				return !((Boolean)myself.getProperty(DeliverPatientTask.PROPERTY_PATIENT)).booleanValue();
			}
			
			public void terminated(Exception reason)
			{
				tv.drop();
			}
		});
		
		tv.getParameter("disaster").setValue(disaster);
//		tv.addGoalListener(new IGoalListener()
//		{
//			public void goalFinished(AgentEvent ae)
//			{
//				if(tv.isSucceeded())
//				{
//					ret.setResultIfUndone(null);
//				}
//				else
//				{
//					ret.setExceptionIfUndone(
//						tv.getException()!=null ? tv.getException() : new RuntimeException("aborted"));
//				}
//			}
//			
//			public void goalAdded(AgentEvent ae)
//			{
//			}
//		});
		capa.getGoalbase().dispatchTopLevelGoal(tv).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
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
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
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
		return "TreatVictimsService, "+agent.getComponentIdentifier();
	}
}

	