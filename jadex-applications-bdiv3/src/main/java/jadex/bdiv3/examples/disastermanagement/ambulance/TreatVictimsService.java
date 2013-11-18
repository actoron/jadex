package jadex.bdiv3.examples.disastermanagement.ambulance;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.examples.disastermanagement.DeliverPatientTask;
import jadex.bdiv3.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3.examples.disastermanagement.ambulance.AmbulanceBDI.TreatVictims;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.IPojoMicroAgent;

/**
 *   Treat victims service.
 */
@Service
public class TreatVictimsService implements ITreatVictimsService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	//-------- methods --------
	
	/**
	 *  Treat victims.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> treatVictims(final ISpaceObject disaster)
	{
		System.out.println("received treat victims task: "+ia.getComponentIdentifier());
		
//		final IGoal tv = (IGoal)agent.getGoalbase().createGoal("treat_victims");
		
		final TreatVictims tv = new TreatVictims(disaster);
		final AmbulanceBDI agent = (AmbulanceBDI)((IPojoMicroAgent)ia).getPojoAgent();
		IFuture<TreatVictims> fut = agent.getAgent().dispatchTopLevelGoal(tv);
		
		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public boolean checkTermination(Exception reason)
			{
				return !((Boolean)agent.getMoveCapa().getMyself().getProperty(DeliverPatientTask.PROPERTY_PATIENT)).booleanValue();
			}
			
			public void terminated(Exception reason)
			{
				agent.getAgent().dropGoal(tv);
//				tv.drop();
			}
		});
		
		fut.addResultListener(new IResultListener<TreatVictims>()
		{
			public void resultAvailable(TreatVictims result)
			{
				ret.setResultIfUndone(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		
//		tv.getParameter("disaster").setValue(disaster);
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
//		agent.getGoalbase().dispatchTopLevelGoal(tv);
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "TreatVictimsService, "+ia.getComponentIdentifier();
	}
}

	