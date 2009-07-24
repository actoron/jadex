package jadex.gpmn;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.OAVBDIXMLReader;
import jadex.bdi.interpreter.Report;
import jadex.gpmn.model.MAchieveGoal;
import jadex.gpmn.model.MGoal;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MMaintainGoal;
import jadex.gpmn.model.MPlan;
import jadex.gpmn.model.MProcess;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class GpmnBDIConverter
{
	/** The loader. */
	protected OAVBDIModelLoader loader;
	
	/**
	 * 
	 */
	public GpmnBDIConverter(OAVBDIModelLoader loader)
	{
		this.loader = loader;
	}
	
	/**
	 * 
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
	 * 
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
	 * 
	 */
	public Object doConvert(MProcess proc, ClassLoader classloader, IOAVState state)
	{
		Object agenthandle = state.createRootObject(OAVBDIMetaModel.agent_type);
		
		// Handle package and imports
		// todo:
		
		// Handle beliefs
		// todo:
		
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
					if(((MAchieveGoal)goal).getTargetCondition()!=null)
					{
						Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition, condhandle);
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MAchieveGoal)goal).getTargetCondition());
					}
				}	
				else if(goal instanceof MMaintainGoal)
				{
					goalhandle = state.createObject(OAVBDIMetaModel.maintaingoal_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.modelelement_has_name, goal.getName());
					state.addAttributeValue(agenthandle, OAVBDIMetaModel.capability_has_goals, goalhandle);
					if(((MMaintainGoal)goal).getMaintainCondition()!=null)
					{
						Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
						state.setAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition, condhandle);
						state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, ((MMaintainGoal)goal).getMaintainCondition());
					}
				}
				
				if(goal.getCreationCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getCreationCondition());
				}
				if(goal.getContextCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getContextCondition());
				}
				if(goal.getDropCondition()!=null)
				{
					Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
					state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition, condhandle);
					state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_content, goal.getDropCondition());
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
			
				// todo: parameters
			}
		}
		
		
		// Do second pass post-processing
		
		OAVBDIXMLReader.ExpressionProcessor expost = new OAVBDIXMLReader.ExpressionProcessor();
		
		// Handle goal conditions
		Collection goalhandles = state.getAttributeValues(agenthandle, OAVBDIMetaModel.capability_has_goals);
		if(goalhandles!=null)
		{
			for(Iterator it = goalhandles.iterator(); it.hasNext(); )
			{
				Object goalhandle = it.next();
				Object condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition);
				if(condhandle!=null)
					expost.postProcess(state, goalhandle, agenthandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition);
				if(condhandle!=null)
					expost.postProcess(state, goalhandle, agenthandle, classloader);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition);
				if(condhandle!=null)
					expost.postProcess(state, goalhandle, agenthandle, classloader);

				if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(condhandle!=null)
						expost.postProcess(state, goalhandle, agenthandle, classloader);
				}
				else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(condhandle!=null)
						expost.postProcess(state, goalhandle, agenthandle, classloader);
				}
			}
		}
		
		return agenthandle;
	}
}
