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
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.component.ComponentXMLReader;
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
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.xml.StackElement;
import jadex.xml.reader.Reader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* $if !android $ */
import javax.xml.namespace.QName;
/* $else $
import javaxx.xml.namespace.QName;
$endif $ */

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
	
	/** Key for the OAV root object in the read context. */
	public static final String CONTEXT_OAVROOT = "oavroot";
	
	/** Turn on debugging output (e.g. for automatic rule selection). */
	protected static boolean DEBUG	= false;
	
	//-------- attributes --------
	
	/** The kernel properties (i.e. plan executors). */
	protected Map	properties;
	
	/** The reader (cached for speed, todo: weak for memory). */
	protected Reader	reader;
	
	/** The platform root. */
	protected IComponentIdentifier	root;
	
	//-------- constructors --------
	
	/**
	 *  Create an OAV BDI Model loader.
	 */
	public OAVBDIModelLoader(Map properties, IComponentIdentifier root)
	{
		super(new String[]{FILE_EXTENSION_AGENT, FILE_EXTENSION_CAPABILITY, FILE_EXTENSION_PROPERTIES});
		this.properties	= properties;
		this.reader	= OAVBDIXMLReader.getReader();
		this.root	= root;
	}

	//-------- methods --------

	/**
	 *  Load an agent model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public OAVAgentModel	loadAgentModel(String name, String[] imports, ClassLoader classloader, Object context) throws Exception
	{
		return (OAVAgentModel)loadModel(name, FILE_EXTENSION_AGENT, imports, classloader, context);
	}

	/**
	 *  Load a capability model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public OAVCapabilityModel	loadCapabilityModel(String name, String[] imports, ClassLoader classloader, Object context) throws Exception
	{
		return (OAVCapabilityModel)loadModel(name, FILE_EXTENSION_CAPABILITY, imports, classloader, context);
	}

	//-------- AbstractModelLoader methods --------

	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel	doLoadModel(String name, String[] imports, ResourceInfo info, ClassLoader classloader, Object context)
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
		ModelInfo	mi	= null;
		Object	handle	= null;
		Map	user	= new HashMap();
		user.put(OAVObjectReaderHandler.CONTEXT_STATE, state);
		user.put(ComponentXMLReader.CONTEXT_ENTRIES, entries);
		try
		{
			mi = (ModelInfo)reader.read(OAVBDIXMLReader.getReaderManager(), OAVBDIXMLReader.getReaderHandler(), info.getInputStream(), classloader, user);
			handle	= user.get(CONTEXT_OAVROOT);
		}
		catch(Exception e)
		{
			entries.put(new Tuple(new Object[]{new StackElement(new QName("capability"), "XML file")}), e.toString());
		}
		state.removeStateListener(listener);
		
		if(handle!=null)
		{
			if(state.getType(handle).isSubtype(OAVBDIMetaModel.agent_type))
			{
				ret	=  new OAVAgentModel(state, handle, mi, types, info.getLastModified(), entries);
				mi.setStartable(true);
			}
			else
			{
				ret	=  new OAVCapabilityModel(state, handle, mi, types, info.getLastModified(), entries);
			}
			
			// Need to set class loader before create agent model entry to load subcapabilities.
//			mi.setClassloader(classloader);
			IResourceIdentifier rid = (IResourceIdentifier)((Object[])context)[0];
			IComponentIdentifier root = (IComponentIdentifier)((Object[])context)[1];
			mi.setFilename(info.getFilename());
			mi.setType(ret instanceof OAVAgentModel ? BDIAgentFactory.FILETYPE_BDIAGENT : BDIAgentFactory.FILETYPE_BDICAPABILITY);
			if(rid==null)
			{
				String src = SUtil.getCodeSource(mi.getFilename(), mi.getPackage());
				URL url = SUtil.toURL(src);
				rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
			}
			mi.setResourceIdentifier(rid);
			if(!mi.checkName())
			{
				entries.put(new Tuple(new Object[]{new StackElement(new QName("capability"), handle)}), "Name '"+mi.getName()+"' does not match file name '"+mi.getFilename()+"'.");				
			}
			if(!mi.checkPackage())
			{
				entries.put(new Tuple(new Object[]{new StackElement(new QName("capability"), handle)}), "Package '"+mi.getPackage()+"' does not match file name '"+mi.getFilename()+"'.");				
			}
			createAgentModelEntry(ret, mi);
			
			
			// Initialize the model info.
			ret.initModelInfo();
		}
		else
		{
			// Todo: capability or agent?
			ret	=  new OAVCapabilityModel(state, handle, mi, types, info.getLastModified(), entries);
		}
		
		return ret;
	}

	/**
	 *  Rules for agent elements have to be created and added to the generic
	 *  BDI interpreter rules.
	 */
	public void	createAgentModelEntry(OAVCapabilityModel model, ModelInfo info)
	{
//		System.out.println("createAgentModelEntry: "+info.getFullName());
		IRulebase rb = model.getRulebase();
		IOAVState state	= model.getState();
		Object mcapa = model.getHandle();
		String[] imports = model.getModelInfo().getAllImports();
		
		// Load subcapabilities.
		Collection mcaparefs = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(mcaparefs!=null)
		{
			Object[]	mcrs	= mcaparefs.toArray(); 
			for(int i=0; i<mcrs.length; i++)
			{
				String	file	= (String)state.getAttributeValue(mcrs[i], OAVBDIMetaModel.capabilityref_has_file);
				try
				{
					OAVCapabilityModel	cmodel	= loadCapabilityModel(file, imports, model.getState().getTypeModel().getClassLoader(), new Object[]{info.getResourceIdentifier(), root});
					model.addSubcapabilityModel(cmodel);
					if(cmodel.getModelInfo().getReport()!=null)
					{
						Tuple	se	= new Tuple(new Object[]{
							new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa),
							new StackElement(new QName("capabilities"), null),
							new StackElement(new QName("capability"), mcrs[i])});
						model.addEntry(se, "Included capability <a href=\"#"+cmodel.getModelInfo().getFilename()+"\">"+cmodel.getModelInfo().getName()+"</a> has errors.");
						model.addDocument(cmodel.getModelInfo().getFilename(), cmodel.getModelInfo().getReport().getErrorHTML());
					}
	
					state.setAttributeValue(mcrs[i], OAVBDIMetaModel.capabilityref_has_capability, cmodel.getHandle());
				}
				catch(Exception e)
				{
					// Remove broken capability reference as otherwise nullpointerexceptions will occur.
					String	name	= (String)state.getAttributeValue(mcrs[i], OAVBDIMetaModel.modelelement_has_name);
					state.removeAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, name);
					
					Tuple	se	= new Tuple(new Object[]{
						new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa),
						new StackElement(new QName("capabilities"), null),
						new StackElement(new QName("capability"), mcrs[i])});	// Todo: mcaparef no longer in state!=
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
					String rulename = Rulebase.getUniqueRuleName(rb, "goal_create_"+gtname);
					Boolean	unique	= (Boolean)state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_unique);
					if(unique==null || !unique.booleanValue())
					{
						Object[]	tmp	= GoalLifecycleRules.createGoalCreationUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, create, rulename, tmp);
					}
					else
					{
						Object[]	tmp	= GoalLifecycleRules.createGoalCreationUniqueUserRule(mgoal, state);
						createUserRule(model, rb, imports, mgoal, create, rulename, tmp);
					}
				}
				
				Object context = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_contextcondition);
				if(context!=null)
				{
					// Two rules have to be added (negated condition for suspend)
					String rulename = Rulebase.getUniqueRuleName(rb, "goal_option_"+gtname);
					Object[]	tmp	= GoalLifecycleRules.createGoalOptionUserRule(mgoal);
					createUserRule(model, rb, imports, mgoal, context, rulename, tmp);

					rulename = Rulebase.getUniqueRuleName(rb, "goal_suspend_"+gtname);
					tmp	= GoalLifecycleRules.createGoalSuspendUserRule(mgoal);
					createUserRule(model, rb, imports, mgoal, context, rulename, tmp);
				}
				
				Object drop = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_dropcondition);
				if(drop!=null)
				{
					String rulename = Rulebase.getUniqueRuleName(rb, "goal_drop_"+gtname);
					Object[]	tmp	= GoalLifecycleRules.createGoalDroppingUserRule(mgoal);
					createUserRule(model, rb, imports, mgoal, drop, rulename, tmp);
				}
				
				// Create recur condition
				Object recur = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurcondition);
				if(recur!=null)
				{
					String rulename = Rulebase.getUniqueRuleName(rb, "goal_recur_"+gtname);
					Object[]	tmp	= GoalProcessingRules.createGoalRecurUserRule(mgoal);
					createUserRule(model, rb, imports, mgoal, recur, rulename, tmp);
				}
				
				// Create deliberation rules
				Collection inhibits = (Collection)state.getAttributeValues(mgoal, OAVBDIMetaModel.goal_has_inhibits);
				if(inhibits!=null)
				{
					for(Iterator it2=inhibits.iterator(); it2.hasNext(); )
					{
						Object	inhibit = it2.next();
						String	text	= (String)state.getAttributeValue(inhibit, OAVBDIMetaModel.expression_has_text);
						Object	cond	= state.getAttributeValue(inhibit, OAVBDIMetaModel.expression_has_parsed);
						if(text!=null || cond!=null)
						{
							String	ref	= (String)state.getAttributeValue(inhibit, OAVBDIMetaModel.inhibits_has_ref);
							String	inmode	= (String)state.getAttributeValue(inhibit, OAVBDIMetaModel.inhibits_has_inhibit);
		
							String rulename = Rulebase.getUniqueRuleName(rb, "goal_deliberate_addinstanceinhibition_"+gtname);
							Object[]	tmp	= GoalDeliberationRules.createAddInhibitionLinkUserRule(mgoal, inmode, ref);
							createUserRule(model, rb, imports, mgoal, inhibit, rulename, tmp);
		
							rulename = Rulebase.getUniqueRuleName(rb, "goal_deliberate_removeinstanceinhibition_"+gtname);
							tmp	= GoalDeliberationRules.createRemoveInhibitionLinkUserRule(mgoal, inmode, ref);
							createUserRule(model, rb, imports, mgoal, inhibit, rulename, tmp);
						}
					}
				}
				
				// Create achievegoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.achievegoal_type))
				{
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(target!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "achievegoal_target_"+gtname);
						Object[]	tmp	= GoalProcessingRules.createAchievegoalSucceededUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, target, rulename, tmp);
					}
				}
				
				// Create maintaingoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.maintaingoal_type))
				{
					Object maintain = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(maintain!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "maintaingoal_maintain_"+gtname);
						Object[]	tmp	= GoalProcessingRules.createMaintaingoalProcessingUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, maintain, rulename, tmp);
					}
					
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_targetcondition);
					target	= target!=null ? target : maintain;
					if(target!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "maintaingoal_target_"+gtname);
						Object[]	tmp	= GoalProcessingRules.createMaintaingoalSucceededUserRule(mgoal);
						createUserRule(model, rb, imports, mgoal, target, rulename, tmp);
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
				
				// Check if body types are supported.
				Object	mbody	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
				String	type	= (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_type);
				if(!properties.containsKey("planexecutor_"+type))
				{
					Tuple	se	= new Tuple(new Object[]{
						new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa),
						new StackElement(new QName(state.getType(mplan).getName()), mplan),
						new StackElement(new QName(state.getType(mbody).getName()), mbody)});
					model.addEntry(se, "No executor for plan body type: "+type);
				}
				
				// Create rules for plans
				
				Object trigger = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
				if(trigger!=null)
				{
					Object create = state.getAttributeValue(trigger, OAVBDIMetaModel.plantrigger_has_condition);
					if(create!=null)
					{
						String rulename = Rulebase.getUniqueRuleName(rb, "plan_create_"+mplan.toString());
						Object[]	tmp	= PlanRules.createPlanCreationUserRule(mplan);
						createUserRule(model, rb, imports, mplan, create, rulename, tmp);
					}
				}
				
				Object context = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_contextcondition);
				if(context!=null)
				{
					String rulename = Rulebase.getUniqueRuleName(rb, "plan_context_"+mplan.toString());
					Object[]	tmp	= PlanRules.createPlanContextInvalidUserRule(mplan);
					createUserRule(model, rb, imports, mplan, context, rulename, tmp);
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
						createUserRule(model, rb, imports, null, fact, rulename, tmp);
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
						createUserRule(model, rb, imports, null, facts, rulename, tmp);
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
				String name = (String)state.getAttributeValue(mcond, OAVBDIMetaModel.modelelement_has_name);
				String rulename = Rulebase.getUniqueRuleName(rb, "condition_"+name);
				Object[]	tmp	= BeliefRules.createConditionUserRule(mcond);
				createUserRule(model, rb, imports, null, mcond, rulename, tmp);
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
				System.out.println("Rules for agent model "+model.getModelInfo().getName()+" ("+compressed.getRules().size()+" rules)"+": "+model.getTypes());
			
			// Todo: use factory for hiding rule engine implementation. 
			RetePatternMatcherFunctionality pm = new RetePatternMatcherFunctionality(compressed);
			((OAVAgentModel)model).setMatcherFunctionality(pm);
			ReteBuilder builder = pm.getReteNode().getBuilder();
			if(builder!=null && ReteBuilder.REPORTING)
				System.out.println(builder.getBuildReport());
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
	protected IRule	createUserRule(OAVCapabilityModel model, IRulebase rb, String[] imports,
		Object melement, Object mcondition, String rulename, Object[] tmp)
	{
		IOAVState state	= model.getState();
		Object mcapa = model.getHandle();
		IRule	ret	= null;
		try
		{
			boolean	invert	= tmp.length>=5 && Boolean.TRUE.equals(tmp[4]);
			Object	usercond	= state.getAttributeValue(mcondition, OAVBDIMetaModel.expression_has_parsed);

			// Compatibility code for clips conditions: Todo remove
			if(usercond instanceof ICondition)
			{
				ICondition	cond	= new AndCondition(new ICondition[]{(ICondition)tmp[0],
						invert ? new NotCondition((ICondition)usercond) : (ICondition)usercond });
					ret	= tmp.length==2
						? new Rule(rulename, cond, (IAction)tmp[1])
						: new Rule(rulename, cond, (IAction)tmp[1], (IPriorityEvaluator)tmp[2]);
			}
			
			else
			{
				String	text	= (String)state.getAttributeValue(mcondition, OAVBDIMetaModel.expression_has_text);
				String language = (String)state.getAttributeValue(mcondition, OAVBDIMetaModel.expression_has_language);
				IParserHelper	helper	= new BDIParserHelper((ICondition)tmp[0], mcapa, melement, state);
				ICondition	cond	= ParserHelper.parseCondition((ICondition)tmp[0], text, language, state.getTypeModel(), imports, null, helper, tmp.length>=4 ? (Variable)tmp[3] : null, invert);
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
					new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa),
					new StackElement(new QName(state.getType(melement).getName()), melement),
					new StackElement(new QName(state.getType(mcondition).getName()), mcondition)});
			}
			else
			{
				se	= new Tuple(new Object[]{
					new StackElement(new QName(model instanceof OAVAgentModel ? "agent" : "capability"), mcapa),
					new StackElement(new QName(state.getType(mcondition).getName()), mcondition)});				
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
						createUserRule(model, rb, imports, mpe, value, rulename, tmp);
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
						createUserRule(model, rb, imports, mpe, values, rulename, tmp);
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
				check	= type instanceof OAVJavaType || types.contains(type)
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
