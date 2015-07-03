package jadex.bdiv3.model;

import jadex.bdiv3x.BDIXModel;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;
import jadex.rules.eca.EventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *  Helper methods for pojo BDI and BDI V3X models.
 */
public class SBDIModel
{
	/**
	 *  Add elements from sub capabilities into model.
	 *  @param bdimodel	The model.
	 *  @param capas	The sub capabilities.
	 */
	public static void mergeSubcapabilities(IBDIModel bdimodel, Map<String, IBDIModel> capas, ClassLoader cl)
	{
		// Add elements from capabilities.
		for(Map.Entry<String, IBDIModel> entry: capas.entrySet())
		{
			String capaname = entry.getKey();
			IBDIModel capa = entry.getValue();
			
			capa.getModelInfo().getConfigurations();	// todo!!!

			for(ProvidedServiceInfo	psi: capa.getModelInfo().getProvidedServices())
			{
				ProvidedServiceInfo	psi2	= new ProvidedServiceInfo(capaname+MElement.CAPABILITY_SEPARATOR+psi.getName(), psi.getType(), psi.getImplementation(), psi.getScope(), psi.getPublish(), psi.getProperties());
				((ModelInfo)bdimodel.getModelInfo()).addProvidedService(psi2);
			}
			for(RequiredServiceInfo	rsi: capa.getModelInfo().getRequiredServices())
			{
				RequiredServiceInfo	rsi2	= new RequiredServiceInfo(capaname+MElement.CAPABILITY_SEPARATOR+rsi.getName(), rsi.getType(), rsi.isMultiple(), rsi.getMultiplexType(), rsi.getDefaultBinding(), rsi.getNFRProperties());
				((ModelInfo)bdimodel.getModelInfo()).addRequiredService(rsi2);
			}
			
			for(MBelief bel: capa.getCapability().getBeliefs())
			{
				String	belname	= capaname+MElement.CAPABILITY_SEPARATOR+bel.getName();
				
				// Mapped (abstract) belief.
				if(bdimodel.getBeliefReferences().containsKey(belname))
				{
					// ignore (only use concrete element from outer model).
					// Todo: merge settings? update rate etc.
				}
				
				// Copy concrete belief.
				else
				{
					Set<String> events = convertEvents(capaname, bel.getBeliefEvents(), bdimodel);
					
					MBelief	bel2;
					if(bel.getGetter()==null)
					{
						bel2 = new MBelief(bel.getField(), bel.getImplClassName(), bel.isDynamic(), bel.getUpdaterate(), events, bel.getRawEvents()!=null? new HashSet<EventType>(bel.getRawEvents()): null);
					}
					else
					{
						bel2 = new MBelief(bel.getGetter(), bel.getImplClassName(), bel.isDynamic(), bel.getUpdaterate(), events, bel.getRawEvents()!=null? new HashSet<EventType>(bel.getRawEvents()): null);
						bel2.setSetter(bel.getSetter());
					}
					bel2.setName(belname);
					bel2.setDefaultFact(bel.getDefaultFact());
					bel2.setDefaultFacts(bel.getDefaultFacts());
					bel2.setDescription(bel.getDescription());
					bel2.setEvaluationMode(bel.getEvaluationMode());
					bel2.setMulti(bel.isMulti(cl));
					bel2.setClazz(bel.getClazz()!=null ? new ClassInfo(bel.getClazz().getType(cl)) : null);
					bdimodel.getCapability().addBelief(bel2);
				}
			}
			
			if(bdimodel instanceof BDIModel)
			{
				for(String reference: capa.getBeliefReferences().keySet())
				{
					String	concrete	= capaname+MElement.CAPABILITY_SEPARATOR+capa.getBeliefReferences().get(reference);
					// Resolve transitive reference.
					if(bdimodel.getBeliefReferences().containsKey(concrete))
					{
						concrete	= bdimodel.getBeliefReferences().get(concrete);
						assert !bdimodel.getBeliefReferences().containsKey(concrete);	// Should only be one level!
					}
					((BDIModel)bdimodel).addBeliefReference(capaname+MElement.CAPABILITY_SEPARATOR+reference, concrete);
				}
			}
			else // if(bdimodel instanceof BDIXModel)
			{
				for(String reference: capa.getBeliefReferences().keySet())
				{
					String	concrete	= capaname+MElement.CAPABILITY_SEPARATOR+capa.getBeliefReferences().get(reference);
					// Resolve transitive reference.
					if(bdimodel.getBeliefReferences().containsKey(concrete))
					{
						concrete	= bdimodel.getBeliefReferences().get(concrete);
						assert !bdimodel.getBeliefReferences().containsKey(concrete);	// Should only be one level!
					}
					((BDIXModel)bdimodel).addBeliefReference(capaname+MElement.CAPABILITY_SEPARATOR+reference, concrete);
				}
			}
			
			for(MGoal goal: capa.getCapability().getGoals())
			{
				MGoal goal2	= new MGoal(capaname+MElement.CAPABILITY_SEPARATOR+goal.getName(), goal.getTarget(),
					goal.isPostToAll(), goal.isRandomSelection(), goal.getExcludeMode(), goal.isRetry(), goal.isRecur(),
					goal.getRetryDelay(), goal.getRecurDelay(), goal.isOrSuccess(), goal.isUnique(), goal.getDeliberation(), goal.getParameters(),
					goal.getServiceParameterMappings(), goal.getServiceResultMappings(), goal.getTriggerGoals()!=null ? new ArrayList<String>(goal.getTriggerGoals()) : null); // clone params?
						
				// Convert goal condition events
				if(goal.getConditions()!=null)
				{
					for(String type: goal.getConditions().keySet())
					{
						List<MCondition> conds = goal.getConditions(type);
						for(MCondition cond: conds)
						{
							MCondition ccond = copyCondition(bdimodel, capaname, cond);
							goal2.addCondition(type, ccond);
						}
					}
				}

				bdimodel.getCapability().addGoal(goal2);
			}
			
			for(MMessageEvent event : capa.getCapability().getMessageEvents())
			{
				MMessageEvent event2	= new MMessageEvent();
				event2.setName(capaname+MElement.CAPABILITY_SEPARATOR+event.getName());
				event2.setDescription(event.getDescription());
				event2.setDirection(event.getDirection());
				event2.setExcludeMode(event.getExcludeMode());
				event2.setMatchExpression(event.getMatchExpression());
				event2.setPostToAll(event.isPostToAll());
				event2.setRandomSelection(event.isRandomSelection());
				event2.setRebuild(event.isRebuild());
				event2.setType(event.getType());
				if(event.getParameters()!=null)
				{
					for(MParameter param: event.getParameters())
					{
						MParameter param2 = copyParameter(bdimodel, cl, capaname, param);
						event2.addParameter(param2);
					}
				}
				bdimodel.getCapability().addMessageEvent(event2);
			}
			
			for(MInternalEvent event : capa.getCapability().getInternalEvents())
			{
				MInternalEvent event2	= new MInternalEvent();
				event2.setName(capaname+MElement.CAPABILITY_SEPARATOR+event.getName());
				event2.setDescription(event.getDescription());
				event2.setExcludeMode(event.getExcludeMode());
				event2.setPostToAll(event.isPostToAll());
				event2.setRandomSelection(event.isRandomSelection());
				event2.setRebuild(event.isRebuild());
				if(event.getParameters()!=null)
				{
					for(MParameter param: event.getParameters())
					{
						MParameter param2 = copyParameter(bdimodel, cl, capaname, param);
						event2.addParameter(param2);
					}
				}
				bdimodel.getCapability().addInternalEvent(event2);
			}
			
			for(MPlan plan : capa.getCapability().getPlans())
			{
				MPlan plan2	= new MPlan(capaname+MElement.CAPABILITY_SEPARATOR+plan.getName(), plan.getBody(),
					copyTrigger(bdimodel, capaname, plan.getTrigger()), copyTrigger(bdimodel, capaname, plan.getWaitqueue()),
					plan.getPriority());
				bdimodel.getCapability().addPlan(plan2);
			}
			
			boolean	firstconf	= true;
			List<MConfiguration>	outerconfs	= bdimodel.getCapability().getConfigurations();
			for(MConfiguration config: capa.getCapability().getConfigurations())
			{
				// Make sure at least one outer conf exists.
				if(outerconfs==null || outerconfs.isEmpty())
				{
					bdimodel.getCapability().addConfiguration(new MConfiguration(""));
					outerconfs	= bdimodel.getCapability().getConfigurations();
				}
				
				// Need to find, which outer configuration(s) use the inner (if any)
				for(MConfiguration outer: outerconfs)
				{
					// Copy first inner configuration to all outer configurations that do not provide special mapping.
					boolean	copydef	= firstconf && (outer.getInitialCapabilities()==null || !outer.getInitialCapabilities().containsKey(capaname));
					
					// Copy all inner configurations to all outer configurations that do define this special mapping.
					boolean	copymap	= outer.getInitialCapabilities()!=null && outer.getInitialCapabilities().containsKey(capaname) && outer.getInitialCapabilities().get(capaname).equals(config.getName());
					
					if(copydef || copymap)
					{
						copyConfiguration(bdimodel, capaname, config, outer);						
					}
				}
				
				firstconf	= false;
			}
			
			// Todo: non-bdi elements from subcapabilities.
//			capa.getModelInfo().getConfigurations()
//			capa.getModelInfo().getFeatures()
//			capa.getModelInfo().getNFProperties()
//			capa.getModelInfo().getProvidedServices()
//			capa.getModelInfo().getProperties()
//			capa.getModelInfo().getRequiredServices()
//			capa.getModelInfo().getSubcomponentTypes()
		}
	}
	
	/**
	 *  Copy an inner configuration into an outer one.
	 */
	protected static void	copyConfiguration(IBDIModel bdimodel, String capaname, MConfiguration inner, MConfiguration outer)
	{
		for(MConfigBeliefElement cbel: SUtil.safeList(inner.getInitialBeliefs()))
		{
			outer.addInitialBelief(copyConfigBelief(bdimodel, capaname, cbel));
		}
		for(MConfigBeliefElement cbel: SUtil.safeList(inner.getEndBeliefs()))
		{
			outer.addEndBelief(copyConfigBelief(bdimodel, capaname, cbel));
		}
		
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialEvents()))
		{
			outer.addInitialEvent(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialGoals()))
		{
			outer.addInitialGoal(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialPlans()))
		{
			outer.addInitialPlan(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
		
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndEvents()))
		{
			outer.addEndEvent(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndGoals()))
		{
			outer.addEndGoal(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
		for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndPlans()))
		{
			outer.addEndPlan(copyConfigParameterElement(bdimodel, capaname, cpel));
		}
	}

	/**
	 *  Copy a config belief element.
	 */
	protected static MConfigBeliefElement copyConfigBelief(IBDIModel bdimodel, String capaname, MConfigBeliefElement cbel)
	{
		MConfigBeliefElement	cbel2	= new MConfigBeliefElement();
		String	name	= capaname + MElement.CAPABILITY_SEPARATOR + cbel.getName();
		cbel2.setName(bdimodel.getBeliefReferences().containsKey(name) ? bdimodel.getBeliefReferences().get(name) : name);
		for(UnparsedExpression fact: SUtil.safeList(cbel.getFacts()))
		{
			String	fname	= capaname + MElement.CAPABILITY_SEPARATOR + (fact.getName()!=null ? fact.getName() : "");
			UnparsedExpression	fact2	= new UnparsedExpression(fname, (String)null, fact.getValue(), fact.getLanguage());
			fact2.setClazz(fact.getClazz());
			cbel2.addFact(fact2);
		}
		return cbel2;
	}
	
	/**
	 *  Copy a config parameter element.
	 */
	protected static MConfigParameterElement copyConfigParameterElement(IBDIModel bdimodel, String capaname, MConfigParameterElement cpel)
	{
		MConfigParameterElement	cpel2	= new MConfigParameterElement();
		// todo: parameter element references
		String	name	= capaname + MElement.CAPABILITY_SEPARATOR + cpel.getName();
		cpel2.setName(name);
//		cpel2.setName(bdimodel.getBeliefReferences().containsKey(name) ? bdimodel.getBeliefReferences().get(name) : name);
		if(cpel.getParameters()!=null)
		{
			for(Entry<String, List<UnparsedExpression>> param: SUtil.safeSet(cpel.getParameters().entrySet()))
			{
				for(UnparsedExpression value: param.getValue())
				{
					UnparsedExpression	value2	= new UnparsedExpression(value.getName(), (String)null, value.getValue(), value.getLanguage());
					value2.setClazz(value.getClazz());
					cpel2.addParameter(value2);
					// Hack!!! change name after adding.
					value2.setName(capaname + MElement.CAPABILITY_SEPARATOR + (value.getName()!=null ? value.getName() : ""));
				}
			}			
		}
		return cpel2;
	}
	
	/**
	 *  Copy a parameter and adapt events.
	 */
	protected static MParameter copyParameter(IBDIModel bdimodel, ClassLoader cl, String capaname, MParameter param)
	{
		MParameter	param2	= new MParameter(param.getField());
		param2.setBeliefEvents(convertEvents(capaname, param.getBeliefEvents(), bdimodel));
		param2.setBindingOptions(param.getBindingOptions());
		param2.setClazz(param.getClazz());
		param2.setDefaultValue(param.getDefaultValue());
		param2.setDescription(param.getDescription());
		param2.setDirection(param.getDirection());
		param2.setEvaluationMode(param.getEvaluationMode());
		param2.setGetter(param.getGetter());
		param2.setMulti(param.isMulti(cl));
		param2.setName(param.getName());
		param2.setOptional(param.isOptional());
		param2.setRawEvents(param.getRawEvents());
		param2.setServiceMappings(param.getServiceMappings());
		param2.setSetter(param.getSetter());
		param2.setUpdateRate(param.getUpdateRate());
		return param2;
	}

	/**
	 *  Add elements from sub capabilities into model.
	 *  @param bdimodel	The model.
	 *  @param capas	The sub capabilities.
	 */
	public static void replaceReferences(IBDIModel bdimodel)
	{
		// Add references to known mappings.
		for(MBelief mbel: bdimodel.getCapability().getBeliefs())
		{
			if(mbel.getRef()!=null)
			{
				if(bdimodel instanceof BDIModel)
				{
					((BDIModel)bdimodel).addBeliefReference(mbel.getName(), mbel.getRef());
				}
				else
				{
					((BDIXModel)bdimodel).addBeliefReference(mbel.getName(), mbel.getRef());
				}
			}
		}
		
		
//		bdimodel.getModelInfo().getConfigurations();	// todo!!!

		for(MBelief bel: bdimodel.getCapability().getBeliefs())
		{
			Set<String> events = convertEvents(null, bel.getBeliefEvents(), bdimodel);
			bel.setBeliefEvents(events);
		}
					
		for(MGoal goal: bdimodel.getCapability().getGoals())
		{
			// todo: goal parameters?
			
			// Convert goal condition events
			if(goal.getConditions()!=null)
			{
				for(String type: goal.getConditions().keySet())
				{
					List<MCondition> conds = goal.getConditions(type);
					for(MCondition cond: conds)
					{
						cond.setEvents(convertEventTypes(null, cond.getEvents(), bdimodel));
					}
				}
			}
		}
			
		for(MPlan plan: bdimodel.getCapability().getPlans())
		{
			plan.setTrigger(copyTrigger(bdimodel, null, plan.getTrigger()));
			plan.setWaitqueue(copyTrigger(bdimodel, null, plan.getWaitqueue()));
		}
	}


	/**
	 *  Find the belief/ref value.
	 *  Returns the expression of the default value.
	 */
	public static UnparsedExpression	findBeliefDefaultValue(BDIXModel model, MBelief mbel, String configname)
	{
		UnparsedExpression	ret	= null;
		
		if(mbel.isMulti(null))
		{
			throw new RuntimeException("Method only allowed for single beliefs: "+mbel);
		}
		
		// Search initial value in configuration.
		MConfiguration	config	= configname!=null 
			? model.getCapability().getConfiguration(configname) : model.getConfigurations().length>0
			? model.getCapability().getConfiguration(model.getConfigurations()[0].getName()) : null;
		if(config!=null && config.getInitialBeliefs()!=null)
		{
			MConfigBeliefElement	inibel	= null;
			for(MConfigBeliefElement cbel: config.getInitialBeliefs())
			{
				if(cbel.getName().equals(mbel.getName()))
				{
					inibel	= cbel;
					break;
				}
			}
			
			if(inibel!=null && inibel.getFacts()!=null && !inibel.getFacts().isEmpty())
			{
				ret	= inibel.getFacts().get(0);
			}
		}
		
		return ret!=null ? ret : mbel.getDefaultFact();
	}
		
	/**
	 *  Find the beliefset/ref value.
	 *  Returns the expressions of the default values.
	 */
	public static List<UnparsedExpression>	findBeliefSetDefaultValues(BDIXModel model, MBelief mbel, String configname)
	{
		List<UnparsedExpression>	ret	= null;
		
		if(!mbel.isMulti(null))
		{
			throw new RuntimeException("Method only allowed for belief sets: "+mbel);
		}
		
		// Search initial value in configuration.
		MConfiguration	config	= configname!=null 
			? model.getCapability().getConfiguration(configname) : model.getConfigurations().length>0
			? model.getCapability().getConfiguration(model.getConfigurations()[0].getName()) : null;
		if(config!=null && config.getInitialBeliefs()!=null)
		{
			MConfigBeliefElement	inibel	= null;
			for(MConfigBeliefElement cbel: config.getInitialBeliefs())
			{
				if(cbel.getName().equals(mbel.getName()))
				{
					inibel	= cbel;
					break;
				}
			}
			
			if(inibel!=null)
			{
				ret	= inibel.getFacts();
			}
		}

		return ret!=null ? ret : mbel.getDefaultFacts();	// Todo: facts expression!?
	}

	//-------- helper methods --------

	/**
	 *  Coyp a plan trigger or waitqueue and map the events.
	 */
	protected static MTrigger	copyTrigger(IBDIModel bdimodel, String capa, MTrigger trigger)
	{
		MTrigger trigger2	= null;
		if(trigger!=null)
		{
			trigger2	= new MTrigger();
			if(trigger.getFactAddeds()!=null)
			{
				for(String event: trigger.getFactAddeds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					trigger2.addFactAdded(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
			}
			if(trigger.getFactChangeds()!=null)
			{
				for(String event: trigger.getFactChangeds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					trigger2.addFactChangeds(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
			}
			if(trigger.getFactRemoveds()!=null)
			{
				for(String event: trigger.getFactRemoveds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					trigger2.addFactRemoved(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
			}
			if(trigger.getGoals()!=null)
			{
				for(MGoal goal: trigger.getGoals())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+goal.getName() : goal.getName();
					trigger2.addGoal(bdimodel.getCapability().getGoal(mapped));
//					trigger.getGoalMatchExpression(mgoal)	// todo!
				}
			}
			if(trigger.getGoalFinisheds()!=null)
			{
				for(MGoal goal: trigger.getGoalFinisheds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+goal.getName() : goal.getName();
					trigger2.addGoalFinished(bdimodel.getCapability().getGoal(mapped));
				}
			}
			if(trigger.getServices()!=null)
			{
				for(MServiceCall ser: trigger.getServices())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+ser.getName() : ser.getName();
					trigger2.addService(bdimodel.getCapability().getService(mapped));
				}
			}
			if(trigger.getMessageEvents()!=null)
			{
				for(MMessageEvent event: trigger.getMessageEvents())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event.getName() : event.getName();
					trigger2.addMessageEvent(bdimodel.getCapability().getMessageEvent(mapped));
				}
			}
			if(trigger.getInternalEvents()!=null)
			{
				for(MInternalEvent event: trigger.getInternalEvents())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event.getName() : event.getName();
					trigger2.addInternalEvent(bdimodel.getCapability().getInternalEvent(mapped));
				}
			}
			if(trigger.getCondition()!=null)
			{
				trigger2.setCondition(copyCondition(bdimodel, null, trigger.getCondition()));
			}
		}
		
		return trigger2;
	}
	
	/**
	 * 
	 */
	protected static Set<String> convertEvents(String capa, Set<String> evs, IBDIModel bdimodel)
	{
		Set<String>	ret;
		if(evs!=null)
		{
			ret	= new LinkedHashSet<String>();
			for(String event: evs)
			{
				String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
				ret.add(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
			}			
		}
		else
		{
			ret	= null;
		}
		return ret==null || ret.isEmpty() ? null : ret;
	}
	
	/**
	 * 
	 */
	protected static List<EventType> convertEventTypes(String capa, Collection<EventType> evs, IBDIModel bdimodel)
	{
		List<EventType>	ret	= null;
		if(evs!=null)
		{
			ret	= new ArrayList<EventType>();
			for(EventType event: evs)
			{
				String[]	types	= event.getTypes().clone();
				String	mapped = capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+types[types.length-1] : types[types.length-1];
				types[types.length-1]	= bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped;
				ret.add(new EventType(types));
			}
		}
		return  ret==null || ret.isEmpty() ? null : ret;
	}
	
	/**
	 *  Copy a condition and adapt the events.
	 */
	protected static MCondition copyCondition(IBDIModel bdimodel, String capa, MCondition cond)
	{
		String	cname	= cond.getName()==null ? null : capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+cond.getName() : cond.getName();
		MCondition ccond = new MCondition(cname, convertEventTypes(capa, cond.getEvents(), bdimodel));
		ccond.setConstructorTarget(cond.getConstructorTarget());
		ccond.setMethodTarget(cond.getMethodTarget());
		ccond.setExpression(cond.getExpression());
		ccond.setDescription(cond.getDescription());
		return ccond;
	}
}
