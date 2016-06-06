package jadex.bdiv3.runtime.impl;

import java.lang.reflect.InvocationTargetException;
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

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameterElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.model.MServiceCall;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RPlan.Waitqueue;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.RInternalEvent;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
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
	protected List<Object> candidates;
	
	/** The metagoal. */
//	protected Object apl_has_metagoal;
	
	/** The mplan candidates. */
	protected List<MPlanInfo> precandidates;
	
	/** The mgoal candidates (in case a goal triggers another goal). */
	protected List<MGoalInfo> goalprecandidates;
	
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
	public APL(RProcessableElement element, List<Object> candidates)
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
						candidates = (List<Object>)m.invoke(pojo, new Object[0]);
						done = true;
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
			}
			
			if(!done)
			{
				// Handle waiting plans
				Collection<RPlan> rplans = ia.getComponentFeature(IInternalBDIAgentFeature.class).getCapability().getPlans();
				if(rplans!=null)
				{
					for(RPlan rplan: rplans)
					{
						// check if plan is currently waiting for this proc elem
						if(rplan.isWaitingFor(element))
						{
							if(candidates==null)
								candidates = new ArrayList<Object>();
							candidates.add((Object)rplan);
						}
						// check if plan always waits for this proc elem
						else if(rplan.isWaitqueueWaitingFor(element))
						{
							if(candidates==null)
								candidates = new ArrayList<Object>();
							candidates.add(rplan.getWaitqueue());
						}
					}
				}
				
				doBuild(ia).addResultListener(new ExceptionDelegationResultListener<List<Object>, Void>(ret)
				{
					public void customResultAvailable(List<Object> result)
					{
						if(candidates==null)
						{
							candidates = result;
						}
						else
						{
							candidates.addAll(result);
						}
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
		
		for(int i=0; i<numcandidates && candidates!=null && candidates.size()>0; i++)
		{
			ret.add(getNextCandidate(mcapa));
		}
		
		return ret;
	}
	
	/**
	 *  Do build the apl by adding possible candidate plans.
	 */
	protected IFuture<List<Object>>	doBuild(IInternalAccess ia)
	{
		final Future<List<Object>> ret = new Future<List<Object>>();
		
		IInternalBDIAgentFeature bdif = ia.getComponentFeature(IInternalBDIAgentFeature.class);
		
//		MProcessableElement mpe = (MProcessableElement)element.getModelElement();
		
		// todo: generate binding candidates
		if(precandidates==null)
		{
			precandidates = new ArrayList<MPlanInfo>();
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
							List<MPlanInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RServiceCall && mtrigger!=null)
					{
						List<MServiceCall> msers = mtrigger.getServices();
						if(msers!=null && msers.contains(element.getModelElement()))
						{
							List<MPlanInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RMessageEvent && mtrigger!=null)
					{
						List<MMessageEvent> msgs = mtrigger.getMessageEvents();
						if(msgs!=null && msgs.contains(element.getModelElement()))
						{
							List<MPlanInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
					else if(element instanceof RInternalEvent && mtrigger!=null)
					{
						List<MInternalEvent> ievs = mtrigger.getInternalEvents();
						if(ievs!=null && ievs.contains(element.getModelElement()))
						{
							List<MPlanInfo> cands = createMPlanCandidates(ia, mplan, element);
							precandidates.addAll(cands);
						}
					}
				}
			}
		}
		
		if(goalprecandidates==null)
		{
			goalprecandidates = new ArrayList<MGoalInfo>();
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
							List<MGoalInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RServiceCall && mtrigger!=null)
					{
						List<MServiceCall> msers = mtrigger.getServices();
						if(msers!=null && msers.contains(element.getModelElement()))
						{
							List<MGoalInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RMessageEvent && mtrigger!=null)
					{
						List<MMessageEvent> msgs = mtrigger.getMessageEvents();
						if(msgs!=null && msgs.contains(element.getModelElement()))
						{
							List<MGoalInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
					else if(element instanceof RInternalEvent && mtrigger!=null)
					{
						List<MInternalEvent> ievs = mtrigger.getInternalEvents();
						if(ievs!=null && ievs.contains(element.getModelElement()))
						{
							List<MGoalInfo> cands = createMGoalCandidates(ia, mgoal, element);
							goalprecandidates.addAll(cands);
						}
					}
				}
			}
		}

//		final CollectionResultListener<MPlan> lis = new CollectionResultListener<MPlan>(precandidates.size(), true, new IResultListener<Collection<MPlan>>()
//		System.out.println("apl: "+(precandidates.size()+goalprecandidates.size()));
		final CollectionResultListener<Object> lis = new CollectionResultListener<Object>(precandidates.size()+goalprecandidates.size(), true, new IResultListener<Collection<Object>>()
		{
			public void resultAvailable(Collection<Object> result) 
			{
				ret.setResult(new ArrayList<Object>(result));
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		// add all goal types as they do not have preconditions (until now)
		for(final MGoalInfo mgoal: goalprecandidates)
		{
			lis.resultAvailable(mgoal);
		}
		
		for(final MPlanInfo mplan: precandidates)
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
	public static IFuture<Boolean> checkMPlan(IInternalAccess ia, MPlanInfo mplaninfo, RProcessableElement element)
	{
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
					RPlan rp = RPlan.createRPlan(mplan, mplan, element, ia, mplaninfo.getBinding(), null);
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
	 *  Get the candidates.
	 *  @return The candidates
	 */
	public List<Object> getCandidates()
	{
		return candidates==null? null: Collections.unmodifiableList(candidates);
	}

	/**
	 *  Get the priority of a candidate.
	 *  @return The priority of a candidate.
	 */
	protected static int getPriority(Object cand, MCapability mcapa)
	{
		MPlan mplan = null;
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
		else if(cand instanceof MPlan)
		{
			mplan = (MPlan)cand;
		}
		else if(cand instanceof MPlanInfo)
		{
			mplan = ((MPlanInfo)cand).getMPlan();
		}
//		else if(cand instanceof MGoal)
//		{
//			mgoal = (MGoal)cand;
//		}
		
		return mplan!=null? mplan.getPriority(): 0;
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

		// Do nothing is APL is always rebuilt or exclude is never
		if(((MProcessableElement)element.getModelElement()).isRebuild()
			|| MProcessableElement.ExcludeMode.Never.equals(exclude))
//			|| (rplan.getModelElement() instanceof MGoal && ((MGoal)rplan.getModelElement()).isMetagoal()))
		{
			return;
		}

		if(exclude.equals(MProcessableElement.ExcludeMode.WhenTried))
		{
			candidates.remove(rplan.getCandidate());
		}
		else
		{
//			PlanLifecycleState state = rplan.getLifecycleState();
			if((rplan.isPassed() && exclude.equals(MProcessableElement.ExcludeMode.WhenSucceeded))
				|| (rplan.isFailed() && exclude.equals(MProcessableElement.ExcludeMode.WhenFailed))
				|| (rplan.isAborted() && rplan.getException()!=null && exclude.equals(MProcessableElement.ExcludeMode.WhenFailed)))
			{
//			if(state.equals(RPlan.PlanLifecycleState.PASSED)
//				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_SUCCEEDED)
//				|| (state.equals(RPlan.PlanLifecycleState.FAILED) 
//				&& exclude.equals(MProcessableElement.EXCLUDE_WHEN_FAILED)))
//			{
				candidates.remove(rplan.getCandidate());
			}
		}
	}
	
	/** 
	 *  Create candidates for a matching mplan.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return List of plan info objects.
	 */
	public static List<MPlanInfo> createMPlanCandidates(IInternalAccess agent, MPlan mplan, RProcessableElement element)
	{
		List<MPlanInfo> ret = new ArrayList<MPlanInfo>();
		
		List<Map<String, Object>> bindings = calculateBindingElements(agent, mplan, element);
		
		if(bindings!=null)
		{
			for(Map<String, Object> binding: bindings)
			{
				ret.add(new MPlanInfo(mplan, binding));
			}
		}
		// No binding: generate one candidate.
		else
		{
			ret.add(new MPlanInfo(mplan, null));
		}
		
		return ret;
	}
	
	/** 
	 *  Create candidates for a matching mgoal.
	 *  Checks precondition and evaluates bindings (if any).
	 *  @return List of goal info objects.
	 */
	public static List<MGoalInfo> createMGoalCandidates(IInternalAccess agent, MGoal mgoal, RProcessableElement element)
	{
		List<MGoalInfo> ret = new ArrayList<MGoalInfo>();
		
		List<Map<String, Object>> bindings = calculateBindingElements(agent, mgoal, element);
		
		if(bindings!=null)
		{
			for(Map<String, Object> binding: bindings)
			{
				ret.add(new MGoalInfo(mgoal, binding));
			}
		}
		// No binding: generate one candidate.
		else
		{
			ret.add(new MGoalInfo(mgoal, null));
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
	 * 
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
		
		@Override
		public String toString()
		{
			return "MPlanInfo(plan="+mplan+", binding="+binding+")";
		}
	}
	
	/**
	 * 
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
}

