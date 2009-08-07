package jadex.gpmn;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.OAVBDIXMLReader;
import jadex.bdi.interpreter.Report;
import jadex.commons.xml.IPostProcessor;
import jadex.gpmn.model.MAchieveGoal;
import jadex.gpmn.model.MArtifact;
import jadex.gpmn.model.MContext;
import jadex.gpmn.model.MGoal;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MMaintainGoal;
import jadex.gpmn.model.MParameter;
import jadex.gpmn.model.MPlan;
import jadex.gpmn.model.MProcess;
import jadex.gpmn.model.MProcessElement;
import jadex.gpmn.model.MSequenceEdge;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  Class for converting a gpmn model description to an agent description.
 */
public class GpmnBDIConverter
{
	//-------- attributes --------
	
	/** The loader. */
	protected OAVBDIModelLoader loader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new converter.
	 */
	public GpmnBDIConverter(OAVBDIModelLoader loader)
	{
		this.loader = loader;
	}
	
	//-------- methods --------
	
	/**
	 *  Convert a gpmn model to a bdi agent.
	 */
	public OAVAgentModel[] convertGpmnModelToBDIAgents(MGpmnModel model, ClassLoader classloader)
	{
		OAVAgentModel[] ret = null;
		
		// todo: more than one process?!
		List procs = model.getProcesses();
		if(procs!=null)
		{
			ret	= new OAVAgentModel[procs.size()];
			for(int i=0; i<procs.size(); i++)
			{
				MProcess proc = (MProcess)procs.get(i);
				
				ret[i] = createBDIAgentForProcess(model, proc, classloader);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a bdi agent for one process.
	 */
	public OAVAgentModel createBDIAgentForProcess(MGpmnModel model, MProcess process, ClassLoader classloader)
	{
		OAVAgentModel agentmodel = null;
		
		OAVTypeModel typemodel = new OAVTypeModel(model.getName()+"_typemodel", classloader);
		// Requires runtime meta model, because e.g. user conditions can refer to runtime elements (belief, goal, etc.) 
		typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		IOAVState state	= OAVStateFactory.createOAVState(typemodel);
		
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
		
		
		Report	report	= new Report();
		state.addStateListener(listener, false);
		
		Object handle = state.createRootObject(OAVBDIMetaModel.agent_type); 
		doConvert(process, classloader, state, handle);
		state.setAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name, model.getName());
		state.setAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description, model.getDescription());
		state.setAttributeValue(handle, OAVBDIMetaModel.capability_has_package, model.getPackage());
		String[] imports = model.getImports();
		if(imports!=null)
		{
			for(int i=0; i<imports.length; i++)
			{
				state.addAttributeValue(handle, OAVBDIMetaModel.capability_has_imports, imports[i]);
			}
		}
		
		state.removeStateListener(listener);
		agentmodel =  new OAVAgentModel(state, handle, typemodel, types, model.getFilename(), model.getLastModified(), report);
		try
		{
			loader.createAgentModelEntry(agentmodel, report);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return agentmodel;
	}
	
	/**
	 *  Convert all aspects of a process.
	 */
	public void doConvert(MProcess proc, ClassLoader classloader, IOAVState state, Object scopehandle)
	{		
		// Handle package and imports here?!
		// todo:
		
		// Create default configuration
		Object confighandle = state.createObject(OAVBDIMetaModel.configuration_type);
		state.setAttributeValue(confighandle, OAVBDIMetaModel.modelelement_has_name, "default");
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_configurations, confighandle);
		
		// Handle beliefs
		List artifacts = proc.getArtifacts();
		if(artifacts!=null)
		{
			for(int i=0; i<artifacts.size(); i++)
			{
				MArtifact art = (MArtifact)artifacts.get(i);
				if(art instanceof MContext)
				{
					MContext cont = (MContext)art;
					List params = cont.getParameters();
					if(params!=null)
					{
						for(int j=0; j<params.size(); j++)
						{
							MParameter param = (MParameter)params.get(j);
							
							if(!param.isSet())
							{
								createBelief(state, scopehandle, param.getName(), param.getClassName(), param.getInitialValueDescription());
							}
							else
							{
								createBeliefSet(state, scopehandle, param.getName(), param.getClassName(), null, param.getInitialValueDescription());
							}
						}
					}
				}
			}
		}
		
		// Handle goals
		List goals = proc.getGoals();
		if(goals!=null)
		{
			for(int i=0; i<goals.size(); i++)
			{
				MGoal goal = (MGoal)goals.get(i);
				OAVObjectType goaltype = goal instanceof MAchieveGoal? OAVBDIMetaModel.achievegoal_type: 
					goal instanceof MMaintainGoal? OAVBDIMetaModel.maintaingoal_type: null;
				Object goalhandle = createGoal(state, scopehandle, goal.getName(), goaltype, goal.getRetry(), 
					goal.getRetryDelay(), goal.getRecur(), goal.getRecurDelay(), goal.getExcludeMode(), 
					goal.getRetry(), goal.getUnique(), goal.getCreationCondition(), goal.getContextCondition(), 
					goal.getDropCondition());
				
				if(goal instanceof MAchieveGoal && ((MAchieveGoal)goal).getTargetCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MAchieveGoal)goal).getTargetCondition());
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
				}	
				else if(goal instanceof MMaintainGoal && ((MMaintainGoal)goal).getMaintainCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MMaintainGoal)goal).getMaintainCondition());
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
				}
				
				// Create initial goal for goal with no incoming edges
//				if(goal.getIncomingSequenceEdges()==null)
//				{
//					Object inigoalhandle = state.createObject(OAVBDIMetaModel.configelement_type);
//					state.setAttributeValue(inigoalhandle, OAVBDIMetaModel.configelement_has_ref, goal.getName());
//					state.addAttributeValue(confighandle, OAVBDIMetaModel.configuration_has_initialgoals, inigoalhandle);
//				}
				
				// Create implicit plans for creating/dispatching subgoals
				List outedges = goal.getOutgoingSequenceEdges();
				if(outedges!=null)
				{
					// todo: parameters
					
					List outgoals = new ArrayList();
					for(int j=0; j<outedges.size(); j++)
					{
						MSequenceEdge outedge = (MSequenceEdge)outedges.get(j);
						MProcessElement elem = outedge.getTarget();
						if(elem instanceof MGoal)
						{
							outgoals.add(elem);
						}
					}
					
					if(outgoals.size()>0)
					{
						// Create plan with body and name
						Object planhandle = goal.getName().endsWith("par")
							? createPlan(scopehandle, state, "implicit_"+goal.getName(), "jadex.gpmn.runtime.plan.ParallelGoalExecutionPlan", "bpmn")
							: createPlan(scopehandle, state, "implicit_"+goal.getName(), "jadex.gpmn.runtime.plan.SequentialGoalExecutionPlan", "bpmn");
						
						// Create trigger
						createPlanTrigger(planhandle, state, new String[]{goal.getName()}, null, null);
				
						// Create subgoals parameter set
						String[] goalnames = new String[outgoals.size()];
						for(int j=0; j<outgoals.size(); j++)
							goalnames[j] = "\""+((MGoal)outgoals.get(j)).getName()+"\"";
						createParameterSet(planhandle, state, "subgoals", "String", goalnames, null, true);
					}
				}
			}
		}
		
		// Handle plans
		List plans = proc.getPlans();
		if(plans!=null)
		{
			for(int i=0; i<plans.size(); i++)
			{
				MPlan plan = (MPlan)plans.get(i);
				
				Object planhandle = createPlan(scopehandle, state, plan.getName(), plan.getBpmnPlan(), "bpmn");
			
				List inedges = plan.getIncomingSequenceEdges();
				if(inedges!=null)
				{
					String[] goalnames = new String[inedges.size()]; 
					for(int j=0; j<inedges.size(); j++)
					{
						MSequenceEdge inedge = (MSequenceEdge)inedges.get(j);
						MGoal goal = (MGoal)inedge.getSource();
						goalnames[j] = goal.getName();
					}
					
					createPlanTrigger(planhandle, state, goalnames, null, null);
				}
				
				// todo: parameters
			}
		}
		
		// Create plan for starting/monitoring the process.
		String planname = "startandmonitor_"+proc.getName().substring(0, proc.getName().indexOf("."));
		Object planhandle = createPlan(scopehandle, state, planname, "jadex.gpmn.runtime.plan.StartAndMonitorProcessPlan", null);
		
		// Create achieve_goals maintain_goals paramterset
		List agoalnames = new ArrayList();
		List mgoalnames = new ArrayList();
		Collection goalhandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_goals);
		if(goals!=null)
		{
			for(int i=0; i<goals.size(); i++)
			{
				MGoal goal = (MGoal)goals.get(i);
				if(goal.getIncomingSequenceEdges()==null)
				{
					Object goalhandle = state.getAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_goals, goal.getName());
					String goalname = (String)state.getAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name);
	
					if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
					{
						agoalnames.add("\""+goalname+"\"");
					}
					else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
					{
						mgoalnames.add("\""+goalname+"\"");
					}
				}
			}
		}
		createParameterSet(planhandle, state, "achieve_goals", "String", 
			agoalnames.size()==0? null: (String[])agoalnames.toArray(new String[agoalnames.size()]), null, true);
		createParameterSet(planhandle, state, "maintain_goals", "String", 
			mgoalnames.size()==0? null: (String[])mgoalnames.toArray(new String[mgoalnames.size()]), null, true);
		
		// Make this plan the initial plan
		Object iniplanhandle = state.createObject(OAVBDIMetaModel.configelement_type);
		state.setAttributeValue(iniplanhandle, OAVBDIMetaModel.configelement_has_ref, planname);
		state.addAttributeValue(confighandle, OAVBDIMetaModel.configuration_has_initialplans, iniplanhandle);

		
		// Do second pass post-processing
		
		
		OAVBDIXMLReader.ExpressionProcessor expost = new OAVBDIXMLReader.ExpressionProcessor();
		OAVBDIXMLReader.ClassPostProcessor clpost = new OAVBDIXMLReader.ClassPostProcessor(OAVBDIMetaModel.typedelement_has_classname, OAVBDIMetaModel.typedelement_has_class);
		
		// Handle beliefs
		Collection beliefhandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_beliefs);
		if(beliefhandles!=null)
		{
			for(Iterator it = beliefhandles.iterator(); it.hasNext(); )
			{
				Object belhandle = it.next();
				Object exphandle = state.getAttributeValue(belhandle, OAVBDIMetaModel.belief_has_fact);
				clpost.postProcess(state, belhandle, scopehandle, classloader);
				if(exphandle!=null)
					expost.postProcess(state, exphandle, scopehandle, classloader);
			}
		}
		Collection beliefsethandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_beliefsets);
		if(beliefsethandles!=null)
		{
			for(Iterator it = beliefsethandles.iterator(); it.hasNext(); )
			{
				Object belsethandle = it.next();
				Object exphandle = state.getAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_factsexpression);
				clpost.postProcess(state, belsethandle, scopehandle, classloader);
				if(exphandle!=null)
					expost.postProcess(state, exphandle, scopehandle, classloader);
				Collection expshandle = state.getAttributeValues(belsethandle, OAVBDIMetaModel.beliefset_has_facts);
				if(expshandle!=null)
				{
					for(Iterator it2=expshandle.iterator(); it2.hasNext(); )
					{
						exphandle = it2.next();
						expost.postProcess(state, exphandle, scopehandle, classloader);
					}
				}
			}
		}
		
		// Handle goals
		if(goalhandles!=null)
		{
			for(Iterator it = goalhandles.iterator(); it.hasNext(); )
			{
				Object goalhandle = it.next();
				postProcessParameterElement(state, scopehandle, goalhandle, expost, classloader);

				Object condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, scopehandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, scopehandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, scopehandle, classloader);

				if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(condhandle!=null)
						expost.postProcess(state, condhandle, scopehandle, classloader);
				}
				else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(condhandle!=null)
						expost.postProcess(state, condhandle, scopehandle, classloader);
				}				
			}
		}
		
		// Handle plans
		Collection planhandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_plans);
		if(planhandles!=null)
		{
			for(Iterator it = planhandles.iterator(); it.hasNext(); )
			{
				planhandle = it.next();
				postProcessParameterElement(state, scopehandle, planhandle, expost, classloader);
			}
		}
	}
	
	//-------- helper creation methods --------
	
	/**
	 *  Create a configuration.
	 */
	protected Object createConfiguration(IOAVState state, Object scopehandle, String name)
	{
		Object confighandle = state.createObject(OAVBDIMetaModel.configuration_type);
		state.setAttributeValue(confighandle, OAVBDIMetaModel.modelelement_has_name, "default");
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_configurations, confighandle);
		return confighandle;
	}
	
	/**
	 *  Create a belief.
	 */
	protected Object createBelief(IOAVState state, Object scopehandle, String name, String classname, String inival)
	{
		Object belhandle = state.createObject(OAVBDIMetaModel.belief_type);
		state.setAttributeValue(belhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_beliefs, belhandle);
		state.setAttributeValue(belhandle, OAVBDIMetaModel.typedelement_has_classname, classname);
		if(inival!=null)
		{
			Object facthandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(facthandle, OAVBDIMetaModel.expression_has_content, inival);
			state.setAttributeValue(belhandle, OAVBDIMetaModel.belief_has_fact, facthandle);
		}
		return belhandle;
	}
	
	/**
	 *  Create a belief set.
	 */
	protected Object createBeliefSet(IOAVState state, Object scopehandle, String name, String classname, String[] values, String valuesexp)
	{
		Object belsethandle = state.createObject(OAVBDIMetaModel.beliefset_type);
		state.setAttributeValue(belsethandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_beliefsets, belsethandle);
		state.setAttributeValue(belsethandle, OAVBDIMetaModel.typedelement_has_classname, classname);
		
		if(values!=null)
		{
			for(int i=0; i<values.length; i++)
			{
				Object valhandle = state.createObject(OAVBDIMetaModel.expression_type);
				state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_content, values[i]);
				state.addAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_facts, valhandle);
			}
		}
		else if(valuesexp!=null)
		{
			Object valshandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(valshandle, OAVBDIMetaModel.expression_has_content, valuesexp);
			state.setAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_factsexpression, valshandle);
		}
		
		return belsethandle;
	}
	
	/**
	 *  Create a goal.
	 */
	protected Object createGoal(IOAVState state, Object scopehandle, String name, OAVObjectType goaltype, 
		Boolean retry, Long retrydelay, Boolean recur, Long recurdelay, String exclude, Boolean rebuild, Boolean unique,
		String creationcond, String contextcond, String dropcond)
	{
		Object goalhandle = state.createObject(goaltype);
		state.setAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_goals, goalhandle);
		
		if(retry!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_retry, retry);
		if(retrydelay!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_retrydelay, retrydelay);
		if(recur!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_recur, recur);
		if(recurdelay!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_recurdelay, recurdelay);
		if(exclude!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_exclude, exclude);
		if(rebuild!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_rebuild, rebuild);
		if(unique!=null)
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_unique, unique);
	
		if(creationcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, creationcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		if(contextcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, contextcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		if(dropcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, dropcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		
		return goalhandle;
	}
	
	/**
	 *  Create a plan.
	 */
	protected Object createPlan(Object scopehandle, IOAVState state, String name, String impl, String bodytype)
	{
		Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_plans, planhandle);
		Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
		state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, impl);
		if(bodytype!=null)
			state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_type, bodytype);
		return planhandle;
	}
	
	/**
	 *  Create a parameter.
	 */
	protected Object createParameter(Object paramelemhandle, IOAVState state, String name, String classname, String value, boolean planparam)
	{
		Object paramhandle = planparam? state.createObject(OAVBDIMetaModel.planparameter_type): 
			state.createObject(OAVBDIMetaModel.parameter_type); 
		state.setAttributeValue(paramhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(paramelemhandle, OAVBDIMetaModel.parameterelement_has_parameters, paramhandle);
		state.setAttributeValue(paramhandle, OAVBDIMetaModel.typedelement_has_classname, classname);
		Object valhandle = state.createObject(OAVBDIMetaModel.expression_type);
		state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_content, value);
		state.setAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value, valhandle);
		return paramhandle;
	}
	
	/**
	 *  Create a parameter set.
	 */
	protected Object createParameterSet(Object paramelemhandle, IOAVState state, String name, String classname, String[] values, String valuesexp, boolean planparamset)
	{
		Object paramsethandle = planparamset? state.createObject(OAVBDIMetaModel.planparameterset_type): 
			state.createObject(OAVBDIMetaModel.parameterset_type); 
		state.setAttributeValue(paramsethandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(paramelemhandle, OAVBDIMetaModel.parameterelement_has_parametersets, paramsethandle);
		state.setAttributeValue(paramsethandle, OAVBDIMetaModel.typedelement_has_classname, classname);

		if(values!=null)
		{
			for(int i=0; i<values.length; i++)
			{
				Object valhandle = state.createObject(OAVBDIMetaModel.expression_type);
				state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_content, values[i]);
				state.addAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_values, valhandle);
			}
		}
		else if(valuesexp!=null)
		{
			Object valshandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(valshandle, OAVBDIMetaModel.expression_has_content, valuesexp);
			state.setAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression, valshandle);
		}
		
		return paramsethandle;
	}
	
	/**
	 *  Create a plan trigger.
	 */
	protected Object createPlanTrigger(Object planhandle, IOAVState state, String[] goals, String[] ievents, String[] mevents)
	{
		Object triggerhandle = state.createObject(OAVBDIMetaModel.plantrigger_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_trigger, triggerhandle);
		
		if(goals!=null)
		{
			for(int i=0; i<goals.length; i++)
			{
				Object triggerrefhandle = state.createObject(OAVBDIMetaModel.triggerreference_type);
				state.setAttributeValue(triggerrefhandle, OAVBDIMetaModel.triggerreference_has_ref, goals[i]);
				state.addAttributeValue(triggerhandle, OAVBDIMetaModel.plantrigger_has_goals, triggerrefhandle);
			}
		}
		
		if(ievents!=null)
		{
			for(int i=0; i<ievents.length; i++)
			{
				Object triggerrefhandle = state.createObject(OAVBDIMetaModel.triggerreference_type);
				state.setAttributeValue(triggerrefhandle, OAVBDIMetaModel.triggerreference_has_ref, ievents[i]);
				state.addAttributeValue(triggerhandle, OAVBDIMetaModel.trigger_has_internalevents, triggerrefhandle);
			}
		}
		
		if(mevents!=null)
		{
			for(int i=0; i<mevents.length; i++)
			{
				Object triggerrefhandle = state.createObject(OAVBDIMetaModel.triggerreference_type);
				state.setAttributeValue(triggerrefhandle, OAVBDIMetaModel.triggerreference_has_ref, mevents[i]);
				state.addAttributeValue(triggerhandle, OAVBDIMetaModel.trigger_has_messageevents, triggerrefhandle);
			}
		}
		
		return triggerhandle;
	}
	
	//-------- other helper methods --------
	
	/**
	 *  Post process a parameter element.
	 */
	protected void postProcessParameterElement(IOAVState state, Object scopehandle, Object paramelem, 
		IPostProcessor postproc, ClassLoader classloader)
	{
		Collection paramhandles = state.getAttributeValues(paramelem, OAVBDIMetaModel.parameterelement_has_parameters);
		if(paramhandles!=null)
		{
			for(Iterator it2 = paramhandles.iterator(); it2.hasNext(); )
			{
				Object paramhandle = it2.next();
				Object exphandle = state.getAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value);
				if(exphandle!=null)
					postproc.postProcess(state, exphandle, scopehandle, classloader);
			}
		}
		Collection paramsethandles = state.getAttributeValues(paramelem, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(paramsethandles!=null)
		{
			for(Iterator it2 = paramsethandles.iterator(); it2.hasNext(); )
			{
				Object paramsethandle = it2.next();
				Object exphandle = state.getAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression);
				if(exphandle!=null)
					postproc.postProcess(state, exphandle, scopehandle, classloader);
				Collection expshandle = state.getAttributeValues(paramsethandle, OAVBDIMetaModel.parameterset_has_values);
				if(expshandle!=null)
				{
					for(Iterator it3=expshandle.iterator(); it3.hasNext(); )
					{
						exphandle = it3.next();
						postproc.postProcess(state, exphandle, scopehandle, classloader);
					}
				}
			}
		}
	}
}
