package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bridge.IInternalAccess;

/**
 * 
 */
public class RServiceCall extends RProcessableElement
{
	/** The finished flag. */
	boolean finished;
	
	/**
	 *  Create a new ServiceCall. 
	 */
	public RServiceCall(MProcessableElement modelelement, InvocationInfo pojoelement)
	{
		super(modelelement, pojoelement);
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

