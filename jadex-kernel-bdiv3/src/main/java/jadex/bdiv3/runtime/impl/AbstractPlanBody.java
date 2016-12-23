package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SUtil;
import jadex.commons.future.ErrorException;
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
	
	/** The currently running plan part. */
	protected Future<Object>	partfuture;
	
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
	 *  Get the body impl (object that is actually invoked).
	 *  @return The object representing the body. 
	 */
	public Object getBody()
	{
		return null;
	}
	
	/**
	 *  Execute the plan body.
	 */
	public IFuture<Void> executePlan()
	{
		final Future<Void> ret = new Future<Void>();
		
		internalInvokePart(0).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
//				if(RPlan.PLANLIFECYCLESTATE_ABORTED.equals(rplan.getLifecycleState()))
				if(rplan.getException()!=null)
				{
					exceptionOccurred(rplan.getException());
				}
				else
				{
					// Automatically set goal result if goal has @GoalResult
					if(result!=null)
					{
						rplan.setResult(result);
						if(rplan.getReason() instanceof RServiceCall)
						{
							RServiceCall sc = (RServiceCall)rplan.getReason();
							InvocationInfo ii = sc.getInvocationInfo();
							ii.setResult(result);
						}
						else if(rplan.getReason() instanceof RGoal)
						{
							RGoal rgoal = (RGoal)rplan.getReason();
//							MGoal mgoal = (MGoal)rgoal.getModelElement();
							rgoal.setGoalResult(result, ia.getClassLoader(), null, rplan, null);
//							Object wa = mgoal.getPojoResultWriteAccess(ia.getClassLoader());
//							if(wa instanceof Field)
//							{
//								try
//								{
//									Field f = (Field)wa;
//									f.setAccessible(true);
//									f.set(rgoal.getPojoElement(), result);
//								}
//								catch(Exception e)
//								{
//									throw new RuntimeException(e);
//								}
//							}
//							else if(wa instanceof Method)
//							{
//								try
//								{
//									Method m = (Method)wa;
//									BDIAgentInterpreter	bai	= agent.getComponentFeature(IBDIAgentFeature.class);
//									Object[] params = bai.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(), rplan.getModelElement(), null, rplan, null);
//									m.invoke(rgoal.getPojoElement(), params);
//								}
//								catch(Exception e)
//								{
//									throw new RuntimeException(e);
//								}
//							}
						}
					}
					
					rplan.setFinishing();
					
					// Schedule passed/failed/aborted on separate component step, as it might be triggered inside other plan execution
					ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Object>()
					{
						@Override
						public IFuture<Object> execute(IInternalAccess ia)
						{
							return internalInvokePart(1);
						}
					}).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							rplan.setLifecycleState(RPlan.PlanLifecycleState.PASSED);
//							if(reason instanceof RProcessableElement)
//								((RProcessableElement)reason).planFinished(ia, rplan);
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							rplan.setException(exception);
							rplan.setLifecycleState(RPlan.PlanLifecycleState.FAILED);
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
				final int next = exception instanceof PlanAbortedException? 3: 2;
				
//				if(next==3)
//					System.out.println("exe of: "+rplan.getId()+", "+next);
				
//				System.out.println("setting ex on: "+rplan);
				rplan.setException(exception);
				
				assert getAgent().getComponentFeature(IExecutionFeature.class).isComponentThread();
				assert rplan.isFinishing() != (next==2): SUtil.getExceptionStacktrace(exception);	// either finishing (due to abort) or failed.
				if(next==2)
				{
					rplan.setFinishing();
				}
				
				// Schedule passed/failed/aborted on separate component step, as it might be triggered inside other plan execution
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Object>()
				{
					@Override
					public IFuture<Object> execute(IInternalAccess ia)
					{
						return internalInvokePart(next);
					}
				}).addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
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
	 *  Issue abortion of the plan body, if currently running.
	 */
	public void abort()
	{
//		System.out.println("body.abort "+rplan);
		if(partfuture!=null)
		{
			Future<Object>	fut	= partfuture;
			partfuture	= null;	// Needs to be set before to allow assert if null
			fut.setExceptionIfUndone(new PlanAbortedException());
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Object> internalInvokePart(int part)
	{
		assert partfuture==null;
		final Future<Object> ret = new Future<Object>();
		partfuture	= ret;
		
		try
		{
			assert RPlan.RPLANS.get()==null : RPlan.RPLANS.get()+", "+rplan;
			RPlan.RPLANS.set(rplan);
			rplan.setProcessingState(RPlan.PlanProcessingState.RUNNING);
			Object res = null;
			if(part==0) 
			{
//				System.out.println("body of: "+rplan);
				rplan.setLifecycleState(RPlan.PlanLifecycleState.BODY);
				res = invokeBody(guessParameters(getBodyParameterTypes()));
			}
			else if(part==1)
			{
//				System.out.println("passed of: "+rplan);
				rplan.setLifecycleState(RPlan.PlanLifecycleState.PASSING);
				res = invokePassed(guessParameters(getPassedParameterTypes()));
			}
			else if(part==2)
			{
//				System.out.println("failed of: "+rplan);
				rplan.setLifecycleState(RPlan.PlanLifecycleState.FAILING);
				res = invokeFailed(guessParameters(getFailedParameterTypes()));
			}
			else if(part==3)
			{
//				System.out.println("aborted of: "+rplan);
				rplan.setLifecycleState(RPlan.PlanLifecycleState.ABORTING);
				res = invokeAborted(guessParameters(getAbortedParameterTypes()));
			}
			
			if(res instanceof IFuture)
			{
				IFuture<Object> fut = (IFuture<Object>)res;
				if(!fut.isDone())
				{
					// When future is not done set state to (non-blocking) waiting.
					rplan.setProcessingState(PlanProcessingState.WAITING);
				}
				fut.addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						if(partfuture==ret)
						{
							partfuture	= null;
						}
						ret.setResultIfUndone(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(partfuture==ret)
						{
							partfuture	= null;
						}
						ret.setExceptionIfUndone(exception);
					}
				});
			}
			else
			{
				if(partfuture==ret)
				{
					partfuture	= null;
				}
				ret.setResultIfUndone(res);
			}
		}
		catch(PlanFailureException e)
		{
			if(partfuture==ret)
			{
				partfuture	= null;
			}
			ret.setExceptionIfUndone(e);
		}
		catch(BodyAborted ba)
		{
			assert ret.isDone() && ret.getException() instanceof PlanAbortedException;
		}
		catch(Throwable e)
		{
			if(partfuture==ret)
			{
				partfuture	= null;
			}
			
			if(e instanceof ThreadDeath)
			{
				// Thread death is used to exit user code -> ignore.
				ret.setResult(null);
			}
			else
			{
				ia.getLogger().warning("Plan '"+getBody()+"' threw exception: "+SUtil.getExceptionStacktrace(e));
				ret.setExceptionIfUndone(e instanceof Exception ? (Exception)e : new ErrorException((Error)e));
			}
		}
		finally
		{
			assert RPlan.RPLANS.get()==rplan : RPlan.RPLANS.get()+", "+rplan;
			RPlan.RPLANS.set(null);
		}
		
		return ret;
	}
	
	/**
	 *  Invoke the plan body.
	 */
	public abstract Object invokeBody(Object[] params) throws BodyAborted;

	/**
	 *  Invoke the plan passed method.
	 */
	public abstract Object invokePassed(Object[] params);

	/**
	 *  Invoke the plan failed method.
	 */
	public abstract Object invokeFailed(Object[] params);

	/**
	 *  Invoke the plan aborted method.
	 */
	public abstract Object invokeAborted(Object[] params);
	
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
		
		return BDIAgentFeature.getInjectionValues(ptypes, null, rplan.getModelElement(), null, rplan, null, ia);
	}

	/**
	 *  Get the rplan.
	 *  @return The rplan.
	 */
	public RPlan getRPlan()
	{
		return rplan;
	}

	/**
	 *  Get the agent.
	 *  @return The agent
	 */
	public IInternalAccess getAgent()
	{
		return ia;
	}
}
