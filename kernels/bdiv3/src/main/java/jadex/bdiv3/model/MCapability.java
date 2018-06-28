package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

/**
 *  The capability model.
 */
public class MCapability extends MElement
{
	/** The beliefs. */
	protected List<MBelief> beliefs;

	/** The goals. */
	protected List<MGoal> goals;
	
	/** The plans. */
	protected List<MPlan> plans;

	/** The message events. */
	protected List<MMessageEvent> messages;
	
	/** The services. */
	protected List<MServiceCall> services;
	
	/** The expressions. */
	protected List<UnparsedExpression> expressions;
	
	/** The configurations. */
	protected List<MConfiguration> configurations;
	
	/** The belief mappings (abstract/reference name -> concrete belief name). */
	protected Map<String, String> beliefreferences;
	
	//-------- xml only --------
	
	/** The result mappings <belief->result>. */
	// Cached for speed.
	protected Map<String, String> resultmappings;
	
	/** The goal mappings (abstract/reference name -> concrete name). */
	protected Map<String, String> goalreferences;
	
	/** The event mappings (abstract/reference name -> concrete name). */
	protected Map<String, String> eventreferences;
	
	/** The expression mappings (abstract/reference name -> concrete name). */
	protected Map<String, String> expressionreferences;

	/** The subcapabilities. */
	protected List<MCapabilityReference> subcapabilities;
	
	/** The internal events. */
	protected List<MInternalEvent> ievents;
	
	/** The goal/service publications. */
	protected Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs = new HashMap<ClassInfo, List<Tuple2<MGoal, String>>>();
	
	/** The expressions. */
	protected List<MCondition> conditions;
	
	/**
	 *	Bean Constructor. 
	 */
	public MCapability()
	{
	}
	
	/**
	 *  Create a capability.
	 */
	public MCapability(String name)
	{
		super(name);
	}

	/**
	 *  Get the beliefs.
	 *  @return The beliefs.
	 */
	public List<MBelief> getBeliefs()
	{
		return beliefs==null? Collections.EMPTY_LIST: beliefs;
	}

	/**
	 *  Set the beliefs.
	 *  @param beliefs The beliefs to set.
	 */
	public void setBeliefs(List<MBelief> beliefs)
	{
		this.beliefs = beliefs;
	}

	/**
	 *  Add a belief.
	 */
	public void addBelief(MBelief belief)
	{
		if(beliefs==null)
			beliefs = new ArrayList<MBelief>();
		beliefs.add(belief);
	}
	
	/**
	 *  Remove a belief.
	 */
	public void removeBelief(MBelief belief)
	{
		if(beliefs!=null)
		{
			beliefs.remove(belief);
		}
	}
	
	/**
	 *  Test if a belief is contained.
	 */
	public boolean hasBelief(String name)
	{
		boolean ret = false;
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a belief is contained.
	 *  @param name The name.
	 *  @return The correctly spelled belief name or null.
	 */
	public String hasBeliefIgnoreCase(String name)
	{
		String ret = null;
		name = name.toLowerCase();
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				if(name.equals(bel.getName().toLowerCase()))
				{
					ret = bel.getName();
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a belief.
	 */
	public MBelief getBelief(String name)
	{
		MBelief ret = null;
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}
	
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List<MGoal> getGoals()
	{
		return goals==null? Collections.EMPTY_LIST: goals;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(List<MGoal> goals)
	{
		this.goals = goals;
	}
	
	/**
	 *  Add a goal.
	 */
	public void addGoal(MGoal goal)
	{
		if(goals==null)
			goals = new ArrayList<MGoal>();
		goals.add(goal);
	}
	
	/**
	 *  Get the goal for its name.
	 *  @return The goal.
	 */
	public MGoal getGoal(String name)
	{
		MGoal ret = null;
		if(goals!=null)
		{
			List<MGoal>	partials	= null;
			for(MGoal goal: goals)
			{
				if(goal.getName().equals(name))
				{
					ret = goal;
					break;
				}
				
				// Partial matches needed for pojo goals, as capability name is not known.
				// Todo: find exact capability name for non-static inner class goals.
				else if(goal.getName().endsWith(name))
				{
					if(partials==null)
					{
						partials	= new ArrayList<MGoal>();
					}
					partials.add(goal);
				}
			}
			
			if(ret==null && partials!=null && partials.size()==1)
			{
				ret	= partials.get(0);
			}
		}
		return ret;
	}

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List<MPlan> getPlans()
	{
		return plans==null? Collections.EMPTY_LIST: plans;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(List<MPlan> plans)
	{
		this.plans = plans;
	}
	
	/**
	 *  Add a plan.
	 */
	public void addPlan(MPlan plan)
	{
		if(plans==null)
			plans = new ArrayList<MPlan>();
		plans.add(plan);
	}
	
	/**
	 *  Get the plan for its name.
	 *  @return The plan.
	 */
	public MPlan getPlan(String name)
	{
		MPlan ret = null;
		if(plans!=null)
		{
			for(MPlan plan: plans)
			{
				if(plan.getName().equals(name))
				{
					ret = plan;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Sorts plans according to their line numbers in the source
	 *  to guarantee their natural declaration order.
	 */
	public void sortPlans(final ClassLoader cl)
	{
		if(plans!=null)
		{
			Collections.sort(plans, new Comparator<MPlan>()
			{
				public int compare(MPlan p1, MPlan p2)
				{
					int ln1 = p1.getBody().getLineNumber(cl);
					int ln2 = p2.getBody().getLineNumber(cl);
					return ln1-ln2;
				}
			});
		}
	}
	
	/**
	 *  Get the Internals.
	 *  @return The Internals.
	 */
	public List<MInternalEvent> getInternalEvents()
	{
		return ievents==null? Collections.EMPTY_LIST: ievents;
	}

	/**
	 *  Set the internal events.
	 *  @param ievents The internal events to set.
	 */
	public void setInternalEvents(List<MInternalEvent> ievents)
	{
		this.ievents = ievents;
	}

	/**
	 *  Add an internal event.
	 */
	public void addInternalEvent(MInternalEvent event)
	{
		if(ievents==null)
			ievents = new ArrayList<MInternalEvent>();
		ievents.add(event);
	}
	
	/**
	 *  Test if an internal event is contained.
	 */
	public boolean hasInternalEvent(String name)
	{
		boolean ret = false;
		
		if(ievents!=null && name!=null)
		{
			for(MInternalEvent bel: ievents)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get an internal event.
	 */
	public MInternalEvent getInternalEvent(String name)
	{
		MInternalEvent ret = null;
		
		if(ievents!=null && name!=null)
		{
			for(MInternalEvent ievent: ievents)
			{
				if(name.equals(ievent.getName()))
				{
					ret = ievent;
					break;
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Get the messages.
	 *  @return The messages.
	 */
	public List<MMessageEvent> getMessageEvents()
	{
		return messages==null? Collections.EMPTY_LIST: messages;
	}

	/**
	 *  Set the messages.
	 *  @param messages The messages to set.
	 */
	public void setMessageEvents(List<MMessageEvent> messages)
	{
		this.messages = messages;
	}

	/**
	 *  Add a message.
	 */
	public void addMessageEvent(MMessageEvent message)
	{
		if(messages==null)
			messages = new ArrayList<MMessageEvent>();
		messages.add(message);
	}
	
	/**
	 *  Test if a message is contained.
	 */
	public boolean hasMessageEvent(String name)
	{
		boolean ret = false;
		
		if(messages!=null && name!=null)
		{
			for(MMessageEvent bel: messages)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a message event. Null if not found.
	 */
	public MMessageEvent getMessageEvent(String name)
	{
		MMessageEvent ret = null;
		
		if(messages!=null && name!=null)
		{
			for(MMessageEvent bel: messages)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a goal by resolved name. Exception if not found.
	 *  This method is meant handles calls from user code (e.g. createXYZ() in a plan).
	 *  Internally, all references should be mapped to the correct concrete elements already during loading (e.g. config elements).
	 *  @param scope	The local scope.
	 *  @param name	The name, relative to scope.
	 */
	public MGoal	getResolvedGoal(String scope, String name)
	{
		MGoal melement = null;
		name	= scope!=null ? scope + MElement.CAPABILITY_SEPARATOR + MElement.internalName(name) : MElement.internalName(name);
		if(goalreferences!=null && goalreferences.containsKey(name))
		{
			String	ref	= goalreferences.get(name);
			if(ref==null)
			{
				// Abstract element -> create dummy element to hold name.
				melement	= new MGoal();
				melement.setName(name);
			}
			else
			{
				name	= ref;
			}
		}
		if(melement==null)
		{
			melement	= getGoal(name);
		}
		if(melement==null)
		{
			throw new RuntimeException("Goal not found: "+name+(scope!=null ? "("+scope+")" : ""));
		}
		return melement;
	}

	/**
	 *  Get an internal event by resolved name. Exception if not found.
	 *  This method is meant handles calls from user code (e.g. createXYZ() in a plan).
	 *  Internally, all references should be mapped to the correct concrete elements already during loading (e.g. config elements).
	 *  @param scope	The local scope.
	 *  @param name	The name, relative to scope.
	 */
	public MInternalEvent	getResolvedInternalEvent(String scope, String name)
	{
		MInternalEvent melement = null;
		name	= scope!=null ? scope + MElement.CAPABILITY_SEPARATOR + MElement.internalName(name) : MElement.internalName(name);
		if(eventreferences!=null && eventreferences.containsKey(name))
		{
			String	ref	= eventreferences.get(name);
			if(ref==null)
			{
				// Abstract element -> create dummy element to hold name.
				melement	= new MInternalEvent();
				melement.setName(name);
			}
			else
			{
				name	= ref;
			}
		}
		if(melement==null)
		{
			melement	= getInternalEvent(name);
		}
		if(melement==null)
		{
			throw new RuntimeException("Internal event not found: "+name+(scope!=null ? "("+scope+")" : ""));
		}
		return melement;
	}

	/**
	 *  Get a message event by resolved name. Exception if not found.
	 *  This method is meant handles calls from user code (e.g. createXYZ() in a plan).
	 *  Internally, all references should be mapped to the correct concrete elements already during loading (e.g. config elements).
	 *  @param scope	The local scope.
	 *  @param name	The name, relative to scope.
	 */
	public MMessageEvent	getResolvedMessageEvent(String scope, String name)
	{
		MMessageEvent melement = null;
		name	= scope!=null ? scope + MElement.CAPABILITY_SEPARATOR + MElement.internalName(name) : MElement.internalName(name);
		if(eventreferences!=null && eventreferences.containsKey(name))
		{
			String	ref	= eventreferences.get(name);
			if(ref==null)
			{
				// Abstract element -> create dummy element to hold name.
				melement	= new MMessageEvent();
				melement.setName(name);
			}
			else
			{
				name	= ref;
			}
		}
		if(melement==null)
		{
			melement	= getMessageEvent(name);
		}
		if(melement==null)
		{
			throw new RuntimeException("Message event not found: "+name+(scope!=null ? "("+scope+")" : ""));
		}
		return melement;
	}

	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public List<MConfiguration> getConfigurations()
	{
		return configurations==null? Collections.EMPTY_LIST: configurations;
	}

	/**
	 *  Set the configurations.
	 *  @param configurations The configurations to set.
	 */
	public void setConfigurations(List<MConfiguration> configurations)
	{
		this.configurations = configurations;
	}
	
	/**
	 *  Add a configuration.
	 */
	public void addConfiguration(MConfiguration config)
	{
		if(configurations==null)
			configurations = new ArrayList<MConfiguration>();
		configurations.add(config);
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public MConfiguration getConfiguration(String name)
	{
		MConfiguration ret = null;
		if(configurations!=null)
		{
			for(MConfiguration conf: configurations)
			{
				if(conf.getName().equals(name))
				{
					ret = conf;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the services.
	 *  @return The services.
	 */
	public List<MServiceCall> getServices()
	{
		return services;
	}

	/**
	 *  Set the services.
	 *  @param services The services to set.
	 */
	public void setServices(List<MServiceCall> services)
	{
		this.services = services;
	}

	/**
	 *  Get the plan for its name.
	 *  @return The plan.
	 */
	public MServiceCall getService(String name)
	{
		MServiceCall ret = null;
		if(services!=null)
		{
			for(MServiceCall ser: services)
			{
				if(ser.getName().equals(name))
				{
					ret = ser;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Add a service.
	 */
	public void addservice(MServiceCall ser)
	{
		if(services==null)
			services = new ArrayList<MServiceCall>();
		services.add(ser);
	}
	
	/**
	 *  Get the sub capabilities.
	 *  @return The sub capabilities.
	 */
	public List<MCapabilityReference> getCapabilities()
	{
		return subcapabilities==null? Collections.EMPTY_LIST: subcapabilities;
	}

	/**
	 *  Set the sub capabilities.
	 *  @param subcapabilities The sub capabilities to set.
	 */
	public void setCapabilities(List<MCapabilityReference> subcapabilities)
	{
		this.subcapabilities = subcapabilities;
	}

	/**
	 *  Add a sub capability.
	 */
	public void addCapability(MCapabilityReference subcapability)
	{
		if(subcapabilities==null)
			subcapabilities = new ArrayList<MCapabilityReference>();
		subcapabilities.add(subcapability);
	}
	
	
	/**
	 *  Get the expressions.
	 *  @return The expressions.
	 */
	public List<UnparsedExpression> getExpressions()
	{
		return expressions==null? Collections.EMPTY_LIST: expressions;
	}

	/**
	 *  Set the expressions.
	 *  @param expressions The expressions to set.
	 */
	public void setExpressions(List<UnparsedExpression> expressions)
	{
		this.expressions = expressions;
	}

	/**
	 *  Add a expression.
	 */
	public void addExpression(UnparsedExpression expression)
	{
		if(expressions==null)
			expressions = new ArrayList<UnparsedExpression>();
		expressions.add(expression);
	}
	
	/**
	 *  Test if a expression is contained.
	 */
	public boolean hasExpression(String name)
	{
		boolean ret = false;
		
		if(expressions!=null && name!=null)
		{
			for(UnparsedExpression bel: expressions)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a expression.
	 */
	public UnparsedExpression getExpression(String name)
	{
		UnparsedExpression ret = null;
		
		if(expressions!=null && name!=null)
		{
			for(UnparsedExpression bel: expressions)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}

	
	/**
	 *  Get the conditions.
	 *  @return The conditions.
	 */
	public List<MCondition> getConditions()
	{
		return conditions==null? Collections.EMPTY_LIST: conditions;
	}

	/**
	 *  Set the conditions.
	 *  @param conditions The conditions to set.
	 */
	public void setConditions(List<MCondition> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 *  Add a condition.
	 */
	public void addCondition(MCondition condition)
	{
		if(conditions==null)
			conditions = new ArrayList<MCondition>();
		conditions.add(condition);
	}
	
	/**
	 *  Test if a condition is contained.
	 */
	public boolean hasCondition(String name)
	{
		boolean ret = false;
		
		if(conditions!=null && name!=null)
		{
			for(MCondition bel: conditions)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a condition.
	 */
	public MCondition getCondition(String name)
	{
		MCondition ret = null;
		
		if(conditions!=null && name!=null)
		{
			for(MCondition bel: conditions)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Get the pubs.
	 *  @return The pubs
	 */
	public Map<ClassInfo, List<Tuple2<MGoal, String>>> getGoalPublications()
	{
		return pubs;
	}

	/**
	 *  The pubs to set.
	 *  @param pubs The pubs to set
	 */
	public void setGoalPublications(Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		this.pubs = pubs;
	}
	
	/**
	 *  Add a publication info.
	 */
	public void addGoalPublication(ClassInfo ci, MGoal mgoal, String methodname)
	{
		if(pubs==null)
			pubs = new HashMap<ClassInfo, List<Tuple2<MGoal,String>>>();
		List<Tuple2<MGoal, String>> ps = pubs.get(ci);
		if(ps==null)
		{
			ps = new ArrayList<Tuple2<MGoal,String>>();
			pubs.put(ci, ps);
		}
		ps.add(new Tuple2<MGoal, String>(mgoal, methodname));
	}

	/**
	 *  Get the fully qualified belief references (abstract/reference name -> concrete belief name).
	 */
	public Map<String, String> getBeliefReferences()
	{
		return getReferences(beliefreferences);
	}
	
	/**
	 *  Add a belief reference (abstract/reference name -> concrete belief name).
	 *  @param reference The fully qualified abstract / reference belief name. 
	 *  @param concrete The fully qualified concrete belief name.
	 */
	public void addBeliefReference(String reference, String concrete)
	{
		beliefreferences = addReference(beliefreferences, reference, concrete);
	}

	/**
	 *  Get the result mappings (concrete belief->result name).
	 *  Note: result mappings are inverse to reference mappings!
	 */
	public Map<String, String> getResultMappings()
	{
		Map<String, String>	ret;
		if(resultmappings==null)
		{
			ret	= Collections.emptyMap();
		}
		else
		{
			ret	= resultmappings;
		}
		return ret;
	}
	
	/**
	 *  Add a result mapping.
	 *  Note: result mappings are inverse to reference mappings!
	 *  @param belief The concrete belief name (fully qualified). 
	 *  @param result The result name.
	 */
	public void addResultMapping(String belief, String result)
	{
		if(resultmappings==null)
		{
			resultmappings = new LinkedHashMap<String, String>();
		}
		resultmappings.put(belief, result);
	}
	
	/**
	 *  Get the fully qualified expression references (abstract/reference name -> concrete expression name).
	 */
	public Map<String, String> getExpressionReferences()
	{
		return getReferences(expressionreferences);
	}

	/**
	 *  Add a expression reference (abstract/reference name -> concrete expression name).
	 *  @param reference The fully qualified abstract / reference expression name. 
	 *  @param concrete The fully qualified concrete expression name.
	 */
	public void addExpressionReference(String reference, String concrete)
	{
		expressionreferences = addReference(expressionreferences, reference, concrete);
	}
	
	/**
	 *  Get the fully qualified event references (abstract/reference name -> concrete event name).
	 */
	public Map<String, String> getEventReferences()
	{
		return getReferences(eventreferences);
	}

	/**
	 *  Add a event reference (abstract/reference name -> concrete event name).
	 *  @param reference The fully qualified abstract / reference event name. 
	 *  @param concrete The fully qualified concrete event name.
	 */
	public void addEventReference(String reference, String concrete)
	{
		eventreferences = addReference(eventreferences, reference, concrete);
	}

	/**
	 *  Get the fully qualified goal references (abstract/reference name -> concrete goal name).
	 */
	public Map<String, String> getGoalReferences()
	{
		return getReferences(goalreferences);
	}

	/**
	 *  Add a goal reference (abstract/reference name -> concrete goal name).
	 *  @param reference The fully qualified abstract / reference goal name. 
	 *  @param concrete The fully qualified concrete goal name.
	 */
	public void addGoalReference(String reference, String concrete)
	{
		goalreferences = addReference(goalreferences, reference, concrete);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Add a reference and resolve transitive dependencies.
	 */
	protected static Map<String, String> addReference(Map<String, String> references, String reference, String concrete)
	{
		if(references==null)
		{
			references = new LinkedHashMap<String, String>();
		}
		
		// Resolve transitive dependency.
		if(references.containsKey(concrete))
		{
			concrete	= references.get(concrete);
			assert	!references.containsKey(concrete);	// Should be only one level.
		}
		
		// Resolve indirect assignto dependencies (i.e. assignto from reference/abstract)
		for(Entry<String, String> entry: references.entrySet())
		{
			if(SUtil.equals(entry.getValue(), reference))
			{
				entry.setValue(concrete);
			}
		}
		
		references.put(reference, concrete);
		return references;
	}
	
	/**
	 *  Get references or empty map.
	 */
	protected static Map<String, String> getReferences(Map<String, String> references)
	{
		if(references==null)
		{
			references	= Collections.emptyMap();
		}
		return references;
	}
}
