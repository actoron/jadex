package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.GoalAPLBuild;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MServiceCall;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.MethodInfo;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  The APL is the applicable plan list. It stores the
 *  candidates that can be (and were) executed for a processable element.
 */
public class APL
{	
	//-------- attributes --------
	
	/** The processable element. */
	protected RProcessableElement element;
	
	/** The list of candidates. */
	protected List<?> candidates;
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The mplan candidates. */
	protected List<MPlan> precandidates;
	
//	/** The plan instance candidates. */
//	protected List<RPlan> planinstancecandidates;
	
//	/** The waitqueue candidates. */
//	protected List<RPlan> waitqueuecandidates;
	
	//-------- constructors --------

	/**
	 *  Create a new APL.
	 */
	public APL(RProcessableElement element)
	{
		this.element = element;
	}
	
	//-------- methods --------
	
//	/**
//	 *  Get the plancandidates.
//	 *  @return The plancandidates.
//	 */
//	public List<MPlan> getPlanCandidates()
//	{
//		return plancandidates;
//	}
//
//	/**
//	 *  Set the plancandidates.
//	 *  @param plancandidates The plancandidates to set.
//	 */
//	public void setPlanCandidates(List<MPlan> plancandidates)
//	{
//		this.plancandidates = plancandidates;
//	}
	
//	/**
//	 *  Get the next candidate.
//	 */
//	public Object getNextCandidate()
//	{
//		Object ret = null;
//		if(plancandidates!=null && plancandidates.size()>0)
//		{
//			// todo exclude modes
//			ret = plancandidates.remove(0);
//		}
//		return ret;
//	}
	
	/**
	 * 
	 */
	public IFuture<Void> build(IInternalAccess ia)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(candidates==null || ((MProcessableElement)element.getModelElement()).isRebuild())
		{
			boolean	done	= false;
			Object	pojo	= element.getPojoElement();
			if(pojo!=null)
			{
				Class<?>	clazz	= pojo.getClass();
				
				// todo: this causes poor performance -> should be moved to model level
				Method[]	ms	= SReflect.getAllMethods(clazz);
				for(int i=0; !done && i<ms.length; i++)
				{
					if(ms[i].isAnnotationPresent(GoalAPLBuild.class))
					{
						if((ms[i].getModifiers()&Modifier.PUBLIC)!=0)
						{
							done	= true;
							try
							{
								candidates	= (List<?>)ms[i].invoke(pojo, new Object[0]);
							}
							catch(InvocationTargetException e)
							{
								throw e.getTargetException() instanceof RuntimeException
									? (RuntimeException)e.getTargetException()
									: new RuntimeException(e.getTargetException());
							}
							catch(Exception e)
							{
								throw e instanceof RuntimeException
									? (RuntimeException)e
									: new RuntimeException(e);
							}
							
						}
						else
						{
							throw new RuntimeException("Method not public: "+ms[i]);
						}
					}
				}
			}
			
			if(!done)
			{
				doBuild(ia).addResultListener(new ExceptionDelegationResultListener<List<Object>, Void>(ret)
				{
					public void customResultAvailable(List<Object> result)
					{
						candidates = result;
						ret.setResult(null);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
		
			
			// both aspects are dealt with dispatchToAll() via rules
			// if we want to support disptach of goals, internal or message events to running plans
			// we need to reintrodcue this
			
			// todo: plan to running?
//			Collection<RPlan> rplans = capa.getPlans();
//			if(rplans!=null)
//			{
//				for(RPlan rplan: rplans)
//				{
//					if(rplan.isWaitingFor(element))
//					{
//						candidates.add(rplan);
//					}
//				}
//			}
			// todo waitqueue ?
//		}
//		else
//		{
//			// check rplans and waitqueues
//			// first remove all rplans that do not wait
//			for(Object cand: candidates)
//			{
//				if(cand instanceof RPlan && !((RPlan)cand).isWaitingFor(element))
//				{
//					candidates.remove(cand);
//				}
//			}
//			// add new rplans that are not contained already
//			Collection<RPlan> rplans = capa.getPlans();
//			if(rplans!=null)
//			{
//				for(RPlan rplan: rplans)
//				{
//					if(!candidates.contains(rplan) && rplan.isWaitingFor(element))
//					{
//						candidates.add(rplan);
//					}
//				}
//			}
//		}
	
	}
	
	//-------- helper methods --------

	/**
	 * 
	 */
	public boolean isEmpty()
	{
		return candidates==null? true: candidates.isEmpty();
	}
	
	/**
	 * 
	 */
	public List<Object> selectCandidates(MCapability mcapa)
	{
		List<Object> ret = new ArrayList<Object>();
		
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		// todo: include a number of retries...
		int numcandidates = 1;
		if(mpe.isPostToAll())
		{
			numcandidates = Integer.MAX_VALUE;
		}
		
		for(int i=0; i<numcandidates && candidates.size()>0; i++)
		{
			ret.add(getNextCandidate(mcapa));
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<List<Object>>	doBuild(IInternalAccess ia)
	{
		final Future<List<Object>> ret = new Future<List<Object>>();
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		
//		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		
		// todo: generate binding candidates
		if(precandidates==null)
		{
			precandidates = new ArrayList<MPlan>();
			List<MPlan> mplans = ((MCapability)ip.getCapability().getModelElement()).getPlans();
			if(mplans!=null)
			{
				for(int i=0; i<mplans.size(); i++)
				{
					MPlan mplan = mplans.get(i);
					MTrigger mtrigger = mplan.getTrigger();
					
					if(element instanceof RGoal && mtrigger!=null)
					{
						List<MGoal> mgoals = mtrigger.getGoals();
						if(mgoals!=null && mgoals.contains(element.getModelElement()))
						{
							precandidates.add(mplan);
//							res.add(mplan);
						}
					}
					else if(element instanceof RServiceCall && mtrigger!=null)
					{
						List<MServiceCall> msers = mtrigger.getServices();
						if(msers!=null && msers.contains(element.getModelElement()))
						{
							precandidates.add(mplan);
//							res.add(mplan);
						}
					}
				}
			}
		}

		final CollectionResultListener<MPlan> lis = new CollectionResultListener<MPlan>(precandidates.size(), true, new IResultListener<Collection<MPlan>>()
		{
			public void resultAvailable(Collection<MPlan> result) 
			{
				ret.setResult((List)result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		for(final MPlan mplan: precandidates)
		{
			// check precondition
			MethodInfo mi = mplan.getBody().getPreconditionMethod(ia.getClassLoader());
			if(mi!=null)
			{
				Method m = mi.getMethod(ia.getClassLoader());
				Object pojo = null;
				if(!Modifier.isStatic(m.getModifiers()))
				{
					RPlan rp = RPlan.createRPlan(mplan, mplan, element, ia);
					final Object agent = ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia;
					pojo = rp.getBody().getBody(agent);
				}
				try
				{
					m.setAccessible(true);
					
					Object app = m.invoke(pojo, ((BDIAgentInterpreter)((BDIAgent)ia).getInterpreter())
						.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(), element.getModelElement(), null, null, element));
					if(app instanceof Boolean)
					{
						if(((Boolean)app).booleanValue())
						{
							lis.resultAvailable(mplan);
						}
						else
						{
							lis.exceptionOccurred(null);
						}
					}
					else if(app instanceof IFuture)
					{
						((IFuture<Boolean>)app).addResultListener(new IResultListener<Boolean>()
						{
							public void resultAvailable(Boolean result)
							{
								if(result.booleanValue())
								{
									lis.resultAvailable(mplan);
								}
								else
								{
									lis.exceptionOccurred(null);
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								lis.exceptionOccurred(exception);
							}
						});
					}
				}
				catch(Exception e)
				{
					lis.exceptionOccurred(e);
				}
			}
			else
			{
				lis.resultAvailable(mplan);
			}
		}
		
		return ret;
	}
	
//	/**
//	 *  Method that tries to guess the parameters for the method call.
//	 */
//	public Object[] guessParameters(Class<?>[] ptypes)
//	{
//		if(ptypes==null)
//			return null;
//		// Guess parameters
////		Class<?>[] ptypes = body.getParameterTypes();
//		
//		Object pojope = element.getPojoElement();
//		
//		Object[] params = new Object[ptypes.length];
//		
//		for(int i=0; i<ptypes.length; i++)
//		{
//			if(SReflect.isSupertype(element.getClass(), ptypes[i]))
//			{
//				params[i] = element;
//			}
//			else if(pojope!=null && SReflect.isSupertype(pojope.getClass(), ptypes[i]))
//			{
//				params[i] = pojope;
//			}
//		}
//				
//		return params;
//	}
	
	/**
	 *  Get the next candidate with respect to the plan
	 *  priority and the rank of the candidate.
	 *  @return The next candidate.
	 */
	protected Object getNextCandidate(MCapability mcapa)
	{
		// Use the plan priorities to sort the candidates.
		// If the priority is the same use the following rank order:
		// running plan - waitqueue of running plan - passive plan

		// first find the list of highest ranked candidates
		// then choose one or more of them
		
		List<Object> finals = new ArrayList<Object>();
		finals.add(candidates.get(0));
		int candprio = getPriority(finals.get(0), mcapa);
		for(int i=1; i<candidates.size(); i++)
		{
			Object tmp = candidates.get(i);
			int tmpprio = getPriority(tmp, mcapa);
			if(tmpprio>candprio || (tmpprio == candprio && getRank(tmp)>getRank(finals.get(0))))
			{
				finals.clear();
				finals.add(tmp);
				candprio = tmpprio;
			}
			else if(tmpprio==candprio && getRank(tmp)==getRank(finals.get(0)))
			{
				finals.add(tmp);
			}
		}

		Object cand;
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		if(mpe.isRandomSelection())
		{
			int rand = (int)(Math.random()*finals.size());
			cand = finals.get(rand);
			//System.out.println("Random sel: "+finals.size()+" "+rand+" "+cand);
		}
		else
		{
			//System.out.println("First sel: "+finals.size()+" "+0);
			cand = finals.get(0);
		}

		return cand;
	}

	/**
	 *  Get the priority of a candidate.
	 *  @return The priority of a candidate.
	 */
	protected static int getPriority(Object cand, MCapability mcapa)
	{
		MPlan mplan;
//		if(cand instanceof RWaitqueuePlan)
//		{
//			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
//			mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
//		}
		if(cand instanceof RPlan)
		{
			mplan = (MPlan)((RPlan)cand).getModelElement();
		}
		else if(cand.getClass().isAnnotationPresent(Plan.class))
		{
			mplan = mcapa.getPlan(cand.getClass().getName());
		}
		else 
		{
			mplan = (MPlan)cand;
		}
			
		return mplan.getPriority();
	}

	/**
	 *  Get the rank of a candidate.
	 *  The order is as follows:
	 *  new plan from model/candidate (0) -> waitqueue (1) -> running plan instance (2).
	 *  @return The rank of a candidate.
	 */
	protected static int getRank(Object cand)
	{
		int ret;
		
		if(cand instanceof RPlan)
		{
			ret = 2;
		}
//		else if() // waitqueue
//		{
//		}
		else
		{
			ret = 0;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void planFinished(RPlan rplan)
	{
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		String exclude = mpe.getExcludeMode();

		// Do nothing is APL is always rebuilt or exclude is never
		if(((MProcessableElement)element.getModelElement()).isRebuild()
			|| MProcessableElement.EXCLUDE_NEVER.equals(exclude))
		{
			return;
		}

		if(exclude.equals(MProcessableElement.EXCLUDE_WHEN_TRIED))
		{
			candidates.remove(rplan.getCandidate());
		}
		else
		{
			PlanLifecycleState state = rplan.getLifecycleState();
			if(state.equals(RPlan.PlanLifecycleState.PASSED)
				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_SUCCEEDED)
				|| (state.equals(RPlan.PlanLifecycleState.FAILED) 
				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_FAILED)))
			{
				candidates.remove(rplan.getCandidate());
			}
		}
	}
}
