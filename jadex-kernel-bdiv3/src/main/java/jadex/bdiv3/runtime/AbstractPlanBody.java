package jadex.bdiv3.runtime;

import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;

/**
 *  Abstract base class for plan body implementations.
 *   
 */
public abstract class AbstractPlanBody implements IPlanBody
{
	//-------- attributes --------
	
	/** The bdi interpreter. */
	protected IInternalAccess ia;
	
	/** The rplan. */
	protected RPlan rplan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan body.
	 */
	public AbstractPlanBody(IInternalAccess ia, RPlan rplan)
	{
		this.ia = ia;
		this.rplan = rplan;
	}

	//-------- methods --------
	
	/**
	 *  Execute the plan body.
	 */
	public IFuture<Void> executePlanBody()
	{
		try
		{
			final Object reason = rplan.getReason();
			Object res = invokeBody(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, 
				guessParameters(getBodyParameterTypes()));
			if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_PASSED);
						if(reason instanceof RProcessableElement)
							((RProcessableElement)reason).planFinished(ia, rplan);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_FAILED);
						rplan.setException(exception);
						if(reason instanceof RProcessableElement)
							((RProcessableElement)reason).planFinished(ia, rplan);
					}
				});
			}
			else
			{
				rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_PASSED);
				if(reason instanceof RProcessableElement)
					((RProcessableElement)reason).planFinished(ia, rplan);
			}
		}
		catch(Exception e)
		{
			rplan.setException(e);
			rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_FAILED);
			if(rplan.getReason() instanceof RProcessableElement)
				((RProcessableElement)rplan.getReason()).planFinished(ia, rplan);
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Invoke the plan body.
	 */
	public abstract Object invokeBody(Object agent, Object[] params);
	
	/**
	 *  Get the body parameters.
	 */
	public abstract Class<?>[] getBodyParameterTypes();
	
	/**
	 *  Method that tries to guess the parameters for the method call.
	 */
	public Object[] guessParameters(Class<?>[] ptypes)
	{
		// Guess parameters
//		Class<?>[] ptypes = body.getParameterTypes();
		
		final Object reason = rplan.getReason();
		Object pojope = null;
		if(reason instanceof RProcessableElement)
			pojope = ((RProcessableElement)reason).getPojoElement();
		
		Object[] params = new Object[ptypes.length];
		
		for(int i=0; i<ptypes.length; i++)
		{
			if(reason!=null && SReflect.isSupertype(reason.getClass(), ptypes[i]))
			{
				params[i] = reason;
			}
			else if(pojope!=null && SReflect.isSupertype(pojope.getClass(), ptypes[i]))
			{
				params[i] = pojope;
			}
			else if(SReflect.isSupertype(RPlan.class, ptypes[i]))
			{
				params[i] = rplan;
			}
		}
				
		return params;
	}

	/**
	 *  Get the rplan.
	 *  @return The rplan.
	 */
	public RPlan getRPlan()
	{
		return rplan;
	}
}
