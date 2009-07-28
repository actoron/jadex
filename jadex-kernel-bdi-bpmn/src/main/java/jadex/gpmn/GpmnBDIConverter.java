package jadex.gpmn;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.OAVBDIXMLReader;
import jadex.bdi.interpreter.Report;
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
import jadex.gpmn.runtime.plan.StartAndMonitorProcessPlan;
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
		Object handle = doConvert(process, classloader, state);
		state.removeStateListener(listener);
		// todo: filename, last modified
		agentmodel =  new OAVAgentModel(state, handle, typemodel, types, null, 0, report);
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
	public Object doConvert(MProcess proc, ClassLoader classloader, IOAVState state)
	{
		Object agenthandle = state.createRootObject(OAVBDIMetaModel.agent_type);
		
		// Handle package and imports
		// todo:
		
		// Create default configuration
		Object confighandle = state.createObject(OAVBDIMetaModel.configuration_type);
		state.setAttributeValue(confighandle, OAVBDIMetaModel.modelelement_has_name, "default");
		state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_configurations, confighandle);
		
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
								Object belhandle = state.createObject(OAVBDIMetaModel.belief_type);
								state.setAttributeValue(belhandle, OAVBDIMetaModel.modelelement_has_name, param.getName());
								state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_beliefs, belhandle);
								state.setAttributeValue(belhandle, OAVBDIMetaModel.typedelement_has_classname, param.getClassName());
								if(param.getInitialValueDescription()!=null)
								{
									Object facthandle = state.createObject(OAVBDIMetaModel.expression_type);
									state.setAttributeValue(facthandle, OAVBDIMetaModel.expression_has_content, param.getInitialValueDescription());
									state.setAttributeValue(belhandle, OAVBDIMetaModel.belief_has_fact, facthandle);
								}
							}
							else
							{
								Object belsethandle = state.createObject(OAVBDIMetaModel.beliefset_type);
								state.setAttributeValue(belsethandle, OAVBDIMetaModel.modelelement_has_name, param.getName());
								state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_beliefsets, belsethandle);
								state.setAttributeValue(belsethandle, OAVBDIMetaModel.typedelement_has_classname, param.getClassName());
								if(param.getInitialValueDescription()!=null)
								{
									Object factshandle = state.createObject(OAVBDIMetaModel.expression_type);
									state.setAttributeValue(factshandle, OAVBDIMetaModel.expression_has_content, param.getInitialValueDescription());
									state.setAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_factsexpression, factshandle);
								}
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
				Object goalhandle = null;
				
				if(goal instanceof MAchieveGoal)
				{
					goalhandle = state.createObject(OAVBDIMetaModel.achievegoal_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name, goal.getName());
					state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_goals, goalhandle);
					if(((MAchieveGoal)goal).getExcludeMode()!=null)
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_exclude, ((MAchieveGoal)goal).getExcludeMode());
					if(((MAchieveGoal)goal).getTargetCondition()!=null)
					{
						Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition, condhandle);
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MAchieveGoal)goal).getTargetCondition());
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
					}
				}	
				else if(goal instanceof MMaintainGoal)
				{
					goalhandle = state.createObject(OAVBDIMetaModel.maintaingoal_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name, goal.getName());
					state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_goals, goalhandle);
					if(((MMaintainGoal)goal).getExcludeMode()!=null)
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_exclude, ((MMaintainGoal)goal).getExcludeMode());
					if(((MMaintainGoal)goal).getMaintainCondition()!=null)
					{
						Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition, condhandle);
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MMaintainGoal)goal).getMaintainCondition());
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
					}
				}
				
				if(goal.getCreationCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getCreationCondition());
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
				}
				if(goal.getContextCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getContextCondition());
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
				}
				if(goal.getDropCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getDropCondition());
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
						Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
						state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, "implicit_"+goal.getName());
						state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_plans, planhandle);
						Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
						state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
						state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, "jadex.gpmn.runtime.plan.GoalHierarchyExecutionPlan.class");
//						state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_classname, "jadex.gpmn.runtime.plan.GoalHierarchyExecutionPlan");
//						state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_type, "bpmn");
						
						// Create trigger
						Object triggerhandle = state.createObject(OAVBDIMetaModel.plantrigger_type);
						state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_trigger, triggerhandle);
						Object triggerrefhandle = state.createObject(OAVBDIMetaModel.triggerreference_type);
						state.setAttributeValue(triggerrefhandle, OAVBDIMetaModel.triggerreference_has_ref, goal.getName());
						state.addAttributeValue(triggerhandle, OAVBDIMetaModel.plantrigger_has_goals, triggerrefhandle);
				
						// Create mode paramter
						Object paramhandle = state.createObject(OAVBDIMetaModel.planparameter_type);
						state.setAttributeValue(paramhandle, OAVBDIMetaModel.modelelement_has_name, "mode");
						state.addAttributeValue(planhandle, OAVBDIMetaModel.parameterelement_has_parameters, paramhandle);
						state.setAttributeValue(paramhandle, OAVBDIMetaModel.typedelement_has_class, String.class);
						Object valhandle = state.createObject(OAVBDIMetaModel.expression_type);
						state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_content, goal.getName().endsWith("par")? "\"parallel\"": "\"sequential\"");
						state.setAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value, valhandle);
					
						// Create subgoals paramterset
						Object paramsethandle = state.createObject(OAVBDIMetaModel.planparameterset_type);
						state.setAttributeValue(paramsethandle, OAVBDIMetaModel.modelelement_has_name, "subgoals");
						state.addAttributeValue(planhandle, OAVBDIMetaModel.parameterelement_has_parametersets, paramsethandle);
						
						StringBuffer goalnames = new StringBuffer("new String[]{");
						for(int j=0; j<outgoals.size(); j++)
						{
							if(j!=0)
								goalnames.append(", ");
							goalnames.append("\"").append(((MGoal)outgoals.get(j)).getName()).append("\"");
						}	
						goalnames.append("}");
						
						state.setAttributeValue(paramsethandle, OAVBDIMetaModel.typedelement_has_class, String.class);
						Object valshandle = state.createObject(OAVBDIMetaModel.expression_type);
						
						state.setAttributeValue(valshandle, OAVBDIMetaModel.expression_has_content, goalnames.toString());
						state.setAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression, valshandle);
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
				
				Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
				state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, plan.getName());
				state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_plans, planhandle);
				Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
				state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
				state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, plan.getBpmnPlan());
				state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_type, "bpmn");
			
				List inedges = plan.getIncomingSequenceEdges();
				if(inedges!=null)
				{
					Object triggerhandle = state.createObject(OAVBDIMetaModel.plantrigger_type);
					state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_trigger, triggerhandle);
					
					for(int j=0; j<inedges.size(); j++)
					{
						MSequenceEdge inedge = (MSequenceEdge)inedges.get(j);
						MGoal goal = (MGoal)inedge.getSource();
						Object triggerrefhandle = state.createObject(OAVBDIMetaModel.triggerreference_type);
						state.setAttributeValue(triggerrefhandle, OAVBDIMetaModel.triggerreference_has_ref, goal.getName());
						state.addAttributeValue(triggerhandle, OAVBDIMetaModel.plantrigger_has_goals, triggerrefhandle);
					}
				}
				
				// todo: parameters
			}
		}
		
		// Create plan for starting/monitoring the process.
		String planname = "startandmonitor_"+proc.getName().substring(0, proc.getName().indexOf("."));
		Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, planname);
		state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_plans, planhandle);
		Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
		state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, "jadex.gpmn.runtime.plan.StartAndMonitorProcessPlan.class");
		
		// Create achieve_goals maintain_goals paramterset
		Object aparamsethandle = state.createObject(OAVBDIMetaModel.planparameterset_type);
		state.setAttributeValue(aparamsethandle, OAVBDIMetaModel.modelelement_has_name, "achieve_goals");
		state.setAttributeValue(aparamsethandle, OAVBDIMetaModel.typedelement_has_class, String.class);
		state.addAttributeValue(planhandle, OAVBDIMetaModel.parameterelement_has_parametersets, aparamsethandle);
		Object mparamsethandle = state.createObject(OAVBDIMetaModel.planparameterset_type);
		state.setAttributeValue(mparamsethandle, OAVBDIMetaModel.modelelement_has_name, "maintain_goals");
		state.setAttributeValue(mparamsethandle, OAVBDIMetaModel.typedelement_has_class, String.class);
		state.addAttributeValue(planhandle, OAVBDIMetaModel.parameterelement_has_parametersets, mparamsethandle);
	
		Collection goalhandles = state.getAttributeValues(agenthandle, OAVBDIMetaModel.capability_has_goals);
		if(goals!=null)
		{
			for(int i=0; i<goals.size(); i++)
			{
				MGoal goal = (MGoal)goals.get(i);
				if(goal.getIncomingSequenceEdges()==null)
				{
					Object goalhandle = state.getAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_goals, goal.getName());
					Object valhandle = state.createObject(OAVBDIMetaModel.expression_type);
					String goalname = (String)state.getAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name);
					state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_content, "\""+goalname+"\"");
	
					if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
					{
						state.addAttributeValue(aparamsethandle, OAVBDIMetaModel.parameterset_has_values, valhandle);
					}
					else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
					{
						state.addAttributeValue(mparamsethandle, OAVBDIMetaModel.parameterset_has_values, valhandle);
					}
				}
			}
		}
		
		// Make this plan the initial plan
		Object iniplanhandle = state.createObject(OAVBDIMetaModel.configelement_type);
		state.setAttributeValue(iniplanhandle, OAVBDIMetaModel.configelement_has_ref, planname);
		state.addAttributeValue(confighandle, OAVBDIMetaModel.configuration_has_initialplans, iniplanhandle);

		
		// Do second pass post-processing
		
		
		OAVBDIXMLReader.ExpressionProcessor expost = new OAVBDIXMLReader.ExpressionProcessor();
		OAVBDIXMLReader.ClassPostProcessor clpost = new OAVBDIXMLReader.ClassPostProcessor(OAVBDIMetaModel.typedelement_has_classname, OAVBDIMetaModel.typedelement_has_class);
		
		// Handle beliefs
		Collection beliefhandles = state.getAttributeValues(agenthandle, OAVBDIMetaModel.capability_has_beliefs);
		if(beliefhandles!=null)
		{
			for(Iterator it = beliefhandles.iterator(); it.hasNext(); )
			{
				Object belhandle = it.next();
				Object exphandle = state.getAttributeValue(belhandle, OAVBDIMetaModel.belief_has_fact);
				clpost.postProcess(state, belhandle, agenthandle, classloader);
				if(exphandle!=null)
					expost.postProcess(state, exphandle, agenthandle, classloader);
			}
		}
		Collection beliefsethandles = state.getAttributeValues(agenthandle, OAVBDIMetaModel.capability_has_beliefsets);
		if(beliefsethandles!=null)
		{
			for(Iterator it = beliefsethandles.iterator(); it.hasNext(); )
			{
				Object belsethandle = it.next();
				Object exphandle = state.getAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_factsexpression);
				clpost.postProcess(state, belsethandle, agenthandle, classloader);
				if(exphandle!=null)
					expost.postProcess(state, exphandle, agenthandle, classloader);
				Collection expshandle = state.getAttributeValues(belsethandle, OAVBDIMetaModel.beliefset_has_facts);
				if(expshandle!=null)
				{
					for(Iterator it2=expshandle.iterator(); it2.hasNext(); )
					{
						exphandle = it2.next();
						expost.postProcess(state, exphandle, agenthandle, classloader);
					}
				}
			}
		}
		
		// Handle goal conditions
		if(goalhandles!=null)
		{
			for(Iterator it = goalhandles.iterator(); it.hasNext(); )
			{
				Object goalhandle = it.next();
				Object condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, agenthandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, agenthandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition);
				if(condhandle!=null)
					expost.postProcess(state, condhandle, agenthandle, classloader);

				if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(condhandle!=null)
						expost.postProcess(state, condhandle, agenthandle, classloader);
				}
				else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(condhandle!=null)
						expost.postProcess(state, condhandle, agenthandle, classloader);
				}
				
				Collection paramhandles = state.getAttributeValues(goalhandle, OAVBDIMetaModel.parameterelement_has_parameters);
				if(paramhandles!=null)
				{
					for(Iterator it2 = paramhandles.iterator(); it2.hasNext(); )
					{
						Object paramhandle = it2.next();
						Object exphandle = state.getAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value);
						if(exphandle!=null)
							expost.postProcess(state, exphandle, agenthandle, classloader);
					}
				}
				Collection paramsethandles = state.getAttributeValues(goalhandle, OAVBDIMetaModel.parameterelement_has_parametersets);
				if(paramsethandles!=null)
				{
					for(Iterator it2 = paramsethandles.iterator(); it2.hasNext(); )
					{
						Object paramsethandle = it2.next();
						Object exphandle = state.getAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression);
						if(exphandle!=null)
							expost.postProcess(state, exphandle, agenthandle, classloader);
						Collection expshandle = state.getAttributeValues(paramsethandle, OAVBDIMetaModel.parameterset_has_values);
						if(expshandle!=null)
						{
							for(Iterator it3=expshandle.iterator(); it3.hasNext(); )
							{
								exphandle = it3.next();
								expost.postProcess(state, exphandle, agenthandle, classloader);
							}
						}
					}
				}
			}
		}
		
		// Handle plan parameters
		Collection planhandles = state.getAttributeValues(agenthandle, OAVBDIMetaModel.capability_has_plans);
		if(planhandles!=null)
		{
			for(Iterator it = planhandles.iterator(); it.hasNext(); )
			{
				planhandle = it.next();
				Collection paramhandles = state.getAttributeValues(planhandle, OAVBDIMetaModel.parameterelement_has_parameters);
				if(paramhandles!=null)
				{
					for(Iterator it2 = paramhandles.iterator(); it2.hasNext(); )
					{
						Object paramhandle = it2.next();
						Object exphandle = state.getAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value);
						if(exphandle!=null)
							expost.postProcess(state, exphandle, agenthandle, classloader);
					}
				}
				Collection paramsethandles = state.getAttributeValues(planhandle, OAVBDIMetaModel.parameterelement_has_parametersets);
				if(paramsethandles!=null)
				{
					for(Iterator it2 = paramsethandles.iterator(); it2.hasNext(); )
					{
						Object paramsethandle = it2.next();
						Object exphandle = state.getAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression);
						if(exphandle!=null)
							expost.postProcess(state, exphandle, agenthandle, classloader);
						Collection expshandle = state.getAttributeValues(paramsethandle, OAVBDIMetaModel.parameterset_has_values);
						if(expshandle!=null)
						{
							for(Iterator it3=expshandle.iterator(); it3.hasNext(); )
							{
								exphandle = it3.next();
								expost.postProcess(state, exphandle, agenthandle, classloader);
							}
						}
					}
				}
			}
		}
		
		return agenthandle;
	}
	
	/**
	 * 
	 * /
	protected Object createPlan(Object scopehandle, IOAVState state, String name)
	{
		Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_plans, planhandle);
		Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
		state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, plan.getBpmnPlan());
		state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_type, "bpmn");
	
	}*/
}
