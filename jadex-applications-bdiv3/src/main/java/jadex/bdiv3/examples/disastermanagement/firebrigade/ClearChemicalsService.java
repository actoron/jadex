package jadex.bdiv3.examples.disastermanagement.firebrigade;

import jadex.bdiv3.examples.disastermanagement.IClearChemicalsService;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeBDI.ClearChemicals;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeBDI.ExtinguishFire;
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

import java.util.Collection;

/**
 *   Clear chemicals service.
 */
@Service
public class ClearChemicalsService implements IClearChemicalsService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	//-------- methods --------
	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> clearChemicals(final ISpaceObject disaster)
	{
		System.out.println("received clear chemicals task: "+ia.getComponentIdentifier());

		final FireBrigadeBDI agent = (FireBrigadeBDI)((IPojoMicroAgent)ia).getPojoAgent();
		
		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				Collection<ClearChemicals> goals = agent.getAgent().getGoals(ClearChemicals.class);
				if(goals!=null)
				{
					for(ClearChemicals g: goals)
					{
//						System.out.println("Dropping: "+goals[i]);
						agent.getAgent().dropGoal(g);
					}
				}
			}
		});
		
		Collection<ExtinguishFire> exgoals = agent.getAgent().getGoals(ExtinguishFire.class);
		if(exgoals.size()>0)
		{
			ret.setExceptionIfUndone(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
		}
		else
		{
			Collection<ClearChemicals> ccgoals = agent.getAgent().getGoals(ClearChemicals.class);
			if(ccgoals.size()>0)
			{
				ret.setExceptionIfUndone(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
			}
			else
			{
				ClearChemicals cc = new ClearChemicals(disaster);
				IFuture<ClearChemicals> fut = agent.getAgent().dispatchTopLevelGoal(cc);
				fut.addResultListener(new IResultListener<ClearChemicals>()
				{
					public void resultAvailable(ClearChemicals result)
					{
						ret.setResultIfUndone(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ClearChemicalsService, "+ia.getComponentIdentifier();
	}
}
