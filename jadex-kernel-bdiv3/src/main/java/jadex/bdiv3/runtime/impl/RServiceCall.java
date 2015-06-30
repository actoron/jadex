package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bridge.IInternalAccess;
import jadex.commons.IValueFetcher;

/**
 *  Runtime element for handling a service call as plan. 
 */
public class RServiceCall extends RFinishableElement
{
	/** The finished flag. */
	boolean finished;
	
	/**
	 *  Create a new ServiceCall. 
	 */
	public RServiceCall(MProcessableElement modelelement, InvocationInfo pojoelement, IInternalAccess agent)
	{
		super(modelelement, pojoelement, agent, null);
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$call";
	}
	
	/**
	 *  Get the invocation info.
	 */
	public InvocationInfo getInvocationInfo()
	{
		return (InvocationInfo)getPojoElement();
	}
	
	/**
	 *  Called when a plan has finished.
	 */
	public void planFinished(IInternalAccess ia, IInternalPlan rplan)
	{
		super.planFinished(ia, rplan);
		finished = true;
		if(rplan instanceof RPlan)
		{
			PlanLifecycleState state = ((RPlan)rplan).getLifecycleState();
			if(state.equals(RPlan.PlanLifecycleState.FAILED))
			{
				setException(rplan.getException());
			}
		}
		notifyListeners();
	}
	
	
	/**
	 *  Test if element is succeeded.
	 */
	public boolean isSucceeded()
	{
		return finished && exception==null;
	}
	
	/**
	 *  Test if element is failed.
	 */
	public boolean isFailed()
	{
		return finished && exception!=null;
	}
}

