package jadex.bdiv3.runtime.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.annotation.ExcludeMode;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameterElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MServiceCall;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RPlan.Waitqueue;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.ICandidateInfo;
import jadex.bdiv3x.runtime.IElement;
import jadex.bdiv3x.runtime.RInternalEvent;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

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
	protected List<ICandidateInfo> candidates;
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The mplan candidates. */
	protected List<ICandidateInfo> precandidates;
	
	/** The mgoal candidates (in case a goal triggers another goal). */
	protected List<ICandidateInfo> goalprecandidates;
	
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
		this(element, null);
	}
	
	/**
	 *  Create a new APL.
	 */
	public APL(RProcessableElement element, List<ICandidateInfo> candidates)
	{
		this.element = element;
		this.candidates = candidates;
	}
	
	//-------- methods --------
	
	/**
	 *  Build the apl.
	 */
	public IFuture<Void> build(IInternalAccess ia)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Reset candidates when rebuild
		if(((MProcessableElement)element.getModelElement()).isRebuild())
			candidates = null;
		
		if(candidates==null)
		{
			boolean	done	= false;

			Object pojo = element.getPojoElement();
			if(pojo!=null && element instanceof IGoal)
			{
				IGoal goal = (IGoal)element;
				MGoal mgoal = (MGoal)goal.getModelElement();
				MethodInfo mi = mgoal.getBuildAPLMethod(ia.getClassLoader());
				if(mi!=null)
				{
					Method m = mi.getMethod(ia.getClassLoader());
					try
					{
						m.setAccessible(true);
						List<Object> cands = (List<Object>)m.invoke(pojo, new Object[0]);
						candidates = new ArrayList<ICandidateInfo>();
						if(cands!=null)
						{
							for(Object cand: cands)
							{
								if(cand.getClass().isAnnotationPresent(Plan.class))
								{
									MCapability	mcapa = (MCapability)ia.getFeature(IInternalBDIAgentFeature.class).getCapability().getModelElement();
									MPlan mplan = mcapa.getPlan(cand.getClass().getName());
									CandidateInfoPojoPlan ci = new CandidateInfoPojoPlan(cand, element, ia);
									RPlan rplan = RPlan.createRPlan(mplan, ci, element, ia, null, null);
									ci.rplan = rplan;
									
									candidates.add(ci);
//									RPlan.executePlan(rplan, ia);
								}
								else if(cand instanceof ICandidateInfo)
								{
									candidates.add((ICandidateInfo)cand);
								}
								else
								{
									ret.setException(new RuntimeException("Candidates must be pojo plans or of type ICandidateInfo"));
									return ret;
								}
							}
						}
						done = true;
					}
					catch(Exception e)
					{
						throw SUtil.throwUnchecked(e);
					}
				}
			}
			
			if(!done)
			{
				// Handle waiting plans
				Collection<RPlan> rplans = ia.getFeature(IInternalBDIAgentFeature.class).getCapability().getPlans();
				if(rplans!=null)
				{
					for(RPlan rplan: rplans)
					{
						// check if plan is currently waiting for this proc elem
						if(rplan.isWaitingFor(element))
						{
							if(candidates==null)
								candidates = new ArrayList<ICandidateInfo>();
							candidates.add(new CandidateInfoRPlan(rplan, element));
						}
						// check if plan always waits for this proc elem
						else if(rplan.isWaitqueueWaitingFor(element))
						{
							if(candidates==null)
								candidates = new ArrayList<ICandidateInfo>();
							candidates.add(new CandidateInfoWaitqueue(rplan, element));
						}
					}
				}
				
				doBuild(ia).addResultListener(new ExceptionDelegationResultListener<List<ICandidateInfo>, Void>(ret)
				{
					public void customResultAvailable(List<ICandidateInfo> result)
					{
						if(candidates==null)
						{
							candidates = result;
						}
						else
						{
							candidates.addAll(result);
						}
						
						removeTriedCandidates();
						ret.setResult(null);
					}
				});
			}
			else
			{
				removeTriedCandidates();
				ret.setResult(null);
			}
		}
		else
		{
//			removeTriedCandidates();
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove tried candidates from the actual candidate collection.
	 */
	protected void removeTriedCandidates()
	{
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		ExcludeMode exclude = mpe.getExcludeMode();
		
		if(candidates!=null && candidates.size()>0 && !ExcludeMode.Never.equals(exclude) && element.getTriedPlans()!=null)
		{
			List<Object> cands = new ArrayList<Object>(candidates);
			for(Object candidate: cands)
			{
				for(IInternalPlan plan: element.getTriedPlans())
				{
					if(plan.getCandidate().equals(candidate))
					{
						if(isToExclude(plan, exclude))
						{
							candidates.remove(candidate);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Check if an rplan is to exclude wrt the exclude mode and plan result state.
	 *  @param rplan The tried plan.
	 *  @param exclude
	 *  @return True, if should be excluded.
	 */
	protected boolean isToExclude(IInternalPlan rplan, ExcludeMode exclude)
	{
		return exclude.equals(ExcludeMode.WhenTried)
			|| (rplan.isPassed() && exclude.equals(ExcludeMode.WhenSucceeded))
			|| (rplan.isFailed() && exclude.equals(ExcludeMode.WhenFailed))
			|| (rplan.isAborted() && rplan.getException()!=null && exclude.equals(ExcludeMode.WhenFailed));
	}
	
	//-------- helper methods --------

	/**
	 *  Test if APL has more candidates.
	 */
	public boolean isEmpty()
	{
		return candidates==null? true: candidates.isEmpty();
	}
	
	/**
	 *  Select candidates from the list of applicable plans.
	 */
	public List<ICandidateInfo> selectCandidates(MCapability mcapa, IInternalAccess ia)
	{
		List<ICandidateInfo> ret = new ArrayList<ICandidateInfo>();
		
//		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
//		// todo: include a number of retries...
//		int numcandidates = 1;
//		if(mpe.isPostToAll())
//		{
//			numcandidates = Integer.MAX_VALUE;
//		}
//		
//		// todo: test if this works with posttoall because getNextCandidate() does not remove a candidate?!
//		
//		for(int i=0; i<numcandidates && candidates!=null && candidates.size()>0; i++)
//		{
//			ret.add(getNextCandidate(mcapa, ia));
//		}
		
		if(candidates!=null && candidates.size()>0)
		{
			MProcessableElement mpe = (MProcessableElement)element.getModelElement();
			if(mpe.isPostToAll())
			{
				ret.addAll(candidates);
			}
			else
			{
				ret.add(getNextCandidate(mcapa, ia));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Do build the apl by adding possible candidate plans.
	 */
	protected IFuture<List<ICandidateInfo>>	doBuild(IInternalAccess ia)
	{
		final Future<List<ICandidateInfo>> ret = new Future<List<ICandidateInfo>>();
		
		IInternalBDIAgentFeature bdif = ia.getFeature(IInternalBDIAgentFeature.class);
		
//		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		
		// todo: generate binding candidates
		if(precandidates==null)
		{
			precandidates = new ArrayList<ICandidateInfo>();
			List<MPlan> mplans = ((MCapability)bdif.getCapability().getModelElement()).getPlans();
			
			if(mplans!=null)
			{
				for(MPlan mplan: mplans)
				{
					MTrigger mtrigger = mplan.getTrigger();
					
					if(element instanceof RGoal && mtrigger!=null)
					{
						List<MGoal> mgoals = mtrigger.getGoals();
						if(mgoals!=null && mgoals.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RServiceCall && mtrigger!=null)
					{
						List<MServiceCall> msers = mtrigger.getServices();
						if(msers!=null && msers.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RMessageEvent && mtrigger!=null)
					{
						List<MMessageEvent> msgs = mtrigger.getMessageEvents();
						if(msgs!=null && msgs.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RInternalEvent && mtrigger!=null)
					{
						List<MInternalEvent> ievs = mtrigger.getInternalEvents();
						if(ievs!=null && ievs.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
				}
			}
		}
		
		if(goalprecandidates==null)
		{
			goalprecandidates = new ArrayList<ICandidateInfo>();
			MCapability mcapa = (MCapability)bdif.getCapability().getModelElement();
			List<MGoal> mgoals = ((MCapability)bdif.getCapability().getModelElement()).getGoals();
			if(mgoals!=null)
			{
				for(int i=0; i<mgoals.size(); i++)
				{
					MGoal mgoal = mgoals.get(i);
//					List<MGoal> trgoals = mgoal.getTriggerMGoals(mcapa);
//					
//					if(element instanceof RGoal && trgoals!=null)
//					{
//						if(trgoals.contains(((RGoal)element).getModelElement()))
//						{
//							goalprecandidates.add(mgoal);
////						res.add(mplan);
//						}
//					}
					
					MTrigger mtrigger = mgoal.getTrigger();
					
					if(element instanceof RGoal && mtrigger!=null)
					{
						List<MGoal> mtrgoals = mtrigger.getGoals();
						if(mtrgoals!=null && mtrgoals.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RServiceCall && mtrigger!=null)
					{
						List<MServiceCall> msers = mtrigger.getServices();
						if(msers!=null && msers.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RMessageEvent && mtrigger!=null)
					{
						List<MMessageEvent> msgs = mtrigger.getMessageEvents();
						if(msgs!=null && msgs.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RInternalEvent && mtrigger!=null)
					{
						List<MInternalEvent> ievs = mtrigger.getInternalEvents();
						if(ievs!=null && ievs.contains(element.getModelElement()))
						{
							List<ICandidateInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
				}
			}
		}

//		final CollectionResultListener<MPlan> lis = new CollectionResultListener<MPlan>(precandidates.size(), true, new IResultListener<Collection<MPlan>>()
//		System.out.println("apl: "+(precandidates.size()+goalprecandidates.size()));
		final CollectionResultListener<ICandidateInfo> lis = new CollectionResultListener<ICandidateInfo>(precandidates.size()+goalprecandidates.size(), true, new IResultListener<Collection<ICandidateInfo>>()
		{
			public void resultAvailable(Collection<ICandidateInfo> result) 
			{
				ret.setResult(new ArrayList<ICandidateInfo>(result));
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		// add all goal types as they do not have preconditions (until now)
		for(final ICandidateInfo mgoal: goalprecandidates)
		{
			lis.resultAvailable(mgoal);
		}
		
		for(final ICandidateInfo mplan: precandidates)
		{
			checkMPlan(ia, mplan, element).addResultListener(new IResultListener<Boolean>()
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
		
		return ret;
	}
	
	/**
	 *  Test precondition (and match expression) of a plan to decide
	 *  if it can be added to the candidates.
	 */
	public static IFuture<Boolean> checkMPlan(IInternalAccess ia, ICandidateInfo cand, RProcessableElement element)
	{
		MPlanInfo mplaninfo = (MPlanInfo)cand.getRawCandidate();
		
		Future<Boolean> ret = new Future<Boolean>();
		boolean	valid	= true;
		MPlan mplan = mplaninfo.getMPlan();
		
		Map<String, Object>	vals	= new LinkedHashMap<String, Object>();
		if(mplaninfo.getBinding()!=null)
		{
			vals.putAll(mplaninfo.getBinding());
		}
		if(element!=null)
		{
			vals.put(element.getFetcherName(), element);
		}
		
		// check match expression
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			UnparsedExpression uexp = mplan.getTrigger().getGoalMatchExpression((MGoal)rgoal.getModelElement());
			if(uexp!=null)
			{
				Object val = SJavaParser.parseExpression(uexp, ia.getModel().getAllImports(), ia.getClassLoader()).getValue(
					CapabilityWrapper.getFetcher(ia, uexp.getLanguage(), vals));
				if(val instanceof Boolean)
				{
					valid	= ((Boolean)val).booleanValue();
				}
				else
				{
					ia.getLogger().warning("Match expression of plan trigger "+mplan.getName()+" not boolean: "+val);
					valid	= false;						
				}
				
				if(!valid)
				{
					ret.setResult(Boolean.FALSE);
					return ret;
//					lis.exceptionOccurred(null);
				}
			}
		}
		
		// check xml precondition
		UnparsedExpression upex = mplan.getPrecondition();
		if(upex!=null)
		{
			try
			{
				Object	val	= SJavaParser.getParsedValue(upex, null, CapabilityWrapper.getFetcher(ia, upex.getLanguage(), vals), null);
				if(val instanceof Boolean)
				{
					valid	= ((Boolean)val).booleanValue();
				}
				else
				{
					ia.getLogger().warning("Precondition of plan "+mplan.getName()+" not boolean: "+val);
					valid	= false;						
				}
			}
			catch(Exception e)
			{
				ia.getLogger().warning("Precondition of plan "+mplan.getName()+" threw exception: "+e);
				valid	= false;
			}
			
			ret.setResult(valid? Boolean.TRUE: Boolean.FALSE);
		}
		else
		{
			// check pojo precondition
			MethodInfo mi = mplan.getBody().getPreconditionMethod(ia.getClassLoader());
			if(mi!=null)
			{
				Method m = mi.getMethod(ia.getClassLoader());
				Object pojo = null;
				if(!Modifier.isStatic(m.getModifiers()))
				{
					RPlan rp = RPlan.createRPlan(mplan, cand, element, ia, mplaninfo.getBinding(), null);
					pojo = rp.getBody().getBody();
				}
				
				try
				{
					m.setAccessible(true);
					
					Object[] params = BDIAgentFeature.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(), element.getModelElement(), null, null, element, ia);
					if(params==null)
						System.out.println("Invalid parameter assignment");
					Object app = m.invoke(pojo, params);
					if(app instanceof Boolean)
					{
						ret.setResult((Boolean)app);
					}
					else if(app instanceof IFuture)
					{
						((IFuture<Boolean>)app).addResultListener(new DelegationResultListener<Boolean>(ret));
					}
				}
				catch(Exception e)
				{
					ret.setResult(Boolean.FALSE);
				}
			}
			else
			{
				ret.setResult(Boolean.TRUE);
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
	protected ICandidateInfo getNextCandidate(MCapability mcapa, IInternalAccess ia)
	{
		ICandidateInfo cand = null;
		
		MElement melem = element.getModelElement();
		if(melem instanceof MGoal)
		{
			MGoal mgoal = (MGoal)melem;
			MethodInfo mi = mgoal.getSelectCandidateMethod(ia.getClassLoader());
			if(mi!=null)
			{
				Method m = mi.getMethod(ia.getClassLoader());
				try
				{
					m.setAccessible(true);
					Collection<Object> col = new ArrayList<Object>();
					col.add(getCandidates());
					cand = (ICandidateInfo)m.invoke(element.getPojoElement(), BDIAgentFeature.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(), melem, null, null, element, col, ia));
				}
				catch(Exception e)
				{
					ia.getLogger().warning("Error in select candidates method: "+e.getMessage());
				}
			}
		}
		
		if(cand==null)
		{
			// Use the plan priorities to sort the candidates.
			// If the priority is the same use the following rank order:
			// running plan - waitqueue of running plan - passive plan
	
			// first find the list of highest ranked candidates
			// then choose one or more of them
			
			List<ICandidateInfo> finals = new ArrayList<ICandidateInfo>();
			finals.add(candidates.get(0));
			int candprio = getPriority(finals.get(0), mcapa);
			for(int i=1; i<candidates.size(); i++)
			{
				ICandidateInfo tmp = candidates.get(i);
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
		}
		
		return cand;
	}
	
	/**
	 *  Get the candidates.
	 *  @return The candidates
	 */
	public List<ICandidateInfo> getCandidates()
	{
		return candidates==null? null: Collections.unmodifiableList(candidates);
	}

	/**
	 *  Get the priority of a candidate.
	 *  @return The priority of a candidate.
	 */
	protected static int getPriority(ICandidateInfo cand, MCapability mcapa)
	{
//		MPlan mplan = null;
//		if(cand instanceof RWaitqueuePlan)
//		{
//			Object	rplan	= state.getAttributeValue(cand, OAVBDIRuntimeModel.waitqueuecandidate_has_plan);
//			mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
//		}
		
		MElement mplan = cand.getModelElement();
		
//		if(cand instanceof RPlan)
//		{
//			mplan = (MPlan)((RPlan)cand).getModelElement();
//		}
//		else if(cand.getClass().isAnnotationPresent(Plan.class))
//		{
//			mplan = mcapa.getPlan(cand.getClass().getName());
//		}
//		else if(cand instanceof MPlan)
//		{
//			mplan = (MPlan)cand;
//		}
//		else if(cand instanceof MPlanInfo)
//		{
//			mplan = ((MPlanInfo)cand).getMPlan();
//		}
////		else if(cand instanceof MGoal)
////		{
////			mgoal = (MGoal)cand;
////		}
		
		return mplan instanceof MPlan? ((MPlan)mplan).getPriority(): 0;
	}

	/**
	 *  Get the rank of a candidate.
	 *  The order is as follows:
	 *  new plan from model/candidate (0/1) -> waitqueue (2/3) -> running plan instance (4/5).
	 *  @return The rank of a candidate.
	 */
	protected int getRank(Object cand)
	{
		int ret;
		String	capaname	= null;
		
		if(cand instanceof RPlan)
		{
			ret = 4;
			capaname	= ((RPlan)cand).getModelElement().getCapabilityName();
		}
		else if(cand instanceof Waitqueue)
		{
			ret = 2;
			capaname	= ((Waitqueue)cand).getPlan().getModelElement().getCapabilityName();
		}
		else
		{
			ret = 0;
		}
		
		if(SUtil.equals(element.getModelElement().getCapabilityName(), capaname))
		{
			ret++;
		}
		
		return ret;
	}
	
	/**
	 *  After plan has finished the candidate will be removed from the APL.
	 */
	public void planFinished(IInternalPlan rplan)
	{
		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		ExcludeMode exclude = mpe.getExcludeMode();
		
		// always delete the rplan because the candidate can be reused
		Object cand = rplan.getCandidate();
		if(cand instanceof CandidateInfoMPlan)
			((CandidateInfoMPlan)cand).removePlan();

		// Do nothing if APL exclude is never
		if(ExcludeMode.Never.equals(exclude))
			return;
		
		if(isToExclude(rplan, exclude))
			candidates.remove(rplan.getCandidate());
	}
	
	/** 
	 *  Create candidates for a matching mplan.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return List of plan info objects.
	 */
	public static List<ICandidateInfo> createMPlanCandidates(IInternalAccess agent, MPlan mplan, RProcessableElement element)
	{
		List<ICandidateInfo> ret = new ArrayList<ICandidateInfo>();
		
		List<Map<String, Object>> bindings = calculateBindingElements(agent, mplan, element);
		
		if(bindings!=null)
		{
			for(Map<String, Object> binding: bindings)
			{
				ret.add(new CandidateInfoMPlan(new MPlanInfo(mplan, binding), element, agent));
			}
		}
		// No binding: generate one candidate.
		else
		{
			ret.add(new CandidateInfoMPlan(new MPlanInfo(mplan, null), element, agent));
		}
		
		return ret;
	}
	
	/** 
	 *  Create candidates for a matching mgoal.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return List of goal info objects.
	 */
	public static List<ICandidateInfo> createMGoalCandidates(IInternalAccess agent, MGoal mgoal, RProcessableElement element)
	{
		List<ICandidateInfo> ret = new ArrayList<ICandidateInfo>();
		
		List<Map<String, Object>> bindings = calculateBindingElements(agent, mgoal, element);
		
		if(bindings!=null)
		{
			for(Map<String, Object> binding: bindings)
			{
				ret.add(new CandidateInfoMGoal(new MGoalInfo(mgoal, binding), element, agent));
			}
		}
		// No binding: generate one candidate.
		else
		{
			ret.add(new CandidateInfoMGoal(new MGoalInfo(mgoal, null), element, agent));
		}
		
		return ret;
	}
	
	/**
	 *  Calculate the possible binding value combinations.
	 *  @param agent The agent.
	 *  @param melem The parameter element.
	 *  @param element The element to process (if any).
	 *  @return The list of binding maps.
	 */
	public static List<Map<String, Object>> calculateBindingElements(IInternalAccess agent, MParameterElement melem, RProcessableElement element)
	{
		List<Map<String, Object>> ret = null;
		
		Map<String, Object> bindingparams	= null;
		List<MParameter> params	= melem.getParameters();
		if(params!=null && params.size()>0)
		{
			Set<String> initializedparams = new HashSet<String>();
			
			// todo: configs with elements that have parameters?
			
//			String confname = agent.getConfiguration();
//			if(confname!=null)
//			{
//				final IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class);
//				final IBDIModel bdimodel = bdif.getBDIModel();
//				MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
//				if(mconf!=null)
//				{
//					List<UnparsedExpression> iplans = mconf.getInitialPlans();
//				}
				
//				Collection cparams = state.getAttributeValues(cel, OAVBDIMetaModel.configparameterelement_has_parameters);
//				if(cparams!=null)
//				{
//					for(Iterator it=cparams.iterator(); it.hasNext(); )
//					{
//						Object cparam = it.next();
//						String pname = (String)state.getAttributeValue(cparam, OAVBDIMetaModel.configparameter_has_ref);
//						Object param = state.getAttributeValue(mel, OAVBDIMetaModel.parameterelement_has_parameters, pname);
//						initializedparams.add(param);
//					}
//				}
//			}
			
			for(MParameter param: params)
			{
				if(!initializedparams.contains(param))
				{
					UnparsedExpression bo = param.getBindingOptions();
					if(bo!=null)
					{
						if(bindingparams==null)
							bindingparams = new HashMap<String, Object>();
						IParsedExpression exp = SJavaParser.parseExpression(bo, agent.getModel().getAllImports(), agent.getClassLoader());
						Object val = exp.getValue(CapabilityWrapper.getFetcher(agent, bo.getLanguage(),
							element!=null ? Collections.singletonMap(element.getFetcherName(), (Object)element) : null));
						bindingparams.put(param.getName(), val);
					}
				}
			}
		}
		
		// Calculate bindings and generate candidates. 
		if(bindingparams!=null)
		{			
			String[] names = (String[])bindingparams.keySet().toArray(new String[bindingparams.keySet().size()]);
			Object[] values = new Object[names.length];
			for(int i=0; i<names.length; i++)
			{
				values[i]	= bindingparams.get(names[i]);
			}
			bindingparams	= null;
			ret = SUtil.calculateCartesianProduct(names, values);
		}
		return ret;
	}
	
	/**
	 *  Plan info that contains the mgoal and the parameter bindings.
	 */
	public static class MPlanInfo
	{
		/** The mplan. */
		protected MPlan mplan; 
	
		/** The bindings. */
		protected Map<String, Object> binding;

		/**
		 *  Create a new plan info.
		 */
		public MPlanInfo()
		{
		}
		
		/**
		 *  Create a new plan info.
		 *  @param mplan
		 *  @param binding
		 */
		public MPlanInfo(MPlan mplan, Map<String, Object> binding)
		{
			this.mplan = mplan;
			this.binding = binding;
		}

		/**
		 *  Get the mplan.
		 *  @return The mplan
		 */
		public MPlan getMPlan()
		{
			return mplan;
		}

		/**
		 *  The mplan to set.
		 *  @param mplan The mplan to set
		 */
		public void setMPlan(MPlan mplan)
		{
			this.mplan = mplan;
		}

		/**
		 *  Get the binding.
		 *  @return The binding
		 */
		public Map<String, Object> getBinding()
		{
			return binding;
		}

		/**
		 *  The binding to set.
		 *  @param binding The binding to set
		 */
		public void setBinding(Map<String, Object> binding)
		{
			this.binding = binding;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return "MPlanInfo(plan="+mplan+", binding="+binding+")";
		}
	}
	
	/**
	 *  Goal info that contains the mgoal and the parameter bindings.
	 */
	public static class MGoalInfo
	{
		/** The mgoal. */
		protected MGoal mgoal; 
	
		/** The bindings. */
		protected Map<String, Object> binding;

		/**
		 *  Create a new plan info.
		 */
		public MGoalInfo()
		{
		}
		
		/**
		 *  Create a new plan info.
		 *  @param mplan
		 *  @param binding
		 */
		public MGoalInfo(MGoal mgoal, Map<String, Object> binding)
		{
			this.mgoal = mgoal;
			this.binding = binding;
		}

		/**
		 *  Get the mgoal. 
		 *  @return The mgoal
		 */
		public MGoal getMGoal()
		{
			return mgoal;
		}

		/**
		 *  Set the mgoal.
		 *  @param mgoal The mgoal to set
		 */
		public void setMGoal(MGoal mgoal)
		{
			this.mgoal = mgoal;
		}

		/**
		 *  Get the binding.
		 *  @return The binding
		 */
		public Map<String, Object> getBinding()
		{
			return binding;
		}

		/**
		 *  The binding to set.
		 *  @param binding The binding to set
		 */
		public void setBinding(Map<String, Object> binding)
		{
			this.binding = binding;
		}
		
	}
	
	/**
	 * Candidate info for mplan.
	 */
	public static class CandidateInfoMPlan implements ICandidateInfo
	{
		/** The mplan info. */
		protected MPlanInfo mplaninfo;
		
		/** The rplan. */
		protected RPlan rplan;
		
		/** The element. */
		protected RProcessableElement element;
		
		/** The agent. */
		protected IInternalAccess agent;

		/**
		 * 
		 * @param mplaninfo
		 * @param element
		 */
		public CandidateInfoMPlan(MPlanInfo mplaninfo, RProcessableElement element, IInternalAccess agent)
		{
			this.mplaninfo = mplaninfo;
			this.element = element;
			this.agent = agent;
		}

		/**
		 *  Get the plan instance.
		 *  @return	The plan instance.
		 */
		public IInternalPlan getPlan()
		{
//			if(rplan!=null)
//				System.out.println("access");
			
			if(rplan==null)
				rplan = RPlan.createRPlan((MPlan)getModelElement(), this, element, agent, mplaninfo.getBinding(), null);
			return rplan;
		}

		/**
		 *  Get the element this 
		 *  candidate was selected for.
		 *  @return	The processable element.
		 */
		public IElement getElement()
		{
			return element;
		}
		
		/**
		 *  Get the raw candidate.
		 *  @return The raw candiate.
		 */
		public Object getRawCandidate()
		{
			return mplaninfo;
		}
		
		/**
		 *  Get the plan model element.
		 *  @return The plan model element.
		 */
		public MElement getModelElement()
		{
			return mplaninfo.getMPlan();
		}
		
		/**
		 *  Remove the rplan.
		 */
		public void removePlan()
		{
			this.rplan = null;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+" "+mplaninfo.getMPlan();
		}
	}
	
	/**
	 * Candidate info for pojo plan.
	 */
	public static class CandidateInfoPojoPlan implements ICandidateInfo
	{
		/** The mplan info. */
		protected Object pojo;
		
		/** The rplan. */
		private RPlan rplan;
		
		/** The mplan. */
		protected MPlan mplan;
		
		/** The element. */
		protected RProcessableElement element;
		
		/** The agent. */
		protected IInternalAccess agent;

		/**
		 * @param mplaninfo
		 * @param element
		 */
		public CandidateInfoPojoPlan(Object pojo, RProcessableElement element, IInternalAccess agent)
		{
			this.pojo = pojo;
			this.element = element;
			this.agent = agent;
			
			MCapability	mcapa = (MCapability)agent.getFeature(IInternalBDIAgentFeature.class).getCapability().getModelElement();
			this.mplan = mcapa.getPlan(pojo.getClass().getName());
		}

		/**
		 *  Get the plan instance.
		 *  @return	The plan instance.
		 */
		public IInternalPlan getPlan()
		{
//			if(rplan!=null)
//				System.out.println("access: "+this);
			
			if(rplan==null)
				rplan = RPlan.createRPlan((MPlan)getModelElement(), this, element, agent, null, null);
			return rplan;
		}

		/**
		 *  Get the element this 
		 *  candidate was selected for.
		 *  @return	The processable element.
		 */
		public IElement getElement()
		{
			return element;
		}
		
		/**
		 *  Get the raw candidate.
		 *  @return The raw candiate.
		 */
		public Object getRawCandidate()
		{
			return pojo;
		}
		
		/**
		 *  Get the plan model element.
		 *  @return The plan model element.
		 */
		public MElement getModelElement()
		{
			return mplan;
		}
		
		/**
		 *  Remove the rplan.
		 */
		public void removePlan()
		{
			this.rplan = null;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+" "+mplan;
		}
	}
	
	/**
	 * Candidate info for mgoal.
	 */
	public static class CandidateInfoMGoal implements ICandidateInfo
	{
		/** The mplan info. */
		protected MGoalInfo mgoalinfo;
		
		/** The element. */
		protected RProcessableElement element;

		/** The agent. */
		protected IInternalAccess agent;
		
		/** The goal (treated as plan). */
		protected RGoal rgoal;
		
		/**
		 * 
		 * @param mplaninfo
		 * @param element
		 */
		public CandidateInfoMGoal(MGoalInfo mgoalinfo, RProcessableElement element, IInternalAccess agent)
		{
			this.mgoalinfo = mgoalinfo;
			this.element = element;
			this.agent = agent;
		}

		/**
		 *  Get the plan instance.
		 *  @return	The plan instance.
		 */
		public IInternalPlan getPlan()
		{
//			if(rgoal!=null)
//				System.out.println("access");
			
			RProcessableElement pae = (RProcessableElement)element;
			RGoal pagoal = pae instanceof RGoal? (RGoal)pae: null;
			Object pgoal = mgoalinfo.getMGoal().createPojoInstance(agent, pagoal);
			rgoal = new RGoal(agent, mgoalinfo.getMGoal(), pgoal, pagoal, mgoalinfo.getBinding(), null, this);
			return rgoal;
		}

		/**
		 *  Get the element this 
		 *  candidate was selected for.
		 *  @return	The processable element.
		 */
		public IElement getElement()
		{
			return element;
		}
		
		/**
		 *  Get the raw candidate.
		 *  @return The raw candiate.
		 */
		public Object getRawCandidate()
		{
			return mgoalinfo;
		}
		
		/**
		 *  Get the candidate model element.
		 *  @return The candidate model element.
		 */
		public MElement getModelElement()
		{
			return mgoalinfo.getMGoal();
		}
		
		/**
		 *  Remove the rplan.
		 */
		public void removePlan()
		{
			this.rgoal = null;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+" "+mgoalinfo.getMGoal();
		}
	}
	
	/**
	 * Candidate info for rplan.
	 */
	public static class CandidateInfoRPlan implements ICandidateInfo
	{
		/** The mplan info. */
		protected RPlan rplan;;
		
		/** The element. */
		protected RProcessableElement element;
		
		/**
		 * @param rplan
		 * @param element
		 */
		public CandidateInfoRPlan(RPlan rplan, RProcessableElement element)
		{
			this.rplan = rplan;
			this.element = element;
		}

		/**
		 *  Get the plan instance.
		 *  @return	The plan instance.
		 */
		public IInternalPlan getPlan()
		{
			return rplan;
		}

		/**
		 *  Get the element this 
		 *  candidate was selected for.
		 *  @return	The processable element.
		 */
		public IElement getElement()
		{
			return element;
		}
		
		/**
		 *  Get the raw candidate.
		 *  @return The raw candiate.
		 */
		public Object getRawCandidate()
		{
			return rplan;
		}
		
		/**
		 *  Get the plan model element.
		 *  @return The plan model element.
		 */
		public MElement getModelElement()
		{
			return rplan.getModelElement();
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+" "+rplan;
		}
	}
	
	/**
	 * Candidate info for waitqueue.
	 */
	public static class CandidateInfoWaitqueue implements ICandidateInfo
	{
		/** The mplan info. */
		protected RPlan rplan;
		
		/** The element. */
		protected RProcessableElement element;
		
		/**
		 * @param rplan
		 * @param element
		 */
		public CandidateInfoWaitqueue(RPlan rplan, RProcessableElement element)
		{
			this.rplan = rplan;
			this.element = element;
		}

		/**
		 *  Get the plan instance.
		 *  @return	The plan instance.
		 */
		public IInternalPlan getPlan()
		{
			return rplan;
		}

		/**
		 *  Get the element this 
		 *  candidate was selected for.
		 *  @return	The processable element.
		 */
		public IElement getElement()
		{
			return element;
		}
		
		/**
		 *  Get the raw candidate.
		 *  @return The raw candiate.
		 */
		public Object getRawCandidate()
		{
			return rplan.getWaitqueue();
		}
		
		/**
		 *  Get the plan model element.
		 *  @return The plan model element.
		 */
		public MElement getModelElement()
		{
			return rplan.getModelElement();
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+" "+rplan;
		}
	}
}

