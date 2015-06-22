package jadex.bdiv3.model;

import jadex.bdiv3x.BDIXModel;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.rules.eca.EventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
			String name = entry.getKey();
			IBDIModel capa = entry.getValue();
			
			capa.getModelInfo().getConfigurations();	// todo!!!

			for(ProvidedServiceInfo	psi: capa.getModelInfo().getProvidedServices())
			{
				ProvidedServiceInfo	psi2	= new ProvidedServiceInfo(name+MElement.CAPABILITY_SEPARATOR+psi.getName(), psi.getType(), psi.getImplementation(), psi.getScope(), psi.getPublish(), psi.getProperties());
				((ModelInfo)bdimodel.getModelInfo()).addProvidedService(psi2);
			}
			for(RequiredServiceInfo	rsi: capa.getModelInfo().getRequiredServices())
			{
				RequiredServiceInfo	rsi2	= new RequiredServiceInfo(name+MElement.CAPABILITY_SEPARATOR+rsi.getName(), rsi.getType(), rsi.isMultiple(), rsi.getMultiplexType(), rsi.getDefaultBinding(), rsi.getNFRProperties());
				((ModelInfo)bdimodel.getModelInfo()).addRequiredService(rsi2);
			}
			
			for(MBelief bel: capa.getCapability().getBeliefs())
			{
				Set<String> events = convertEvents(name, bel.getBeliefEvents(), bdimodel);
				
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
				bel2.setName(name+MElement.CAPABILITY_SEPARATOR+bel.getName());
				bel2.setDefaultFact(bel.getDefaultFact());
				bel2.setDefaultFacts(bel.getDefaultFacts());
				bel2.setDescription(bel.getDescription());
				bel2.setEvaluationMode(bel.getEvaluationMode());
				bel2.setMulti(bel.isMulti(cl));
				bel2.setClazz(bel.getClazz()!=null ? new ClassInfo(bel.getClazz().getType(cl)) : null);
				bdimodel.getCapability().addBelief(bel2);
			}
			
			if(bdimodel instanceof BDIModel)
			{
				for(String target: capa.getBeliefMappings().keySet())
				{
					((BDIModel)bdimodel).addBeliefMapping(name+MElement.CAPABILITY_SEPARATOR+target, name+MElement.CAPABILITY_SEPARATOR+capa.getBeliefMappings().get(target));
				}
			}
			else
			{
				for(String target: capa.getBeliefMappings().keySet())
				{
					((BDIXModel)bdimodel).addBeliefMapping(name+MElement.CAPABILITY_SEPARATOR+target, name+MElement.CAPABILITY_SEPARATOR+capa.getBeliefMappings().get(target));
				}
			}
			
			for(MGoal goal: capa.getCapability().getGoals())
			{
				MGoal goal2	= new MGoal(name+MElement.CAPABILITY_SEPARATOR+goal.getName(), goal.getTarget(),
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
							MCondition ccond = new MCondition(cond.getName(), convertEventTypes(name, cond.getEvents(), bdimodel));
							ccond.setConstructorTarget(cond.getConstructorTarget());
							ccond.setMethodTarget(cond.getMethodTarget());
							ccond.setExpression(cond.getExpression());
							ccond.setDescription(cond.getDescription());
							goal2.addCondition(type, ccond);
						}
					}
				}

				bdimodel.getCapability().addGoal(goal2);
			}
			
			for(MPlan plan : capa.getCapability().getPlans())
			{
				MPlan plan2	= new MPlan(name+MElement.CAPABILITY_SEPARATOR+plan.getName(), plan.getBody(),
					copyTrigger(bdimodel, name, plan.getTrigger()), copyTrigger(bdimodel, name, plan.getWaitqueue()),
					plan.getPriority());
				bdimodel.getCapability().addPlan(plan2);
			}
		}
	}

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
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+event;
					trigger2.addFactAdded(bdimodel.getBeliefMappings().containsKey(mapped) ? bdimodel.getBeliefMappings().get(mapped) : mapped);
				}
			}
			if(trigger.getFactChangeds()!=null)
			{
				for(String event: trigger.getFactChangeds())
				{
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+event;
					trigger2.addFactChangeds(bdimodel.getBeliefMappings().containsKey(mapped) ? bdimodel.getBeliefMappings().get(mapped) : mapped);
				}
			}
			if(trigger.getFactRemoveds()!=null)
			{
				for(String event: trigger.getFactRemoveds())
				{
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+event;
					trigger2.addFactRemoved(bdimodel.getBeliefMappings().containsKey(mapped) ? bdimodel.getBeliefMappings().get(mapped) : mapped);
				}
			}
			if(trigger.getGoals()!=null)
			{
				for(MGoal goal: trigger.getGoals())
				{
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+goal.getName();
					trigger2.addGoal(bdimodel.getCapability().getGoal(mapped));
				}
			}
			if(trigger.getGoalFinisheds()!=null)
			{
				for(MGoal goal: trigger.getGoalFinisheds())
				{
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+goal.getName();
					trigger2.addGoalFinished(bdimodel.getCapability().getGoal(mapped));
				}
			}
			if(trigger.getServices()!=null)
			{
				for(MServiceCall ser: trigger.getServices())
				{
					String	mapped	= capa+MElement.CAPABILITY_SEPARATOR+ser.getName();
					trigger2.addService(bdimodel.getCapability().getService(mapped));
				}
			}
		}
		
		return trigger2;
	}
	
	/**
	 * 
	 */
	protected static Set<String> convertEvents(String name, Set<String> evs, IBDIModel bdimodel)
	{
		Set<String>	ret;
		if(evs!=null)
		{
			ret	= new LinkedHashSet<String>();
			for(String event: evs)
			{
				String	mapped	= name+MElement.CAPABILITY_SEPARATOR+event;
				ret.add(bdimodel.getBeliefMappings().containsKey(mapped) ? bdimodel.getBeliefMappings().get(mapped) : mapped);
			}			
		}
		else
		{
			ret	= null;
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected static List<EventType> convertEventTypes(String name, Collection<EventType> evs, IBDIModel bdimodel)
	{
		List<EventType>	events	= new ArrayList<EventType>();
		for(EventType event: evs)
		{
			String[]	types	= event.getTypes().clone();
			String	mapped = name+MElement.CAPABILITY_SEPARATOR+types[types.length-1];
			types[types.length-1]	= bdimodel.getBeliefMappings().containsKey(mapped) ? bdimodel.getBeliefMappings().get(mapped) : mapped;
			events.add(new EventType(types));
		}
		return events;
	}
}
