package jadex.gpmn;

import jadex.bdi.OAVBDIModelLoader;
import jadex.bdi.OAVBDIXMLReader;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.JavaStandardPlanExecutor;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdibpmn.BpmnPlanExecutor;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.ThreadSuspendable;
import jadex.component.ComponentXMLReader;
import jadex.gpmn.model.MActivationEdge;
import jadex.gpmn.model.MActivationPlan;
import jadex.gpmn.model.MBpmnPlan;
import jadex.gpmn.model.MContext;
import jadex.gpmn.model.MContextElement;
import jadex.gpmn.model.MGoal;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MPlanEdge;
import jadex.gpmn.model.MSubprocess;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Class for converting a gpmn model description to an agent description.
 */
public class GpmnBDIConverter
{
	//-------- attributes --------
	
	/** The bdi agent loader. */
	protected OAVBDIModelLoader loader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new converter.
	 */
	public GpmnBDIConverter(IComponentIdentifier root)
	{
		// Todo: use original OAVBDIModelLoader (via service?) for accurate properties.
		this.loader = new OAVBDIModelLoader(SUtil.createHashMap(new String[]
			{
				"planexecutor_standard", 
				"planexecutor_bpmn"
			},
			new Object[]
			{
				null,
				null
			}), root);
	}
	
	//-------- methods --------
	
	/**
	 *  Convert a gpmn model to a bdi agent.
	 */
	public OAVAgentModel convertGpmnModelToBDIAgents(MGpmnModel model, ClassLoader classloader)
	{
		OAVAgentModel agentmodel = null;
		
		OAVTypeModel typemodel = new OAVTypeModel(model.getModelInfo().getName()+"_typemodel", classloader);
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
		
		
//		Report	report	= new Report();
		state.addStateListener(listener, false);
		
		Object handle = state.createRootObject(OAVBDIMetaModel.agent_type); 
		
		state.setAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name, model.getModelInfo().getName());
		//TODO: Add process description
		//state.setAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description, model.getDescription());
//		state.setAttributeValue(handle, OAVBDIMetaModel.capability_has_package, model.getModelInfo().getPackage());
//		String[] imports = (String[]) model.getImports().toArray(new String[0]);
//		if(imports!=null)
//		{
//			for(int i=0; i<imports.length; i++)
//			{
//				state.addAttributeValue(handle, OAVBDIMetaModel.capability_has_imports, imports[i]);
//			}
//		}
		doConvert(model, classloader, state, handle, false);
		state.removeStateListener(listener);
		agentmodel =  new OAVAgentModel(state, handle, model.getModelInfo(), types, model.getLastModified(), null);
		try
		{
			loader.createAgentModelEntry(agentmodel, model.getModelInfo());
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
	public void doConvert(MGpmnModel model, final ClassLoader classloader, final IOAVState state, final Object scopehandle, boolean subprocess)
	{		
		// Handle package and imports here?!
		// TODO:
		
		String modelname = model.getModelInfo().getFilename().substring(0, model.getModelInfo().getFilename().length() - 5);
		
		// Create default configuration
		Object confighandle = state.createObject(OAVBDIMetaModel.configuration_type);
		state.setAttributeValue(confighandle, OAVBDIMetaModel.modelelement_has_name, "default");
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_configurations, confighandle);
		
		MContext modelcontext = model.getContext();
		if(modelcontext!=null)
		{
			List elements = modelcontext.getElements();
			if (elements != null)
			{
				for(int i=0; i<elements.size(); i++)
				{
					MContextElement element = (MContextElement)elements.get(i);
					
					String name = subprocess? modelname + "." + element.getName(): element.getName();
					
					if(!element.isSet())
					{
						createBelief(state, scopehandle, name, element.getType(), element.getValue());
					}
					else
					{
						createBeliefSet(state, scopehandle, name, element.getType(), null, element.getValue());
					}
				}
			}
		}
		
		// --- Plan Edge representation ---
		// Prepare goal->activation_plan map
		Map activationplanmap = new HashMap();
		for (Iterator it = model.getPlanEdges().iterator(); it.hasNext(); )
		{
			MPlanEdge edge = (MPlanEdge) it.next();
			if (model.getActivationPlans().containsKey(edge.getTargetId()))
			{
				List sourceedges = (List) activationplanmap.get(edge.getSourceId());
				if (sourceedges == null)
				{
					sourceedges = new ArrayList();
					activationplanmap.put(edge.getSourceId(), sourceedges);
				}
				sourceedges.add(model.getActivationPlans().get(edge.getTargetId()));
			}
		}
		
		// Prepare user_plan->goal map
		Map userplanmap = new HashMap();
		for (Iterator it = model.getPlanEdges().iterator(); it.hasNext(); )
		{
			MPlanEdge edge = (MPlanEdge) it.next();
			if (model.getBpmnPlans().containsKey(edge.getTargetId()))
			{
				List targetedges = (List) activationplanmap.get(edge.getTargetId());
				if (targetedges == null)
				{
					targetedges = new ArrayList();
					userplanmap.put(edge.getTargetId(), targetedges);
				}
				targetedges.add(model.getGoals().get(edge.getSourceId()));
			}
		}
		
		// --- Activation Edge representation ---
		
		// Prepare plan_id->activation_edge map
		Map planactivationedges = new HashMap();
		for (Iterator it = model.getActivationEdges().iterator(); it.hasNext(); )
		{
			MActivationEdge edge = (MActivationEdge) it.next();
			List outgoingactivationedges = (List) planactivationedges.get(edge.getSourceId());
			if (outgoingactivationedges == null)
			{
				outgoingactivationedges = new ArrayList();
				planactivationedges.put(edge.getSourceId(), outgoingactivationedges);
			}
			outgoingactivationedges.add(edge);
		}
		
		// Handle goals
		for(Iterator it = model.getGoals().values().iterator(); it.hasNext(); )
		{
			MGoal goal = (MGoal)it.next();
			String name = subprocess? modelname + "." + goal.getName(): goal.getName();
			
			OAVObjectType goaltype = MGoal.Types.ACHIEVE_GOAL.equals(goal.getGoalType())? OAVBDIMetaModel.achievegoal_type: 
				MGoal.Types.MAINTAIN_GOAL.equals(goal.getGoalType())? OAVBDIMetaModel.maintaingoal_type:
				MGoal.Types.PERFORM_GOAL.equals(goal.getGoalType())? OAVBDIMetaModel.performgoal_type: null;
			Object goalhandle = createGoal(state, scopehandle, name, goaltype, goal.getRetry(), 
				goal.getRetryDelay(), goal.getRecur(), goal.getRecurDelay(), goal.getExcludeMode(), 
				goal.getRetry(), goal.getUnique(), goal.getCreationCondition(), goal.getContextCondition(), 
				goal.getDropCondition());
			
			if(MGoal.Types.ACHIEVE_GOAL.equals(goal.getGoalType()) && (goal.getTargetCondition()!=null))
			{
				Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
				state.setAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition, condhandle);
				state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, goal.getTargetCondition());
				state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
			}
			else if(MGoal.Types.MAINTAIN_GOAL.equals(goal.getGoalType()) && goal.getMaintainCondition()!=null)
			{
				Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
				state.setAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition, condhandle);
				state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, goal.getMaintainCondition());
				state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
			}
			// Handle activation plans
			List activationplans = (List) activationplanmap.get(goal.getId());
			if (activationplans != null)
			{
				for(Iterator it2 = activationplans.iterator(); it2.hasNext(); )
				{
					MActivationPlan plan = (MActivationPlan) it2.next();
					String actName = "ActivationPlan_Goal:"+name+"_"+String.valueOf(plan.getName());
					boolean seq = MActivationPlan.Modes.SEQUENTIAL.equals(plan.getMode());
						
					List activationedges = (List) planactivationedges.get(plan.getId());
					if (seq)
					{
						activationedges = new ArrayList(activationedges);
						Collections.sort(activationedges, new Comparator()
						{
							public int compare(Object o1, Object o2)
							{
								MActivationEdge ae1 = (MActivationEdge) o1;
								MActivationEdge ae2 = (MActivationEdge) o2;
								return ae1.getOrder() - ae2.getOrder();
							}
						});
					}
					
					// Create plan
					// TODO: Handle orphaned activation plans
					Object planhandle = seq
						? createPlan(scopehandle, state, actName, "jadex.gpmn.runtime.plan.SequentialGoalExecutionPlan", null, null, "bpmn")
						: createPlan(scopehandle, state, actName, "jadex.gpmn.runtime.plan.ParallelGoalExecutionPlan", null, null, "bpmn");
					
					// Create trigger
					createPlanTrigger(planhandle, state, new String[]{name}, null, null);
					
					// Create subgoals parameter set
					// TODO: Support Subprocesses
					List activationtargets = new ArrayList();
					for(int j=0; j<activationedges.size(); j++)
					{
						MActivationEdge edge = (MActivationEdge)activationedges.get(j);
						if (model.getGoals().containsKey(edge.getTargetId()))
							activationtargets.add("new jadex.gpmn.runtime.plan.ActivationTarget(jadex.gpmn.runtime.plan.ActivationTarget.Types.GOAL,"+"\""+
								((MGoal) model.getGoals().get(edge.getTargetId())).getName()+"\")");
						else
						{
							MSubprocess sprocess = (MSubprocess) model.getSubprocesses().get(edge.getTargetId());
							if (sprocess.isInternal())
							{
								GpmnModelLoader loader = new GpmnModelLoader();
								MGpmnModel submodel = null;
								try
								{
									// hmm rid of parent ok?
									submodel = (MGpmnModel) loader.loadModel(sprocess.getProcessReference(), null, classloader, model.getModelInfo().getResourceIdentifier());
								}
								catch (Exception e)
								{
									throw new RuntimeException(e);
								}
								
								// TODO: Catch multiple subprocess instances
								doConvert(submodel, classloader, state, scopehandle, true);
								
								Set subacttargets = new HashSet();
								for (Iterator it3 = submodel.getActivationEdges().iterator(); it3.hasNext(); )
									subacttargets.add(((MActivationEdge) it3.next()).getTargetId());
								
								Set subtargets = (new HashSet(submodel.getGoals().keySet()));
								subtargets.removeAll(subacttargets);
								
								String submodelname = submodel.getModelInfo().getFilename().substring(0, model.getModelInfo().getFilename().length() - 5);
								for (Iterator it3 = subtargets.iterator(); it3.hasNext(); )
								{
									MGoal subprocgoal = (MGoal) submodel.getGoals().get(it3.next());
									activationtargets.add("new jadex.gpmn.runtime.plan.ActivationTarget(jadex.gpmn.runtime.plan.ActivationTarget.Types.GOAL,"+"\""+
											submodelname + "." + subprocgoal.getName()+"\")");
								}
							}
							else
							{
								activationtargets.add("new jadex.gpmn.runtime.plan.ActivationTarget(jadex.gpmn.runtime.plan.ActivationTarget.Types.SUBPROCESS,"+"\""+
									sprocess.getName()+"\")");
							}
						}
					}
					createParameterSet(planhandle, state, "activationtargets", "jadex.gpmn.runtime.plan.ActivationTarget", (String[]) activationtargets.toArray(new String[0]), null, true);
				}
			}
		}
		
		// Handle BPMN plans
		Map bpmnplans = model.getBpmnPlans();
		for(Iterator it = bpmnplans.values().iterator(); it.hasNext(); )
		{
			MBpmnPlan plan = (MBpmnPlan)it.next();
			String name = subprocess? modelname + "." + plan.getName(): plan.getName();
			Object planhandle = createPlan(scopehandle, state, name, plan.getPlanref(), plan.getPreCondition(), plan.getContextCondition(), "bpmn");
			List goalnames = new ArrayList();
			List plangoals = (List)userplanmap.get(plan.getId());
			// null check for orphaned plans
			if (plangoals != null)
				for (Iterator it2 = plangoals.iterator(); it2.hasNext(); )
				{
					MGoal goal = (MGoal) it2.next();
					String goalName = subprocess? modelname + "." + goal.getName(): goal.getName();
					goalnames.add(goalName);
				}
			createPlanTrigger(planhandle, state, (String[])goalnames.toArray(new String[0]), null, null);
		}
		
		Set activatedElements = new HashSet();
		Collection goalhandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_goals);
		
		if (!subprocess)
		{
			Object startplanhandle = null;
			// Create plan for starting/monitoring the process.
			String planname = "startandmonitor_"+model.getModelInfo().getName();//.substring(0, proc.getName().indexOf("."));
			startplanhandle = createPlan(scopehandle, state, planname, "jadex.gpmn.runtime.plan.StartAndMonitorProcessPlan", null, null, null);
			
			// Prepare activated elements set
			for (Iterator it=model.getActivationEdges().iterator(); it.hasNext(); )
				activatedElements.add(((MActivationEdge)it.next()).getTargetId());
		
		
			// Create achieve_goals maintain_goals parameterset
			List agoalnames = new ArrayList();
			List mgoalnames = new ArrayList();
			for(Iterator it=model.getGoals().values().iterator(); it.hasNext(); )
			{
				MGoal goal = (MGoal)it.next();
				if(!activatedElements.contains(goal.getId()))
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
			createParameterSet(startplanhandle, state, "achieve_goals", "String", 
				agoalnames.size()==0? null: (String[])agoalnames.toArray(new String[agoalnames.size()]), null, true);
			createParameterSet(startplanhandle, state, "maintain_goals", "String", 
				mgoalnames.size()==0? null: (String[])mgoalnames.toArray(new String[mgoalnames.size()]), null, true);
			
			// Make this plan the initial plan
			Object iniplanhandle = state.createObject(OAVBDIMetaModel.configelement_type);
			state.setAttributeValue(iniplanhandle, OAVBDIMetaModel.configelement_has_ref, planname);
			state.addAttributeValue(confighandle, OAVBDIMetaModel.configuration_has_initialplans, iniplanhandle);
		}

		// Do second pass post-processing
		
		final IModelInfo modelinfo = model.getModelInfo();
		IContext context = new IContext() 
		{
			public Object getRootObject() 
			{
				return modelinfo;
			}
			public ClassLoader getClassLoader() 
			{
				return classloader;
			}
			public Object getUserContext() 
			{
				Map	user	= new HashMap();
				user.put(OAVObjectReaderHandler.CONTEXT_STATE, state);
				user.put(ComponentXMLReader.CONTEXT_ENTRIES, new MultiCollection());	// Todo: check for errors after conversion?
				return user;
			}
		};
		
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
				//clpost.postProcess(state, belhandle, scopehandle, classloader);
				clpost.postProcess(context, belhandle);
				if(exphandle!=null)
					expost.postProcess(context, exphandle);
			}
		}
		Collection beliefsethandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_beliefsets);
		if(beliefsethandles!=null)
		{
			for(Iterator it = beliefsethandles.iterator(); it.hasNext(); )
			{
				Object belsethandle = it.next();
				Object exphandle = state.getAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_factsexpression);
				clpost.postProcess(context, belsethandle);
				if(exphandle!=null)
					expost.postProcess(context, exphandle);
				Collection expshandle = state.getAttributeValues(belsethandle, OAVBDIMetaModel.beliefset_has_facts);
				if(expshandle!=null)
				{
					for(Iterator it2=expshandle.iterator(); it2.hasNext(); )
					{
						exphandle = it2.next();
						expost.postProcess(context, exphandle);
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
				postProcessParameterElement(context, goalhandle, expost, clpost);

				Object condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_creationcondition);
				if(condhandle!=null)
					expost.postProcess(context, condhandle);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition);
				if(condhandle!=null)
					expost.postProcess(context, condhandle);
				condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition);
				if(condhandle!=null)
					expost.postProcess(context, condhandle);

				if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.achievegoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(condhandle!=null)
						expost.postProcess(context, condhandle);
				}
				else if(state.getType(goalhandle).isSubtype(OAVBDIMetaModel.maintaingoal_type))
				{
					condhandle = state.getAttributeValue(goalhandle, OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(condhandle!=null)
						expost.postProcess(context, condhandle);
				}				
			}
		}
		
		// Handle plans
		Collection planhandles = state.getAttributeValues(scopehandle, OAVBDIMetaModel.capability_has_plans);
		if(planhandles!=null)
		{
			for(Iterator it = planhandles.iterator(); it.hasNext(); )
			{
				Object planhandle = it.next();
				postProcessParameterElement(context, planhandle, expost, clpost);
				
				Object condhandle = state.getAttributeValue(planhandle, OAVBDIMetaModel.plan_has_precondition);
				if(condhandle!=null)
					expost.postProcess(context, condhandle);
				
				condhandle = state.getAttributeValue(planhandle, OAVBDIMetaModel.plan_has_contextcondition);
				if(condhandle!=null)
					expost.postProcess(context, condhandle);
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
			state.setAttributeValue(facthandle, OAVBDIMetaModel.expression_has_text, inival);
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
				state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_text, values[i]);
				state.addAttributeValue(belsethandle, OAVBDIMetaModel.beliefset_has_facts, valhandle);
			}
		}
		else if(valuesexp!=null)
		{
			Object valshandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(valshandle, OAVBDIMetaModel.expression_has_text, valuesexp);
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
		
		//TODO:Hack! Fixme!!
		//if (exclude == null)
			//exclude = OAVBDIMetaModel.EXCLUDE_NEVER;
		//System.out.println(name + " " + retry + " " + exclude);
		
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
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, creationcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		if(contextcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_contextcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, contextcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		if(dropcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(goalhandle, OAVBDIMetaModel.goal_has_dropcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, dropcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		
		return goalhandle;
	}
	
	/**
	 *  Create a plan.
	 */
	protected Object createPlan(Object scopehandle, IOAVState state, String name, String impl, String precond, String contextcond, String bodytype)
	{
		Object planhandle = state.createObject(OAVBDIMetaModel.plan_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.modelelement_has_name, name);
		state.addAttributeValue(scopehandle, OAVBDIMetaModel.capability_has_plans, planhandle);
		Object bodyhandle = state.createObject(OAVBDIMetaModel.body_type);
		state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_body, bodyhandle);
		state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_impl, impl);
		if(bodytype!=null)
			state.setAttributeValue(bodyhandle, OAVBDIMetaModel.body_has_type, bodytype);
		
		if(precond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_precondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, precond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "java");
		}
		
		if(contextcond!=null)
		{
			Object condhandle = state.createObject(OAVBDIMetaModel.condition_type);
			state.setAttributeValue(planhandle, OAVBDIMetaModel.plan_has_contextcondition, condhandle);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_text, contextcond);
			state.setAttributeValue(condhandle, OAVBDIMetaModel.expression_has_language, "jcl");
		}
		
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
		state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_text, value);
		state.setAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value, valhandle);
		return paramhandle;
	}
	
	/**
	 *  Create a parameter set.
	 */
	protected Object createParameterSet(Object paramelemhandle, IOAVState state, String name, String classname, Object[] values, String valuesexp, boolean planparamset)
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
				state.setAttributeValue(valhandle, OAVBDIMetaModel.expression_has_text, values[i]);
				state.addAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_values, valhandle);
			}
		}
		else if(valuesexp!=null)
		{
			Object valshandle = state.createObject(OAVBDIMetaModel.expression_type);
			state.setAttributeValue(valshandle, OAVBDIMetaModel.expression_has_text, valuesexp);
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
	protected void postProcessParameterElement(IContext context, Object paramelem, 
		IPostProcessor exproc, IPostProcessor clproc)
	{
		Map	ouc	= (Map)context.getUserContext();
		IOAVState state = (IOAVState)ouc.get(OAVObjectReaderHandler.CONTEXT_STATE);
		Collection paramhandles = state.getAttributeValues(paramelem, OAVBDIMetaModel.parameterelement_has_parameters);
		if(paramhandles!=null)
		{
			for(Iterator it2 = paramhandles.iterator(); it2.hasNext(); )
			{
				Object paramhandle = it2.next();
				clproc.postProcess(context, paramhandle);
//				clproc.postProcess(state, paramhandle, scopehandle, classloader);
				Object exphandle = state.getAttributeValue(paramhandle, OAVBDIMetaModel.parameter_has_value);
				if(exphandle!=null)
					exproc.postProcess(context, exphandle);
			}
		}
		Collection paramsethandles = state.getAttributeValues(paramelem, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(paramsethandles!=null)
		{
			for(Iterator it2 = paramsethandles.iterator(); it2.hasNext(); )
			{
				Object paramsethandle = it2.next();
				clproc.postProcess(context, paramsethandle);
				Object exphandle = state.getAttributeValue(paramsethandle, OAVBDIMetaModel.parameterset_has_valuesexpression);
				if(exphandle!=null)
					exproc.postProcess(context, exphandle);
				Collection expshandle = state.getAttributeValues(paramsethandle, OAVBDIMetaModel.parameterset_has_values);
				if(expshandle!=null)
				{
					for(Iterator it3=expshandle.iterator(); it3.hasNext(); )
					{
						exphandle = it3.next();
						exproc.postProcess(context, exphandle);
					}
				}
			}
		}
	}
}
