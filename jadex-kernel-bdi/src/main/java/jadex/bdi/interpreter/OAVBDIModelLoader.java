package jadex.bdi.interpreter;

import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.concurrent.ThreadPool;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.builder.ReteBuilder;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.io.xml.IOAVXMLMapping;
import jadex.rules.state.io.xml.Reader;
import jadex.rules.state.javaimpl.OAVState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.SAXParserFactory;

/**
 *  Loader for reading agent XMLs into OAV representation.
 */
public class OAVBDIModelLoader
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
	
	/** The xmlmapping (cached for speed, todo: weak for memory). */
	protected IOAVXMLMapping	mapping;
		
	/** The model cache (filename -> oav capability model). */
	protected Map modelcache;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- constructors --------
	
	/**
	 *  Create an OAV BDI Model loader.
	 */
	public OAVBDIModelLoader(Map props)
	{
		this.reader	= new Reader();
		this.mapping = OAVBDIMetaModel.getXMLMapping(props);
		this.modelcache	= new HashMap();	
	}

	//-------- methods --------

	/**
	 *  Load an agent model into a state.
	 *  @param name	The name or filename of the model to load.
	 *  @param imports Optional imports, used to resolve the name to a filename, if necessary. 
	 *  @return The reference to the loaded model in the state.
	 */
	// Todo: remove imports, support only fully qualified.
	public OAVAgentModel loadAgentModel(String name, String[] imports) throws IOException
	{
		return (OAVAgentModel)loadModel(name, FILE_EXTENSION_AGENT, imports);
	}
	
	/**
	 *  Load a capability model into a state.
	 *  @param parent	The parent capability model.
	 *  @param name	The name or filename of the model to load.
	 *  @param imports Optional imports, used to resolve the name to a filename, if necessary. 
	 *  @return The reference to the loaded model in the state.
	 */
	// Todo: remove imports, support only fully qualified.
	public OAVCapabilityModel loadCapabilityModel(String name, String[] imports) throws IOException
	{
		return loadModel(name, FILE_EXTENSION_CAPABILITY, imports);
	}

	/**
	 *  Load a properties model into a state.
	 *  @param state	The state to load the model into.
	 *  @param name	The name or filename of the model to load.
	 *  @param imports Optional imports, used to resolve the name to a filename, if necessary. 
	 *  @return The reference to the loaded model in the state.
	 * /
	// Todo: remove imports, support only fully qualified.
	public Object loadPropertyModel(IOAVState state, String name, String[] imports, OAVCapabilityModel parent) throws IOException
	{
		return loadModel(state, name, FILE_EXTENSION_PROPERTIES, imports, parent);
	}*/
	
	/**
	 *  Load an xml Jadex model.
	 *  Creates file name when specified with or without package.
	 *  Transforms the model via the Jadex default xslt when not null.
	 *  Configures the model via setup() when configurable.
	 *  @param xml The filename | fully qualified classname
	 *  @return The loaded model.
	 */
	// Todo: fix directory stuff!???
	public static ResourceInfo getResourceInfo(String xml, String suffix, String[] imports, ClassLoader classloader) throws IOException
	{
		if(xml==null)
			throw new IllegalArgumentException("Required ADF name nulls.");
		if(suffix==null && !xml.endsWith(FILE_EXTENSION_AGENT) && !xml.endsWith(FILE_EXTENSION_CAPABILITY))
			throw new IllegalArgumentException("Required suffix nulls.");

		if(suffix==null)
			suffix="";
		
		// Try to find directly as absolute path.
		String resstr = xml;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);

		if(ret==null || ret.getInputStream()==null)
		{
			// Fully qualified package name? Can also be full package name with empty package ;-)
			//if(xml.indexOf(".")!=-1)
			//{
				resstr	= SUtil.replace(xml, ".", "/") + suffix;
				//System.out.println("Trying: "+resstr);
				ret	= SUtil.getResourceInfo0(resstr, classloader);
			//}

			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + xml + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import
				else if(imports[i].endsWith(xml))
				{
					resstr = SUtil.replace(imports[i], ".", "/") + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
			}
		}

		if(ret==null || ret.getInputStream()==null)
			throw new IOException("File "+xml+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}
	
	// todo: synchronize modelcache!
	
	/**
	 *  Set the class loader.
	 *  @param classloader The class loader.
	 */
	public synchronized void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
		modelcache.clear();
	}

	//-------- helper methods --------

	/**
	 *  Load a model.
	 */
	protected synchronized OAVCapabilityModel loadModel(String name, String extension, String[] imports) throws IOException
	{
		if(extension==null)
			extension = getFilenameExtension(name);
		
		// Lookup cache by name/extension/imports
		OAVCapabilityModel cached = null;
		Object[] keys	= imports!=null? new Object[imports.length+2]: new Object[2];
		keys[0]	= name;
		keys[1]	= extension;
		if(imports!=null)
			System.arraycopy(imports, 0, keys, 2, imports.length);
		Tuple	keytuple	= new Tuple(keys);
		
		ResourceInfo	info	= getResourceInfo(name, extension, imports, classloader);

		//		synchronized(modelcache)
//		{
			cached	= (OAVCapabilityModel)modelcache.get(keytuple);
			if(cached!=null && cached.getLastModified()<info.getLastModified())
				cached	= null;
//		}

		if(cached==null)
		{
			// Lookup cache by resolved filename.
//			synchronized(modelcache)
//			{
				cached	= (OAVCapabilityModel)modelcache.get(info.getFilename());
				if(cached!=null && cached.getLastModified()<info.getLastModified())
					cached	= null;
//			}
			
			// Not found: load from disc and store in cache.
			if(cached==null)
			{
				OAVTypeModel	typemodel	= new OAVTypeModel(name+"_typemodel", classloader);
				// Requires runtime meta model, because e.g. user conditions can refer to runtime elements (belief, goal, etc.) 
				typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
				IOAVState	state	= new OAVState(typemodel);
				
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
				
				
				try
				{
					Report	report	= new Report();
					state.addStateListener(listener, false);
					Object handle = reader.read(info.getInputStream(), state, mapping, report.entries);
					state.removeStateListener(listener);
	
					if(state.getType(handle).isSubtype(OAVBDIMetaModel.agent_type))
					{
						cached	=  new OAVAgentModel(state, handle, typemodel, types, info.getFilename(), info.getLastModified(), report);
					}
					else
					{
						cached	=  new OAVCapabilityModel(state, handle, typemodel, types, info.getFilename(), info.getLastModified(), report);
					}
				}
				finally
				{
					info.cleanup();
				}				
				
				createAgentModelEntry(cached);

				// Store by filename also, to avoid reloading with different imports.
				modelcache.put(info.getFilename(), cached);
			}
			
			// Associate cached model to new key (name/extension/imports).
			modelcache.put(keytuple, cached);
		}

		return cached;
	}
	
	/**
	 *  Rules for agent elements have to be created and added to the generic
	 *  BDI interpreter rules.
	 */
	public void	createAgentModelEntry(OAVCapabilityModel model)	throws IOException
	{
		IRulebase rb = model.getRulebase();
		IOAVState	state	= model.getState();
		Object	magent	= model.getHandle();
		
		// Build user defined goal conditions and add them to the rule base.
		Collection mgoals = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_goals);
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
					ICondition usercond = (ICondition)state.getAttributeValue(create, OAVBDIMetaModel.expression_has_content);
					rb.addRule(GoalLifecycleRules.createGoalCreationUserRule(usercond, gtname));
				}
				
				Object context = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_contextcondition);
				if(context!=null)
				{
					// Two rules have to be added (negated condition for suspend)
					ICondition usercond = (ICondition)state.getAttributeValue(context, OAVBDIMetaModel.expression_has_content);
					rb.addRule(GoalLifecycleRules.createGoalOptionUserRule(usercond, gtname));
					rb.addRule(GoalLifecycleRules.createGoalSuspendUserRule(usercond, gtname));
				}
				
				Object drop = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_dropcondition);
				if(drop!=null)
				{
					ICondition usercond = (ICondition)state.getAttributeValue(drop, OAVBDIMetaModel.expression_has_content);
					rb.addRule(GoalLifecycleRules.createGoalDroppingUserRule(usercond, gtname));
				}
				
				// Create recur condition
				Object recur = state.getAttributeValue(mgoal, OAVBDIMetaModel.goal_has_recurcondition);
				if(recur!=null)
				{
					ICondition usercond = (ICondition)state.getAttributeValue(recur, OAVBDIMetaModel.expression_has_content);
					rb.addRule(GoalProcessingRules.createGoalRecurUserRule(usercond, gtname));
				}
				
				// Create deliberation rules
				Collection inhibits = (Collection)state.getAttributeValues(mgoal, OAVBDIMetaModel.goal_has_inhibits);
				if(inhibits!=null)
				{
					for(Iterator it2=inhibits.iterator(); it2.hasNext(); )
					{
						Object inhibit = it2.next();
						ICondition usercond = (ICondition)state.getAttributeValue(inhibit, OAVBDIMetaModel.expression_has_content);
						if(usercond!=null)
						{
							rb.addRule(GoalDeliberationRules.createAddInhibitionLinkUserRule(usercond, gtname));
							rb.addRule(GoalDeliberationRules.createRemoveInhibitionLinkUserRule(usercond, gtname));
						}
					}
				}
				
				// Create achievegoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.achievegoal_type))
				{
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(target!=null)
					{
						ICondition usercond = (ICondition)state.getAttributeValue(target, OAVBDIMetaModel.expression_has_content);
						rb.addRule(GoalProcessingRules.createAchievegoalSucceededUserRule(usercond, gtname));
					}
				}
				
				// Create maintaingoal specific rules
				
				if(state.getType(mgoal).equals(OAVBDIMetaModel.maintaingoal_type))
				{
					Object maintain = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(maintain!=null)
					{
						ICondition usercond = (ICondition)state.getAttributeValue(maintain, OAVBDIMetaModel.expression_has_content);						
						rb.addRule(GoalProcessingRules.createMaintaingoalProcessingUserRule(usercond, gtname));
					}
					
					Object target = state.getAttributeValue(mgoal, OAVBDIMetaModel.maintaingoal_has_targetcondition);
					target	= target!=null ? target : maintain;
					if(target!=null)
					{
						ICondition usercond = (ICondition)state.getAttributeValue(target, OAVBDIMetaModel.expression_has_content);
						rb.addRule(GoalProcessingRules.createMaintaingoalSucceededUserRule(usercond, gtname));
					}
				}
				
				// Create rules for dynamic parameter values.
				createDynamicParameterValuesConditions(mgoal, state, rb);
				
				// Create rules for dynamic parameter set values.
				createDynamicParameterSetValuesConditions(mgoal, state, rb);
			}
		}
		
		// Build user defined plan conditions and add them to the rule base.
		Collection mplans = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_plans);
		if(mplans!=null)
		{
			for(Iterator it=mplans.iterator(); it.hasNext(); )
			{
				Object mplan = it.next();
				String ptname = (String)state.getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
				
				// Create rules for plans
				
				Object trigger = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_trigger);
				if(trigger!=null)
				{
					Object create = state.getAttributeValue(trigger, OAVBDIMetaModel.plantrigger_has_condition);
					if(create!=null)
					{
						ICondition usercond = (ICondition)state.getAttributeValue(create, OAVBDIMetaModel.expression_has_content);
						rb.addRule(PlanRules.createPlanCreationUserRule(usercond, ptname));
					}
				}
				
				Object context = state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_contextcondition);
				if(context!=null)
				{
					ICondition usercond = (ICondition)state.getAttributeValue(context, OAVBDIMetaModel.expression_has_content);
					rb.addRule(PlanRules.createPlanContextInvalidUserRule(usercond, ptname));
				}
				
				// Create rules for dynamic parameter values.
				createDynamicParameterValuesConditions(mplan, state, rb);
				
				// Create rules for dynamic parameter set values.
				createDynamicParameterSetValuesConditions(mplan, state, rb);
			}
		}
		
		// Build user defined dynamic belief conditions and add them to the rule base.
		Collection mbeliefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefs);
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
						String btname = (String)state.getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name);
						Object usercond = state.getAttributeValue(fact, OAVBDIMetaModel.expression_has_content);
						if(usercond instanceof ICondition)
							rb.addRule(BeliefRules.createDynamicBeliefUserRule((ICondition)usercond, btname));
					}
				}
			}
		}
		
		// Build user defined dynamic belief set conditions and add them to the rule base.
		Collection mbeliefsets = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_beliefsets);
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
						String btname = (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.modelelement_has_name);
						Object usercond = state.getAttributeValue(facts, OAVBDIMetaModel.expression_has_content);
						if(usercond instanceof ICondition)
							rb.addRule(BeliefRules.createDynamicBeliefSetUserRule((ICondition)usercond, btname));
					}
				}
			}
		}
		
		// Build user defined conditions and add them to the rule base.
		Collection mconds = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_conditions);
		if(mconds!=null)
		{
			for(Iterator it=mconds.iterator(); it.hasNext(); )
			{
				Object mcond = it.next();
				String name = (String)state.getAttributeValue(mcond, OAVBDIMetaModel.modelelement_has_name);
				Object usercond = state.getAttributeValue(mcond, OAVBDIMetaModel.expression_has_content);
				if(usercond instanceof ICondition)
					rb.addRule(BeliefRules.createConditionUserRule((ICondition)usercond, mcond, name));
			}
		}

		// Load subcapabilities.
		Collection mcaparefs = state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(mcaparefs!=null)
		{
			for(Iterator it=mcaparefs.iterator(); it.hasNext(); )
			{
				Object mcaparef = it.next();
				String	file	= (String)state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_file);
				OAVCapabilityModel	cmodel	= loadCapabilityModel(file, null);
				model.addSubcapabilityModel(cmodel);

				state.setAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability, cmodel.getHandle());				
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
		}
	}

	/**
	 *  Create conditions for dynamic parameter of an processable model element.
	 *  @param mpe The processable model element.
	 *  @param state The state.
	 *  @param rb The rulebase.
	 */
	protected void createDynamicParameterValuesConditions(Object mpe, IOAVState state, IRulebase rb)
	{
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
						String ptname = (String)state.getAttributeValue(mparam, OAVBDIMetaModel.modelelement_has_name);
						Object usercond = state.getAttributeValue(value, OAVBDIMetaModel.expression_has_content);
						if(usercond instanceof ICondition)
						{
							String mpename = (String)state.getAttributeValue(mpe, OAVBDIMetaModel.modelelement_has_name);
							rb.addRule(BeliefRules.createDynamicParameterUserRule(mpename, (ICondition)usercond, ptname));
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
	protected void createDynamicParameterSetValuesConditions(Object mpe, IOAVState state, IRulebase rb)
	{
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
						String ptname = (String)state.getAttributeValue(mparamset, OAVBDIMetaModel.modelelement_has_name);
						Object usercond = state.getAttributeValue(values, OAVBDIMetaModel.expression_has_content);
						if(usercond instanceof ICondition)
						{
							String rpename = (String)state.getAttributeValue(mpe, OAVBDIMetaModel.modelelement_has_name);
							rb.addRule(BeliefRules.createDynamicParameterSetUserRule(rpename, (ICondition)usercond, ptname));
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
				// Only ignore rule, when type is part of agent meta(!) model.
				OAVObjectType	type	= ((ObjectCondition)conditions.get(i)).getObjectType();
				check	= types.contains(type) || !OAVBDIMetaModel.bdimm_type_model.contains(type);
				if(DEBUG && !check)
					System.out.println("Ignored rule "+rule.getName()+" due to missing objects of type "+type);
			}
		}
		
		if(DEBUG && check)
			System.out.println("Using rule "+rule.getName());
		return check;
	}

	/**
	 *  Create a condition for a goal.
	 *  @param goalname	The name of the goal type.
	 *  @param usercond	The user part of the condition
	 *  @return	The complete goal condition including variables ?rgoal and ?mgoal.
	 */
	protected static ICondition createGoalCondition(String goalname, ICondition usercond)
	{
		return createGoalCondition(goalname, usercond, null);
	}

	/**
	 *  Create a condition for a goal.
	 *  @param goalname	The name of the goal type.
	 *  @param usercond	The user part of the condition.
	 *  @param lifecyclestate	A goal lifecycle state in which the condition should trigger.
	 *  @return	The complete goal condition including variables ?rgoal and ?mgoal.
	 */
	protected static ICondition createGoalCondition(String goalname, ICondition usercond, String lifecyclestate)
	{
		return createGoalCondition(goalname, usercond, lifecyclestate, true);
	}

	/**
	 *  Create a condition for a goal.
	 *  @param goalname	The name of the goal type.
	 *  @param usercond	The user part of the condition.
	 *  @param lifecyclestate	A goal lifecycle state in which the condition should trigger.
	 *  @param islifecyclestate	If true, condition triggers only when goal is in the given lifecyclestate,
	 *  	otherwise triggers when goal is NOT in lifecyclestate.
	 *  @return	The complete goal condition including variables ?rgoal and ?mgoal.
	 */
	protected static ICondition createGoalCondition(String goalname, ICondition usercond, String lifecyclestate, boolean islifecyclestate)
	{
		ObjectCondition	mgoalcon	= new ObjectCondition(OAVBDIMetaModel.goal_type);
		mgoalcon.addConstraint(new BoundConstraint(null, new Variable("?mgoal", OAVBDIMetaModel.goal_type)));
		mgoalcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.modelelement_has_name, goalname));
		ObjectCondition	goalcon	= new ObjectCondition(OAVBDIRuntimeModel.goal_type);
		goalcon.addConstraint(new BoundConstraint(null, new Variable("?rgoal", OAVBDIRuntimeModel.goal_type)));
		goalcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, new Variable("?mgoal", OAVBDIMetaModel.goal_type)));
		if(lifecyclestate!=null)
		{
			goalcon.addConstraint(new LiteralConstraint(OAVBDIRuntimeModel.goal_has_lifecyclestate, lifecyclestate,
				islifecyclestate ? IOperator.EQUAL : IOperator.NOTEQUAL));
		}
		ICondition	goalcond	= new AndCondition(new ICondition[]{mgoalcon, goalcon, usercond});
		return goalcond;
	}

	/**
	 * 
	 */
	public String getFilenameExtension(String filename)
	{
		String ret = null;
		if(filename.endsWith(FILE_EXTENSION_AGENT))
			ret = FILE_EXTENSION_AGENT;
		else if(filename.endsWith(FILE_EXTENSION_CAPABILITY))
			ret = FILE_EXTENSION_CAPABILITY;
		if(ret==null)
			throw new RuntimeException("Unknown extension: "+filename);
		return ret;
	}

	public static void	main(String[] args) throws IOException
	{
		final File	file	= new File("../target/jadex-1.0-SNAPSHOT-dist.dir/lib/jadex-applications-bdi-1.0-SNAPSHOT.jar");
		JarFile	jar	= new JarFile(file);
		ClassLoader	cl	= new URLClassLoader(new URL[]{file.toURI().toURL()}, OAVBDIModelLoader.class.getClassLoader());

		final OAVBDIModelLoader	loader	= new OAVBDIModelLoader(Collections.EMPTY_MAP);
		loader.setClassLoader(cl);
		final SAXParserFactory	factory	= SAXParserFactory.newInstance();
		factory.setNamespaceAware(false);
		ThreadPool	tp	= new ThreadPool();
		Enumeration	entries	= jar.entries();
		while(entries.hasMoreElements())
		{
			final Object	entry	= entries.nextElement();
			tp.execute(new Runnable()
			{
				public void run()
				{
					final String	filename	= "jar:file:/"+file.getAbsolutePath()
						+"!/"+((JarEntry) entry).getName();
					if(filename.endsWith(FILE_EXTENSION_AGENT) || filename.endsWith(FILE_EXTENSION_CAPABILITY))
					{
//						ResourceInfo	rinfo	= null;
						try
						{
							loader.loadModel(filename, null, null);

//							rinfo = getResourceInfo(filename, loader.getFilenameExtension(filename), null, null);
//							OAVTypeModel	typemodel	= new OAVTypeModel(filename+"_typemodel", null);
//							typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
//							loader.reader.read(rinfo.getInputStream(), new OAVState(typemodel), loader.mapping);

//							XMLReader	xmlreader	= factory.newSAXParser().getXMLReader();
//							xmlreader.setContentHandler(new DefaultHandler()
//							{
//								public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
//								{
//									if(Math.random()>0.9)
//										throw new SAXException("kaputt");
//								}
//							});
//							xmlreader.parse(new InputSource(rinfo.getInputStream()));

//							InputStream	is	= rinfo.getInputStream();
//							while(is.read()!=-1);
						}
						catch(NullPointerException e)
						{
							System.err.println("Error loading: "+filename);
							e.printStackTrace();
						}
						catch(Exception e)
						{
							System.err.println("Error loading: "+filename+", "+e.getClass());
						}
						
//						if(rinfo!=null)
//							rinfo.cleanup();
					}
				}
			});
		}
	}
}
