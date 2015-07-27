package jadex.bdiv3.model;

import jadex.bdiv3x.BDIXModel;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
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
					goal.getRetryDelay(), goal.getRecurDelay(), goal.isOrSuccess(), goal.isUnique(), goal.getDeliberation(), null,
					goal.getServiceParameterMappings(), goal.getServiceResultMappings(), goal.getTriggerGoals()!=null ? new ArrayList<String>(goal.getTriggerGoals()) : null);
				
				// Copy parameters and convert events of dynamic parameters.
				if(goal.getParameters()!=null)
				{
					for(MParameter param: goal.getParameters())
					{
						MParameter param2 = copyParameter(bdimodel, cl, capaname, param);
						goal2.addParameter(param2);
					}
				}

						
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
					convertTrigger(bdimodel, capaname, plan.getTrigger(), true),
					convertTrigger(bdimodel, capaname, plan.getWaitqueue(), true),
					plan.getPriority());
				
				// Copy parameters and convert events of dynamic parameters.
				if(plan.getParameters()!=null)
				{
					for(MParameter param: plan.getParameters())
					{
						MParameter param2 = copyParameter(bdimodel, cl, capaname, param);
						plan2.addParameter(param2);
					}
				}
				
				bdimodel.getCapability().addPlan(plan2);
			}
			
			boolean	firstconf	= true;
			ConfigurationInfo[]	outerconfs	= bdimodel.getModelInfo().getConfigurations();
			for(ConfigurationInfo config: capa.getModelInfo().getConfigurations())
			{
				// Make sure at least one outer conf exists.
				if(outerconfs.length==0)
				{
					((ModelInfo)bdimodel.getModelInfo()).addConfiguration(new ConfigurationInfo(""));
					outerconfs	= bdimodel.getModelInfo().getConfigurations();
				}
				
				// Need to find, which outer configuration(s) use the inner (if any)
				for(ConfigurationInfo outer: outerconfs)
				{
					MConfiguration	outerbdi	= bdimodel.getCapability().getConfiguration(outer.getName());
					
					// Copy first inner configuration to all outer configurations that do not provide special mapping.
					boolean	copydef	= firstconf && (outerbdi==null || outerbdi.getInitialCapabilities()==null || !outerbdi.getInitialCapabilities().containsKey(capaname));
					
					// Copy all inner configurations to all outer configurations that do define this special mapping.
					boolean	copymap	= outerbdi!=null && outerbdi.getInitialCapabilities()!=null && outerbdi.getInitialCapabilities().containsKey(capaname) && outerbdi.getInitialCapabilities().get(capaname).equals(config.getName());
					
					if(copydef || copymap)
					{
						copyConfiguration(bdimodel, capaname, config, outer, capa.getCapability().getConfiguration(config.getName()), outerbdi);
					}
				}
				
				firstconf	= false;
			}
			
			// Todo: non-bdi elements from subcapabilities.
//			capa.getModelInfo().getFeatures()
//			capa.getModelInfo().getNFProperties()
//			capa.getModelInfo().getProperties()
//			capa.getModelInfo().getSubcomponentTypes()
		}
	}
	
	/**
	 *  Copy an inner configuration into an outer one.
	 */
	protected static void	copyConfiguration(IBDIModel bdimodel, String capaname, ConfigurationInfo cinner, ConfigurationInfo couter, MConfiguration inner, MConfiguration outer)
	{
		// Todo: non-bdi elements.
		
		if(inner!=null)
		{
			if(outer==null)
			{
				outer	= new MConfiguration(couter.getName());
				bdimodel.getCapability().addConfiguration(outer);
			}
			
			for(MConfigBeliefElement cbel: SUtil.safeList(inner.getInitialBeliefs()))
			{
				MConfigBeliefElement	cbel2	= copyConfigBelief(bdimodel, capaname, cbel, outer.getInitialBeliefs());
				if(cbel2!=null)
				{
					outer.addInitialBelief(cbel2);
				}
			}
			for(MConfigBeliefElement cbel: SUtil.safeList(inner.getEndBeliefs()))
			{
				MConfigBeliefElement	cbel2	= copyConfigBelief(bdimodel, capaname, cbel, outer.getEndBeliefs());
				if(cbel2!=null)
				{
					outer.addEndBelief(cbel2);
				}
			}
			
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialEvents()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getInitialEvents());
				if(cpel2!=null)
				{
					outer.addInitialEvent(cpel2);
				}
			}
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialGoals()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getInitialGoals());
				if(cpel2!=null)
				{
					outer.addInitialGoal(cpel2);
				}
			}
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getInitialPlans()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getInitialPlans());
				if(cpel2!=null)
				{
					outer.addInitialPlan(cpel2);
				}
			}
			
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndEvents()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getEndEvents());
				if(cpel2!=null)
				{
					outer.addEndEvent(cpel2);
				}
			}
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndGoals()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getEndGoals());
				if(cpel2!=null)
				{
					outer.addEndGoal(cpel2);
				}
			}
			for(MConfigParameterElement cpel: SUtil.safeList(inner.getEndPlans()))
			{
				MConfigParameterElement	cpel2	= copyConfigParameterElement(bdimodel, capaname, cpel, outer.getEndPlans());
				if(cpel2!=null)
				{
					outer.addEndPlan(cpel2);
				}
			}
		}
	}

	/**
	 *  Copy a config belief element.
	 */
	protected static MConfigBeliefElement copyConfigBelief(IBDIModel bdimodel, String capaname, MConfigBeliefElement cbel, List<MConfigBeliefElement> test)
	{
		// Only copy belief if it does not exist already (outer overrides inner settings).
		MConfigBeliefElement	cbel2	= null;
		String	name	= capaname + MElement.CAPABILITY_SEPARATOR + cbel.getName();
		name	= bdimodel.getBeliefReferences().containsKey(name) ? bdimodel.getBeliefReferences().get(name) : name;
		boolean	found	= false;
		for(MConfigBeliefElement tbel: SUtil.safeList(test))
		{
			if(tbel.getName().equals(name))
			{
				found	= true;
				break;
			}
		}
		
		if(!found)
		{
			cbel2	= new MConfigBeliefElement();
			cbel2.setName(name);
			for(UnparsedExpression fact: SUtil.safeList(cbel.getFacts()))
			{
				String	fname	= capaname + MElement.CAPABILITY_SEPARATOR + (fact.getName()!=null ? fact.getName() : "");
				UnparsedExpression	fact2	= new UnparsedExpression(fname, (String)null, fact.getValue(), fact.getLanguage());
				fact2.setParsedExp(fact.getParsed());	// Use parsed expression from inner scope (with correct imports).
				fact2.setClazz(fact.getClazz());
				cbel2.addFact(fact2);
			}
		}
		return cbel2;
	}
	
	/**
	 *  Copy a config parameter element.
	 */
	protected static MConfigParameterElement copyConfigParameterElement(IBDIModel bdimodel, String capaname, MConfigParameterElement cpel, List<MConfigParameterElement> test)
	{
		// Only copy element if it does not exist already (outer overrides inner settings).
		MConfigParameterElement	cpel2	= null;
		String	name	= capaname + MElement.CAPABILITY_SEPARATOR + cpel.getName();
		// todo: parameter element references
//		name	= bdimodel.getBeliefReferences().containsKey(name) ? bdimodel.getBeliefReferences().get(name) : name;
		boolean	found	= false;
		for(MConfigParameterElement tpel: SUtil.safeList(test))
		{
			if(tpel.getName().equals(name))
			{
				found	= true;
				break;
			}
		}

		if(!found)
		{
			cpel2	= new MConfigParameterElement();
			cpel2.setName(name);
			if(cpel.getParameters()!=null)
			{
				for(Entry<String, List<UnparsedExpression>> param: SUtil.safeSet(cpel.getParameters().entrySet()))
				{
					for(UnparsedExpression value: param.getValue())
					{
						UnparsedExpression	value2	= new UnparsedExpression(value.getName(), (String)null, value.getValue(), value.getLanguage());
						value2.setParsedExp(value.getParsed());	// Use parsed expression from inner scope (with correct imports).
						value2.setClazz(value.getClazz());
						cpel2.addParameter(value2);
						// Hack!!! change name after adding.
						value2.setName(capaname + MElement.CAPABILITY_SEPARATOR + (value.getName()!=null ? value.getName() : ""));
					}
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
		MParameter	param2	= param instanceof MPlanParameter ? new MPlanParameter() : new MParameter(param.getField());
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
		
		if(param instanceof MPlanParameter)
		{
			for(String mapping: SUtil.safeList(((MPlanParameter)param).getGoalMappings()))
			{
				((MPlanParameter)param2).addGoalMapping(capaname + MElement.CAPABILITY_SEPARATOR + mapping);
			}
			for(String mapping: SUtil.safeList(((MPlanParameter)param).getMessageEventMappings()))
			{
				((MPlanParameter)param2).addMessageEventMapping(capaname + MElement.CAPABILITY_SEPARATOR + mapping);
			}
			for(String mapping: SUtil.safeList(((MPlanParameter)param).getInternalEventMappings()))
			{
				((MPlanParameter)param2).addInternalEventMapping(capaname + MElement.CAPABILITY_SEPARATOR + mapping);
			}
		}
		
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
			// Triggers need to be converted in place, because they are post processed in pass 2!
			convertTrigger(bdimodel, null, plan.getTrigger(), false);
			convertTrigger(bdimodel, null, plan.getWaitqueue(), false);
			if(plan.getContextCondition()!=null)
			{
				plan.getContextCondition().setEvents(convertEventTypes(null, plan.getContextCondition().getEvents(), bdimodel));
			}
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
	 *  Convert a plan trigger or waitqueue and map the events.
	 *  Create a copy if desired.
	 */
	protected static MTrigger	convertTrigger(IBDIModel bdimodel, String capa, MTrigger trigger, boolean copy)
	{
		MTrigger trigger2	= null;
		if(trigger!=null)
		{
			trigger2	= copy ? new MTrigger() : trigger;
			if(trigger.getFactAddeds()!=null)
			{
				List<String>	events	= new ArrayList<String>();
				for(String event: trigger.getFactAddeds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					events.add(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
				trigger2.setFactAddeds(events);
			}
			if(trigger.getFactChangeds()!=null)
			{
				List<String>	events	= new ArrayList<String>();
				for(String event: trigger.getFactChangeds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					events.add(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
				trigger2.setFactChangeds(events);
			}
			if(trigger.getFactRemoveds()!=null)
			{
				List<String>	events	= new ArrayList<String>();
				for(String event: trigger.getFactRemoveds())
				{
					String	mapped	= capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+event : event;
					events.add(bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped);
				}
				trigger2.setFactRemoveds(events);
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
				int	exchange	= (types[0].startsWith("value") || types[0].startsWith("parameter")) ? types.length-2 : types.length-1;
				String	mapped = capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+types[exchange] : types[exchange];
				types[exchange]	= bdimodel.getBeliefReferences().containsKey(mapped) ? bdimodel.getBeliefReferences().get(mapped) : mapped;					
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
