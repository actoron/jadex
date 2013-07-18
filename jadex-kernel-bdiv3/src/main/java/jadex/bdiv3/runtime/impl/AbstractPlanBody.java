package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
	 * 
	 */
	public Object getBody(Object agent)
	{
		return null;
	}
	
	/**
	 *  Execute the plan body.
	 */
	public IFuture<Void> executePlan()
	{
		final Future<Void> ret = new Future<Void>();
		
		String	pname	= rplan.getModelElement().getName();
		String	capaname	= pname.indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)==-1
			? null : pname.substring(0, pname.lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR));
		final Object agent	= ((BDIAgentInterpreter)((BDIAgent)ia).getInterpreter()).getCapabilityObject(capaname);

		internalInvokePart(agent, 0).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				if(RPlan.PLANLIFECYCLESTATE_ABORTED.equals(rplan.getLifecycleState()))
				if(rplan.getException()!=null)
				{
					exceptionOccurred(rplan.getException());
				}
				else
				{
					internalInvokePart(agent, 1)
						.addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							rplan.setLifecycleState(RPlan.PlanLifecycleState.PASSED);
//							if(reason instanceof RProcessableElement)
//								((RProcessableElement)reason).planFinished(ia, rplan);
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							rplan.setLifecycleState(RPlan.PlanLifecycleState.FAILED);
							rplan.setException(exception);
//							if(reason instanceof RProcessableElement)
//								((RProcessableElement)reason).planFinished(ia, rplan);
							ret.setException(exception);
						}
					});
				}
			}
			
			public void exceptionOccurred(final Exception exception)
			{
//				int next = RPlan.PLANLIFECYCLESTATE_ABORTED.equals(rplan.getLifecycleState())? 3: 2; 
//				int next = rplan.getException() instanceof PlanAbortedException? 3: 2;
				int next = exception instanceof PlanAbortedException? 3: 2;
				
				rplan.setException(exception);
				internalInvokePart(agent, next)
					.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if(!rplan.isFinished())
						{
							rplan.setLifecycleState(exception instanceof PlanAbortedException
								? RPlan.PlanLifecycleState.ABORTED : RPlan.PlanLifecycleState.FAILED);
						}
//						if(reason instanceof RProcessableElement)
//							((RProcessableElement)reason).planFinished(ia, rplan);
						ret.setException(exception);
					}
					
					public void exceptionOccurred(Exception ex)
					{
						if(!rplan.isFinished())
						{
							rplan.setLifecycleState(exception instanceof PlanAbortedException
								? RPlan.PlanLifecycleState.ABORTED : RPlan.PlanLifecycleState.FAILED);
						}
//						if(reason instanceof RProcessableElement)
//							((RProcessableElement)reason).planFinished(ia, rplan);
						ret.setException(exception);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> internalInvokePart(Object agent, int part)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{			
			Object res = null;
			if(part==0) 
			{
//				System.out.println("body of: "+rplan);
				res = invokeBody(agent, guessParameters(getBodyParameterTypes()));
			}
			else if(part==1)
			{
//				System.out.println("passed of: "+rplan);
				res = invokePassed(agent, guessParameters(getPassedParameterTypes()));
			}
			else if(part==2)
			{
//				System.out.println("failed of: "+rplan);
				res = invokeFailed(agent, guessParameters(getFailedParameterTypes()));
			}
			else if(part==3)
			{
//				System.out.println("aborted of: "+rplan);
				res = invokeAborted(agent, guessParameters(getAbortedParameterTypes()));
			}
			
			if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(null);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		catch(BodyAborted ba)
		{
			ret.setException(new PlanAbortedException());
		}
		
		return ret;
	}
	
	/**
	 *  Invoke the plan body.
	 */
	public abstract Object invokeBody(Object agent, Object[] params) throws BodyAborted;

	/**
	 *  Invoke the plan passed method.
	 */
	public abstract Object invokePassed(Object agent, Object[] params);

	/**
	 *  Invoke the plan failed method.
	 */
	public abstract Object invokeFailed(Object agent, Object[] params);

	/**
	 *  Invoke the plan aborted method.
	 */
	public abstract Object invokeAborted(Object agent, Object[] params);
	
	/**
	 *  Get the body parameters.
	 */
	public abstract Class<?>[] getBodyParameterTypes();
	
	/**
	 *  Get the passed parameters.
	 */
	public abstract Class<?>[] getPassedParameterTypes();

	/**
	 *  Get the failed parameters.
	 */
	public abstract Class<?>[] getFailedParameterTypes();

	/**
	 *  Get the aborted parameters.
	 */
	public abstract Class<?>[] getAbortedParameterTypes();

	/**
	 *  Method that tries to guess the parameters for the method call.
	 */
	// Todo: parameter annotations (currently only required for event injection)
	public Object[] guessParameters(Class<?>[] ptypes)
	{
		if(ptypes==null)
			return null;
		
		return ((BDIAgentInterpreter)((BDIAgent)ia).getInterpreter())
			.getInjectionValues(ptypes, null, rplan.getModelElement(), null, rplan, null);
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
