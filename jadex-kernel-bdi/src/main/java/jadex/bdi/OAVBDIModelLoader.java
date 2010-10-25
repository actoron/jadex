package jadex.bdi;

import jadex.bdi.model.BDIParserHelper;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.OAVCapabilityModel;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.GoalDeliberationRules;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.GoalProcessingRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bridge.AbstractErrorReportBuilder;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.parser.conditions.javagrammar.IParserHelper;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.builder.ReteBuilder;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.Constraint;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.io.xml.OAVUserContext;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.xml.StackElement;
import jadex.xml.reader.Reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  Loader for reading agent XMLs into OAV representation.
 */
public class OAVBDIModelLoader	extends AbstractModelLoader
{
	//-------- constants --------

	/** The Jadex agent extension. */
	public static final String FILE_EXTENSION_AGENT = ".agent.xml";

	/** The Jadex capability extension. */
	public static final String FILE_EXTENSION_CAPABILITY = ".capability.xml";

	/** The Jadex properties extension. */
	public static final String FILE_EXTENSION_PROPERTIES = ".properties.xml";

	/** Flag for using all rules. */
	public static final boolean ALL_RULES = false;
	
	/** Turn on debugging output (e.g. for automatic rule selection). */
	protected static boolean DEBUG	= false;
	
	//-------- attributes --------
	
	/** The reader (cached for speed, todo: weak for memory). */
	protected Reader	reader;
	
	//-------- constructors --------
	
	/**
	 *  Create an OAV BDI Model loader.
	 */
	public OAVBDIModelLoader()
	{
		super(new String[]{FILE_EXTENSION_AGENT, FILE_EXTENSION_CAPABILITY, FILE_EXTENSION_PROPERTIES});
		this.reader	= OAVBDIXMLReader.getReader();
	}

	//-------- methods --------

	/**
	 *  Load an agent model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public OAVAgentModel	loadAgentModel(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		return (OAVAgentModel)loadModel(name, FILE_EXTENSION_AGENT, imports, classloader);
	}

	/**
	 *  Load a capability model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public OAVCapabilityModel	loadCapabilityModel(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		return (OAVCapabilityModel)loadModel(name, FILE_EXTENSION_CAPABILITY, imports, classloader);
	}

	//-------- AbstractModelLoader methods --------

	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel	doLoadModel(String name, ResourceInfo info, ClassLoader classloader) throws Exception
	{
		OAVCapabilityModel	ret;

		OAVTypeModel	typemodel	= new OAVTypeModel(name+"_typemodel", classloader);
		// Requires runtime meta model, because e.g. user conditions can refer to runtime elements (belief, goal, etc.) 
		typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		final IOAVState	state	= OAVStateFactory.createOAVState(typemodel);
		
		final Set	types	= new HashSet();
		IOAVStateListener	listener	= new IOAVStateListener()
		{
			public void objectAdded(Object id, OAVObjectType type, boolean root)
			{
				// Add the type and its supertypes (if not already contained).
				while(type!=null && types.add(type))
					type	= type.getSupertype();
			}
			
			public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
			{
			}
			
			public void objectRemoved(Object id, OAVObjectType type)
			{
			}
		};
		
		
		state.addStateListener(listener, false);
		// Use index map to keep insertion order for elements.
		MultiCollection	entries	= new MultiCollection(new IndexMap().getAsMap(), LinkedHashSet.class);
		Object handle = reader.read(info.getInputStream(), classloader, new OAVUserContext(state, entries));
		state.removeStateListener(listener);

		if(state.getType(handle).isSubtype(OAVBDIMetaModel.agent_type))
		{
			ret	=  new OAVAgentModel(state, handle, types, info.getFilename(), info.getLastModified(), entries);
		}
		else
		{
			ret	=  new OAVCapabilityModel(state, handle, types, info.getFilename(), info.getLastModified(), entries);
		}
		
		createAgentModelEntry(ret);
		
		// Build error report.
		ret.getModelInfo().setReport(new AbstractErrorReportBuilder(ret.getModelInfo().getName(), ret.getModelInfo().getFilename(),
			new String[]{"Capability", "Belief", "Goal", "Plan", "Event"}, entries, ret.getDocuments())
		{
			public boolean isInCategory(Object obj, String category)
			{
				boolean	ret	= false;
				if("Capability".equals(category))
				{
					ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.capabilityref_type);
				}
				else if("Belief".equals(category))
				{
					ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.belief_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.beliefset_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.beliefreference_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.beliefsetreference_type);
				}
				else if("Goal".equals(category))
				{
					ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.goal_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.goalreference_type);
				}
				else if("Plan".equals(category))
				{
					ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.plan_type);
				}
				else if("Event".equals(category))
				{
					ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.event_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.internaleventreference_type)
						|| state.getType(obj).isSubtype(OAVBDIMetaModel.messageeventreference_type);
				}
				return ret;
			}
			
			public Object getPathElementObject(Object element)
			{
				return ((StackElement)element).getObject();
			}
			
			public String getObjectName(Object obj)
			{
				String	name	= null;
				if(state.getType(obj).isSubtype(OAVBDIMetaModel.modelelement_type))
				{
					name	= (String)state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name);
				}
				
				if(name==null && state.getType(obj).isSubtype(OAVBDIMetaModel.elementreference_type))
				{
					name	= (String)state.getAttributeValue(obj, OAVBDIMetaModel.elementreference_has_concrete);
				}
				
				if(name==null && state.getType(obj).isSubtype(OAVBDIMetaModel.expression_type))
				{
					Object	exp	=state.getAttributeValue(obj, OAVBDIMetaModel.expression_has_content);
					name	= exp!=null ? ""+exp : null;
				}
				
				if(name==null)
				{
					name	= ""+obj;
				}
				
				return state.getType(obj).getName().substring(1) + " " + name;
			}
		}.buildErrorReport());
		
		return ret;
	}

	/**
	 *  Rules for agent elements have to be created and added to the generic
	 *  BDI interpreter rules.
	 */
	public void	createAgentModelEntry(OAVCapabilityModel model)
	{
		IRulebase rb = model.getRulebase();
		IOAVState state	= model.getState();
		Object mcapa = model.getHandle();
		String[] imports = OAVBDIMetaModel.getImports(state, mcapa);
		
		// Load subcapabilities.
		Collection mcaparefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(mcaparefs!=null)
		{
			for(Iterator it=mcaparefs.iterator(); it.hasNext(); )
			{
				Object mcaparef = it.next();
				String	file	= (String)state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_file);
				try
				{
					OAVCapabilityModel	cmodel	= loadCapabilityModel(file, imports, model.getClassLoader());
					model.addSubcapabilityModel(cmodel);
					if(cmodel.getModelInfo().getReport()!=null)
					{
						Tuple	se	= new Tuple(new Object[]{
							new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa, null),
							new StackElement(new QName("capabilities"), null, null),
							new StackElement(new QName("capability"), mcaparef, null)});
						model.addEntry(se, "Included capability <a href=\"#"+cmodel.getModelInfo().getFilename()+"\">"+cmodel.getName()+"</a> has errors.");
						model.addDocument(cmodel.getModelInfo().getFilename(), cmodel.getModelInfo().getReport().getErrorHTML());
					}
	
					state.setAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability, cmodel.getHandle());
				}
				catch(Exception e)
				{
					Tuple	se	= new Tuple(new Object[]{
						new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa, null),
						new StackElement(new QName("capabilities"), null, null),
						new StackElement(new QName("capability"), mcaparef, null)});
					model.addEntry(se, "Included capability '"+file+"' cannot be loaded: "+e);
				}
			}
		}

		// Build user defined goal conditions and add them to the rule base.
		Collection mgoals = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_goals);
		if(mgoals!=null)
		{
			for(Iterator it=mgoals.iterator(); it.hasNext(); )
			{
				Object mgoal = it.next();
				String gtname = (String)state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name);
				
				// Create rules for lifecycle conditions
				
				Object create = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_creationcondition);
				if(create!=null)
				{
					Object usercond = state.getAttributeValue(create, OAVBDIMetaModel.expression_has_content);
					if(usercond!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "goal_create_"+gtname);
						Boolean	unique	= (Boolean)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_unique);
						if(unique==null || !unique.booleanValue())
						{
							Object[]	tmp	= GoalLifecycleRules.createGoalCreationUserRule(mgoal);
							createUserRule(model, rb, imports, mgoal, create, usercond, rulename, tmp);
						}
						else
						{
							Object[]	tmp	= GoalLifecycleRules.createGoalCreationUniqueUserRule(mgoal, state);
							createUserRule(model, rb, imports, mgoal, create, usercond, rulename, tmp);
						}
					}
				}
				
				Object context = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_contextcondition);
				if(context!=null)
				{
					// Two rules have to be added (negated condition for suspend)
					Object	usercond	= state.getAttributeValue(context, OAVBDIMetaModel.expression_has_content);
					if(usercond!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "goal_option_"+gtname);
						Object[]	tmp	= GoalLifecycleRules.createGoalOptionUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, context, usercond, rulename, tmp);

						rulename = Rulebase.getUniqueRuleName(rb, "goal_suspend_"+gtname);
						tmp	= GoalLifecycleRules.createGoalSuspendUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, context, usercond, rulename, tmp);
					}
				}
				
				Object drop = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_dropcondition);
				if(drop!=null)
				{
					Object usercond = state.getAttributeValue(drop, OAVBDIMetaModel.expression_has_content);
					if(usercond!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "goal_drop_"+gtname);
						Object[]	tmp	= GoalLifecycleRules.createGoalDroppingUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, drop, usercond, rulename, tmp);
					}
				}
				
				// Create recur condition
				Object recur = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurcondition);
				if(recur!=null)
				{
					Object	usercond = state.getAttributeValue(recur, OAVBDIMetaModel.expression_has_content);
					if(usercond!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "goal_recur_"+gtname);
						Object[]	tmp	= GoalProcessingRules.createGoalRecurUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, recur, usercond, rulename, tmp);
					}
				}
				
				// Create deliberation rules
				Collection inhibits = (Collection)state.getAttributeValues(mgoal, OAVBDIMetaModel.goal_has_inhibits);
				if(inhibits!=null)
				{
					for(Iterator it2=inhibits.iterator(); it2.hasNext(); )
					{
						Object	inhibit = it2.next();
						Object	usercond = state.getAttributeValue(inhibit, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							String	ref	= (String)state.getAttributeValue(inhibit, OAVBDIMetaModel.inhibits_has_ref);
							String	inmode	= (String)state.getAttributeValue(inhibit, OAVBDIMetaModel.inhibits_has_inhibit);

							String rulename = Rulebase.getUniqueRuleName(rb, "goal_deliberate_addinstanceinhibition_"+gtname);
							Object[]	tmp	= GoalDeliberationRules.createAddInhibitionLinkUserRule(mgoal, inmode, ref);
							createUserRule(model, rb, imports, mgoal, inhibit, usercond, rulename, tmp);

							rulename = Rulebase.getUniqueRuleName(rb, "goal_deliberate_removeinstanceinhibition_"+gtname);
							tmp	= GoalDeliberationRules.createRemoveInhibitionLinkUserRule(mgoal, inmode, ref);
							createUserRule(model, rb, imports, mgoal, inhibit, usercond, rulename, tmp);
						}
					}
				}
				
				// Create achievegoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.achievegoal_type))
				{
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(target!=null)
					{
						Object usercond = state.getAttributeValue(target, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							String rulename = Rulebase.getUniqueRuleName(rb, "achievegoal_target_"+gtname);
							Object[]	tmp	= GoalProcessingRules.createAchievegoalSucceededUserRule(mgoal);
							createUserRule(model, rb, imports, mgoal, target, usercond, rulename, tmp);
						}
					}
				}
				
				// Create maintaingoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.maintaingoal_type))
				{
					Object maintain = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(maintain!=null)
					{
						Object usercond = state.getAttributeValue(maintain, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							String rulename = Rulebase.getUniqueRuleName(rb, "maintaingoal_maintain_"+gtname);
							Object[]	tmp	= GoalProcessingRules.createMaintaingoalProcessingUserRule(mgoal);
							createUserRule(model, rb, imports, mgoal, maintain, usercond, rulename, tmp);
						}
					}
					
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_targetcondition);
					target	= target!=null ? target : maintain;
					if(target!=null)
					{
						Object	usercond = state.getAttributeValue(target, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							String rulename = Rulebase.getUniqueRuleName(rb, "maintaingoal_target_"+gtname);
							Object[]	tmp	= GoalProcessingRules.createMaintaingoalSucceededUserRule(mgoal);
							createUserRule(model, rb, imports, mgoal, target, usercond, rulename, tmp);
						}
					}
				}
				
				// Create rules for dynamic parameter values.
				createDynamicParameterValuesConditions(model, mgoal, rb, imports);
				
				// Create rules for dynamic parameter set values.
				createDynamicParameterSetValuesConditions(model, mgoal, rb, imports);
			}
		}
		
		// Build user defined plan conditions and add them to the rule base.
		Collection mplans = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_plans);
		if(mplans!=null)
		{
			for(Iterator it=mplans.iterator(); it.hasNext(); )
			{
				Object mplan = it.next();
				
				// Create rules for plans
				
				Object trigger = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
				if(trigger!=null)
				{
					Object create = state.getAttributeValue(trigger, OAVBDIMetaModel.plantrigger_has_condition);
					if(create!=null)
					{
						Object usercond = state.getAttributeValue(create, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							String rulename = Rulebase.getUniqueRuleName(rb, "plan_create_"+mplan.toString());
							Object[]	tmp	= PlanRules.createPlanCreationUserRule(mplan);
							createUserRule(model, rb, imports, mplan, create, usercond, rulename, tmp);
						}
					}
				}
				
				Object context = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_contextcondition);
				if(context!=null)
				{
					Object usercond = state.getAttributeValue(context, OAVBDIMetaModel.expression_has_content);
					if(usercond!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "plan_context_"+mplan.toString());
						Object[]	tmp	= PlanRules.createPlanContextInvalidUserRule(mplan);
						createUserRule(model, rb, imports, mplan, context, usercond, rulename, tmp);
					}
				}
				
				// Create rules for dynamic parameter values.
				createDynamicParameterValuesConditions(model, mplan, rb, imports);
				
				// Create rules for dynamic parameter set values.
				createDynamicParameterSetValuesConditions(model, mplan, rb, imports);
			}
		}
		
		// Build user defined dynamic belief conditions and add them to the rule base.
		Collection mbeliefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefs);
		if(mbeliefs!=null)
		{
			for(Iterator it=mbeliefs.iterator(); it.hasNext(); )
			{
				Object mbel = it.next();
				
				// Create rules for dynamic beliefs
				
				Object	evamode	= state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
				if(OAVBDIMetaModel.EVALUATIONMODE_PUSH.equals(evamode))
				{
					Object fact = state.getAttributeValue(mbel, OAVBDIMetaModel.belief_has_fact);
					if(fact!=null)
					{
						Object usercond = state.getAttributeValue(fact, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							Variable	var	= null;
							String	varname	= (String)state.getAttributeValue(fact, OAVBDIMetaModel.expression_has_variable);
							if(varname!=null)
							{
								Class	clazz	= (Class)state.getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
								var	= new Variable(varname, state.getTypeModel().getJavaType(clazz));
							}
							else
							{
								var	= new Variable("?ret", OAVJavaType.java_object_type);
							}
							String btname = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
							String rulename = Rulebase.getUniqueRuleName(rb, "belief_dynamicfact_"+btname);
							Object[]	tmp	= BeliefRules.createDynamicBeliefUserRule(mbel, var);
							createUserRule(model, rb, imports, null, fact, usercond, rulename, tmp);
						}
					}
				}
			}
		}
		
		// Build user defined dynamic belief set conditions and add them to the rule base.
		Collection mbeliefsets = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_beliefsets);
		if(mbeliefsets!=null)
		{
			for(Iterator it=mbeliefsets.iterator(); it.hasNext(); )
			{
				Object mbelset = it.next();
				
				// Create rules for dynamic beliefsets
				
				Object	evamode	= state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_evaluationmode);
				if(OAVBDIMetaModel.EVALUATIONMODE_PUSH.equals(evamode))
				{
					Object facts = state.getAttributeValue(mbelset, OAVBDIMetaModel.beliefset_has_factsexpression);
					if(facts!=null)
					{
						Object usercond = state.getAttributeValue(facts, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							Variable	var	= null;
							String	varname	= (String)state.getAttributeValue(facts, OAVBDIMetaModel.expression_has_variable);
							if(varname!=null)
							{
								Class	clazz	= (Class)state.getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
								var	= new Variable(varname, state.getTypeModel().getJavaType(clazz));
							}
							else
							{
								var	= new Variable("$?ret", OAVJavaType.java_object_type);
							}
							String btname = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
							String rulename = Rulebase.getUniqueRuleName(rb, "beliefset_dynamicfacts_"+btname);
							Object[]	tmp	= BeliefRules.createDynamicBeliefSetUserRule(mbelset, var);
							createUserRule(model, rb, imports, null, facts, usercond, rulename, tmp);
						}
					}
				}
			}
		}
		
		// Build user defined conditions and add them to the rule base.
		Collection mconds = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_conditions);
		if(mconds!=null)
		{
			for(Iterator it=mconds.iterator(); it.hasNext(); )
			{
				Object mcond = it.next();
				Object usercond = state.getAttributeValue(mcond, OAVBDIMetaModel.expression_has_content);
				if(usercond!=null)
				{
					String name = (String)state.getAttributeValue(mcond, OAVBDIMetaModel.modelelement_has_name);
					String rulename = Rulebase.getUniqueRuleName(rb, "condition_"+name);
					Object[]	tmp	= BeliefRules.createConditionUserRule(mcond);
					createUserRule(model, rb, imports, null, mcond, usercond, rulename, tmp);
				}
			}
		}
		
		// For an agent model build the rete network.
		if(model instanceof OAVAgentModel)
		{
			Rulebase compressed	= new Rulebase();
			
			// Add basic BDI rules.
			for(Iterator rules=BDIInterpreter.RULEBASE.getRules().iterator(); rules.hasNext(); )
			{
				IRule	rule	= (IRule)rules.next();
				if(ALL_RULES || checkRule(rule, model.getTypes()))
					compressed.addRule(rule);
			}
			
			// Add custom agent/capability specific rules.
			for(Iterator rules=rb.getRules().iterator(); rules.hasNext(); )
			{
				IRule	rule	= (IRule)rules.next();
				if(ALL_RULES || checkRule(rule, model.getTypes()))
					compressed.addRule(rule);
			}
			
			if(DEBUG)
				System.out.println("Rules for agent model "+model.getName()+" ("+compressed.getRules().size()+" rules)"+": "+model.getTypes());
			
			// Todo: use factory for hiding rule engine implementation. 
			RetePatternMatcherFunctionality pm = new RetePatternMatcherFunctionality(compressed);
			((OAVAgentModel)model).setMatcherFunctionality(pm);
			ReteBuilder builder = pm.getReteNode().getBuilder();
			if(builder!=null && ReteBuilder.REPORTING)
				System.out.println(builder.getBuildReport());
			
			((OAVAgentModel)model).addAgentProperties();
		}
	}

	/**
	 *  Create a user rule.
	 *  @param model	The capability model.
	 *  @param imports	The imports.
	 *  @param melement	The element that holds the condition, if any (e.g. mgoal or mplan).
	 *  @param mcondition	The mcondition.
	 *  @param usercond	The user condition (ADF text).
	 *  @param rulename	The name of the rule to create.
	 *  @param tmp	The rule template [predefined condition, action, priority evaluator(optional), return variable(optional), invert (optional)].
	 *  @return The created rule.
	 */
	protected IRule	createUserRule(OAVCapabilityModel model, IRulebase rb,
			String[] imports, Object melement, Object mcondition,
			Object usercond, String rulename, Object[] tmp)
	{
		IOAVState state	= model.getState();
		Object mcapa = model.getHandle();
		IRule	ret	= null;
		try
		{
			boolean	invert	= tmp.length>=5 && Boolean.TRUE.equals(tmp[4]);
			if(usercond instanceof String)
			{
				String language = (String)state.getAttributeValue(mcondition, OAVBDIMetaModel.expression_has_language);
				IParserHelper	helper	= new BDIParserHelper((ICondition)tmp[0], mcapa, melement, state);
				ICondition	cond	= ParserHelper.parseCondition((ICondition)tmp[0], (String)usercond, language, state.getTypeModel(), imports, null, helper, tmp.length>=4 ? (Variable)tmp[3] : null, invert);
				ret	= tmp.length==2
					? new Rule(rulename, cond, (IAction)tmp[1])
					: new Rule(rulename, cond, (IAction)tmp[1], (IPriorityEvaluator)tmp[2]);
			}
			
			// Compatibility code for clips conditions: Todo remove
			else
			{
				ICondition	cond	= new AndCondition(new ICondition[]{(ICondition)tmp[0],
					invert ? new NotCondition((ICondition)usercond) : (ICondition)usercond });
				ret	= tmp.length==2
					? new Rule(rulename, cond, (IAction)tmp[1])
					: new Rule(rulename, cond, (IAction)tmp[1], (IPriorityEvaluator)tmp[2]);
			}
			
			rb.addRule(ret);
		}
		catch(RuntimeException e)
		{
			Tuple	se;
			if(melement!=null)
			{
				se	= new Tuple(new Object[]{
					new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa, null),
					new StackElement(new QName(state.getType(melement).getName()), melement, null),
					new StackElement(new QName(state.getType(mcondition).getName()), mcondition, null)});
			}
			else
			{
				se	= new Tuple(new Object[]{
					new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa, null),
					new StackElement(new QName(state.getType(mcondition).getName()), mcondition, null)});				
			}
			model.addEntry(se, "Error in condition: "+e);
		}
		return ret;
	}

	/**
	 *  Create conditions for dynamic parameter of an processable model element.
	 *  @param mpe The processable model element.
	 *  @param state The state.
	 *  @param rb The rulebase.
	 */
	protected void createDynamicParameterValuesConditions(OAVCapabilityModel model, Object mpe, IRulebase rb, String[] imports)
	{
		IOAVState state	= model.getState();
		// Create rules for dynamic parameter value.
		
		Collection mparams = state.getAttributeValues(mpe, OAVBDIMetaModel.parameterelement_has_parameters);
		if(mparams!=null)
		{
			for(Iterator it2=mparams.iterator(); it2.hasNext(); )
			{
				Object mparam = it2.next();
				
				Object	evamode	= state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_evaluationmode);
				if(OAVBDIMetaModel.EVALUATIONMODE_PUSH.equals(evamode))
				{
					Object value = state.getAttributeValue(mparam, OAVBDIMetaModel.parameter_has_value);
					if(value!=null)
					{
						Object	usercond	= state.getAttributeValue(value, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							Variable	var	= null;
							String	varname	= (String)state.getAttributeValue(value, OAVBDIMetaModel.expression_has_variable);
							if(varname!=null)
							{
								Class	clazz	= (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
								var	= new Variable(varname, state.getTypeModel().getJavaType(clazz));
							}
							else
							{
								var	= new Variable("?ret", OAVJavaType.java_object_type);
							}
							String	ptname	= (String)state.getAttributeValue(mparam, OAVBDIMetaModel.modelelement_has_name);
							String	rulename	= Rulebase.getUniqueRuleName(rb, "parameter_dynamicvalue_"+state.getAttributeValue(mpe, OAVBDIMetaModel.modelelement_has_name)+"_"+ptname);
							Object[]	tmp	= BeliefRules.createDynamicParameterUserRule(mpe, ptname, var);
							createUserRule(model, rb, imports, mpe, value, usercond, rulename, tmp);
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Create conditions for dynamic parameter sets of an processable model element.
	 *  @param mpe The processable model element.
	 *  @param state The state.
	 *  @param rb The rulebase.
	 */
	protected void createDynamicParameterSetValuesConditions(OAVCapabilityModel model, Object mpe, IRulebase rb, String[] imports)
	{
		IOAVState state	= model.getState();
		// Create rules for dynamic parameter set values.
	
		Collection mparamsets = state.getAttributeValues(mpe, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(mparamsets!=null)
		{
			for(Iterator it2=mparamsets.iterator(); it2.hasNext(); )
			{
				Object mparamset = it2.next();
				
				Object	evamode	= state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_evaluationmode);
				if(OAVBDIMetaModel.EVALUATIONMODE_PUSH.equals(evamode))
				{
					Object values = state.getAttributeValue(mparamset, OAVBDIMetaModel.parameterset_has_valuesexpression);
					if(values!=null)
					{
						Object usercond = state.getAttributeValue(values, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							Variable	var	= null;
							String	varname	= (String)state.getAttributeValue(values, OAVBDIMetaModel.expression_has_variable);
							if(varname!=null)
							{
								Class	clazz	= (Class)state.getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
								var	= new Variable(varname, state.getTypeModel().getJavaType(clazz));
							}
							else
							{
								var	= new Variable("$?ret", OAVJavaType.java_object_type);
							}
							String ptname = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.modelelement_has_name);
							String rulename = Rulebase.getUniqueRuleName(rb, "parameterset_dynamicvalues_"+state.getAttributeValue(mpe, OAVBDIMetaModel.modelelement_has_name)+"_"+ptname);
							Object[]	tmp	= BeliefRules.createDynamicParameterSetUserRule(mpe, ptname, var);
							createUserRule(model, rb, imports, mpe, values, usercond, rulename, tmp);
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Check if a rule needs to be added, based on objects in model.
	 *  If no objects exist for a given object condition, the rule can
	 *  never trigger and therefore can be ignored.
	 *  @param rule	The rule.
	 *  @param types	The types of which objects are contained in the model.
	 *  @return True, if the rule needs to be added.
	 */
	protected boolean	checkRule(IRule rule, Set types)
	{
		boolean	check	= true;
		List	conditions	= new ArrayList();
		conditions.add(rule.getCondition());
		for(int i=0; check && i<conditions.size(); i++)
		{
			// Expand AND conditions to check contained object conditions (if any).
			if(conditions.get(i) instanceof AndCondition)
			{
				conditions.addAll(((AndCondition)conditions.get(i)).getConditions());
			}

			// Check object conditions, if some object is available.
			if(conditions.get(i) instanceof ObjectCondition)
			{
				ObjectCondition	oc	= (ObjectCondition)conditions.get(i);
				// Only ignore rule, when type is part of agent meta(!) model.
				OAVObjectType	type	= oc.getObjectType();
				OAVObjectType	mtype	= (OAVObjectType)OAVBDIRuntimeModel.modelmap.get(type);
				check	= types.contains(type)
					|| mtype!=null && types.contains(mtype)
					|| mtype==null && !OAVBDIMetaModel.bdimm_type_model.contains(type);
				
				if(check)
				{
					// Check for navigating constraints.
					List	cons	= oc.getConstraints();
					for(int c=0; check && cons!=null && c<cons.size(); c++)
					{
						if(cons.get(c) instanceof Constraint)
						{
							Object	source	= ((Constraint)cons.get(c)).getValueSource();
							if(source instanceof OAVAttributeType[])
							{
								OAVAttributeType[]	attrs	= (OAVAttributeType[])source;
								// Contraint fails if some intermediate value (0..length-1) is not accessible.
								for(int a=0; check && a<attrs.length-1; a++)
								{
									type	= attrs[a].getType();
									mtype	= (OAVObjectType)OAVBDIRuntimeModel.modelmap.get(type);
									check	= types.contains(type)
										|| mtype!=null && types.contains(mtype)
										|| mtype==null && !OAVBDIMetaModel.bdimm_type_model.contains(type);
								}
							}
						}
					}
				}
				
				if(DEBUG && !check)
					System.out.println("Ignored rule "+rule.getName()+" due to missing objects of type "+type);
			}
		}
		
		if(DEBUG && check)
			System.out.println("Using rule "+rule.getName());
		return check;
	}
}
