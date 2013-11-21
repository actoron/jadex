package jadex.bdiv3.examples.disastermanagement.firebrigade;

import jadex.bdiv3.examples.disastermanagement.IExtinguishFireService;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeBDI.ClearChemicals;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeBDI.ExtinguishFire;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.IPojoMicroAgent;

import java.util.Collection;

/**
 *  Fire extinguish service.
 */
@Service
public class ExtinguishFireService implements IExtinguishFireService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess ia;

	//-------- methods --------
	
	/**
	 *  Extinguish a fire.
	 *  @param disaster The disaster.
	 */
	public ITerminableFuture<Void> extinguishFire(final ISpaceObject disaster)
	{
		System.out.println("received ext fire task: "+ia.getComponentIdentifier());

		final FireBrigadeBDI agent = (FireBrigadeBDI)((IPojoMicroAgent)ia).getPojoAgent();
		
		final TerminableFuture<Void> ret = new TerminableFuture<Void>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				Collection<ExtinguishFire> goals = agent.getAgent().getGoals(ExtinguishFire.class);
				if(goals!=null)
				{
					for(ExtinguishFire g: goals)
					{
						System.out.println("Dropping ext after terminate: "+g.getDisaster());
						agent.getAgent().dropGoal(g);
					}
				}
			}
		});;
		
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
				ExtinguishFire exfire = new ExtinguishFire(disaster);
				IFuture<ExtinguishFire> fut = agent.getAgent().dispatchTopLevelGoal(exfire);
				fut.addResultListener(new IResultListener<ExtinguishFire>()
				{
					public void resultAvailable(ExtinguishFire result)
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
		return "ExtinguishFireService, "+ia.getComponentIdentifier();
	}
}
