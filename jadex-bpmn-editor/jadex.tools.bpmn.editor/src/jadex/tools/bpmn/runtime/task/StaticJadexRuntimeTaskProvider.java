package jadex.tools.bpmn.runtime.task;


import java.util.HashMap;
import java.util.Map;

/**
 *  Helper class for delivering meta data for tasks as long as no
 *  introspection task provider is available.
 */
public class StaticJadexRuntimeTaskProvider implements IRuntimeTaskProvider
{
	// ---- static part ----
	
	public static final String[] TASK_IMPLEMENTATIONS = new String[] {
		"jadex.bpmn.runtime.task.PrintTask.class",
		"jadex.bpmn.runtime.task.InvokeMethodTask.class",
		"jadex.bpmn.runtime.task.CreateComponentTask.class",
		"jadex.bpmn.runtime.task.DestroyComponentTask.class",
		"jadex.bpmn.runtime.task.StoreResultsTask.class",
		"jadex.bpmn.runtime.task.UserInteractionTask",
		
		"jadex.bpmnbdi.task.DispatchGoalTask.class",
		"jadex.bpmnbdi.task.WaitForGoalTask.class",
		"jadex.bpmnbdi.task.DispatchInternalEventTask.class",
		"jadex.bpmnbdi.task.WriteBeliefTask.class",
		"jadex.bpmnbdi.task.WriteParameterTask.class",
		
		"jadex.bpmnbdi.task.CreateSpaceObjectTaskTask.class",
		"jadex.bpmnbdi.task.WaitForSpaceObjectTaskTask.class",
		"jadex.bpmnbdi.task.RemoveSpaceObjectTaskTask.class",
		
		"jadex.wfms.client.task.WorkitemTask.class",
	};
	
	private static Map createMetaInfos()
	{
		Map map = new HashMap();
		
		// General bpmn tasks.
		
		// print task.
		String desc = "The print task can be used for printing out a text on the console.";
		ParameterMetaInfo textmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "text", null, "The text parameter should contain the text to be printed.");
		map.put("jadex.bpmn.runtime.task.PrintTask.class", new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi})); 
		
		// store results task.
		desc = "The store results task can be used for storing values as process results. As" +
			"parameters a name, value pair or if more than one results an arbitrary number of name, value" +
			"pairs with a postfix number can be used (e.g. name0 and value0, name1 and value1, etc.)";
		ParameterMetaInfo namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter identifies the name of the result parameter.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value", null, "The value parameter identifies the value of the result parameter belonging to the name.");
		ParameterMetaInfo namesmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name0[..n]", null, "The name0[..n] parameter(s) identify the name(s) of the result parameter(s).");
		ParameterMetaInfo valuesmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value0[..n]", null, "The value0[..n] parameter(s) identify the value(s) of the result parameter(s) belonging to the name(s).");
		TaskMetaInfo tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{namemi, valuemi, namesmi, valuesmi});
		map.put("jadex.bpmn.runtime.task.StoreResults.class", tmi);
		
		// invoke method task.
		desc = "The invoke method task can be used to invoke a mathod on an object or a" +
			"static method on a class. It accepts any number of parameters and may store the result" +
			"in a specific parameter.";
		ParameterMetaInfo objectmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "object", null, "The object parameter identifies the object the method should be called on.");
		ParameterMetaInfo classmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Class.class, "class", null, "The class parameter identifies the class the method should be called on (alternativly to object).");
		ParameterMetaInfo methodnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "methodname", null, "The methodname parameter identifies the method to be called.");
		ParameterMetaInfo parammi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "param", null, "The param parameter stores the value for an input parameter of the methodcall.");
		ParameterMetaInfo paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "param0[..n]", null, "The param0[..n] parameter(s) stores the value(s) for input parameter(s) of the methodcall.");
		ParameterMetaInfo retmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "returnname", null, "The returnname parameter identifies the result task parameter for storing the result of the call.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{objectmi, classmi, methodnamemi, parammi, paramsmi, retmi});
		map.put("jadex.bpmn.runtime.task.InvokeMethodTask.class", tmi);

		// create component task.
		desc = "The create component task can be used for creating a new component instance. " +
			"This allows a process to start other processes as well as other kinds of components like agents";
		namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter identifies the name of new component instance.");
		ParameterMetaInfo modelmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "model", null, "The model parameter contains the filename of the component to start.");
		ParameterMetaInfo confmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "configuration", null, "The configuration parameter defines the configuration the component should be started in.");
		ParameterMetaInfo suspendmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "suspend", null, "The suspend parameter can be used to create the component in suspended mode.");
		ParameterMetaInfo subcommi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "subcomponent", null, "The subcomponent parameter decides if the new component is considered as subcomponent.");
		ParameterMetaInfo killimi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "killlistener", null, "The killlistener parameter can be used to be notified when the component terminates.");
		ParameterMetaInfo resultmapmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String[].class, "resultmapping", null, "The resultmapping parameter defines the mapping of result to context parameters. " +
				"The string array structure is 0: first result name, 1: first context parameter name, 2: second result name, etc.");
		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter specifies is the activity should wait for the completeion of the started component." +
				"This is e.g. necessary if the return values should be used.");
		ParameterMetaInfo mastermi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "master", null, "The master parameter decides if the component is considered as master for its parent. The parent" +
				"can implement special logic when a master dies, e.g. an application terminates itself.");
		ParameterMetaInfo argumentsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "arguments", null, "The arguments parameter allows passing an argument map of name value pairs.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{namemi, modelmi, confmi, suspendmi, 
			subcommi, killimi, resultmapmi, waitmi, mastermi, argumentsmi});
		map.put("jadex.bpmn.runtime.task.CreateComponentTask.class", tmi);
		
		//  destroy component task.
		desc = "The destroy component task can be used for killing a specific component.";
		ParameterMetaInfo cidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "componentid", null, "The componentid parameter serves for specifying the component id.");
		namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter serves for specifying the local component name (if id not available).");
		ParameterMetaInfo lismi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "resultlistener", null, "The resultlistener parameter can be used to be notified when the component terminates.");
		waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter specifies is the activity should wait for the component being killed." +
				"This is e.g. necessary if the return values should be used.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{cidmi, namemi, lismi, waitmi}); 
		map.put("jadex.bpmn.runtime.task.DestroyComponentTask.class", tmi);
		
		// user interaction task.
		desc = "The user interaction task can be used for fetching in parameter values " +
			"via an interactive user interface dialog. The task automatically uses all declared" +
			"in parameters.";
		tmi = new TaskMetaInfo(desc, null); 
		map.put("jadex.bpmn.runtime.task.UserInteractionTask.class", tmi);
		
		// BDI tasks.
		
		// dispatch goal task.
		desc = "The dispatch goal task can be used for dipatching a goal as top-level " +
			" or subgoal and optinally wait for the result (available only in bdi-bpmn plans).";
		ParameterMetaInfo typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "type", null, "The type parameter identifies the user goal type.");
		paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the goal parameters.");
		ParameterMetaInfo subgoal = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "subgoal", null, "The subgoal parameter for dispatching as top-level or subgoal.");
		ParameterMetaInfo wait = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter to wait for the results.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, paramsmi, subgoal, wait}); 
		map.put("jadex.bpmnbdi.task.DispatchGoalTask.class", tmi);
	
		// wait for goal task.
		desc = "The wait for goal task can be used to wait for an existing goal (available only in bdi-bpmn plans).";
		ParameterMetaInfo goalmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "goal", null, "The goal parameter identifies the goal to be waited for.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{goalmi});
		map.put("jadex.bpmnbdi.task.WaitForGoalTask.class", tmi);
		
		// dispatch internal event task.
		desc = "The dispatch internal event task can be used for dipatching an internal event (available only in bdi-bpmn plans).";
		typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "type", null, "The type parameter identifies the user goal type.");
		paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the goal parameters.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, paramsmi}); 
		map.put("jadex.bpmnbdi.task.DispatchInternalEventTask.class", tmi);
		
		// write belief task.
		desc = "The write belief task can be used for setting a value to a belief or" +
			"for adding/removing a value to/from a beliefset (available only in bdi-bpmn plans).";
		ParameterMetaInfo belnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "beliefname", null, "The beliefname parameter identifies the belief.");
		ParameterMetaInfo belsetnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "beliefsetname", null, "The beliefsetname parameter identifies the beliefset.");
		valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value", null, "The value parameter identifies the value to set/add/remove.");
		ParameterMetaInfo modemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "mode", null, "The mode parameter identifies the beliefset mode (add, remove, or remove all).");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{belnamemi, belsetnamemi, valuemi, modemi}); 
		map.put("jadex.bpmnbdi.task.WriteBeliefTask.class", tmi);
			
		// write parameter task.
		desc = "The write parameter task can be used for setting a value to a parameter or" +
			"for adding/removing a value to/from a parameterset (available only in bdi-bpmn plans).";
		ParameterMetaInfo paramnamnmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "parametername", null, "The parametername parameter identifies the parameter.");
		ParameterMetaInfo paramsetnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "parametersetname", null, "The parametersetname parameter identifies the parameterset.");
		valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value", null, "The value parameter identifies the value to set/add/remove.");
		modemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "mode", null, "The mode parameter identifies the parameterset mode (add, remove, or remove all).");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{paramnamnmi, paramsetnamemi, valuemi, modemi});
		map.put("jadex.bpmnbdi.task.WriteParameterTask.class", tmi);
		
		// These task are in bdi but aren't bdi specific
		
		// create space object task task.
		desc = "The create space object task task can be used to create a space object task in an" +
			"EnvSupport environment (available only in bdi-bpmn plans).";
		typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "type", null, "The type parameter identifies the space object task type.");
		ParameterMetaInfo spacemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "space", null, "The space parameter defines the space.");
		ParameterMetaInfo objectid = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "objectid", null, "The objectid parameter for identifying the space object.");
		ParameterMetaInfo propsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "properties", null, "The properties parameter to specify a map of properties for the task.");
		ParameterMetaInfo taskidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
			Object.class, "taskid", null, "The taskid parameter for the return value, i.e. the id of the created task.");
		waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter to wait for the task.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, spacemi, objectid, propsmi, taskidmi, waitmi}); 
		map.put("jadex.bpmnbdi.task.CreateSpaceObjectTaskTask.class", tmi);
		
		// wait for space object task task.
		desc = "The wait for space object task task can be used to wait for completion of a task in an" +
			"EnvSupport environment (available only in bdi-bpmn plans).";
		spacemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "space", null, "The space parameter defines the space.");
		objectid = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "objectid", null, "The objectid parameter for identifying the space object.");
		taskidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
			Object.class, "taskid", null, "The taskid parameter for the return value, i.e. the id of the created task.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{spacemi, objectid, taskidmi}); 
		map.put("jadex.bpmnbdi.task.WaitForSpaceObjectTaskTask.class", tmi);
		
		// remove space object task task.
		desc = "The remove space object task task can be used to remove a task in an" +
			"EnvSupport environment (available only in bdi-bpmn plans).";
		spacemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "space", null, "The space parameter defines the space.");
		objectid = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "objectid", null, "The objectid parameter for identifying the space object.");
		taskidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
			Object.class, "taskid", null, "The taskid parameter for identifying the task.");
		tmi = new TaskMetaInfo(desc, new ParameterMetaInfo[]{spacemi, objectid, taskidmi}); 
		map.put("jadex.bpmnbdi.task.RemoveSpaceObjectTaskTask.class", tmi);
		
		return map;
	}
	
	// ---- attributes ----
	
	protected Map metaInfoMap;
	
	// ---- constructor ----
	
	/**
	 * 
	 */
	public StaticJadexRuntimeTaskProvider()
	{
		metaInfoMap = createMetaInfos();
	}

	// ---- interface methods ----

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	public String[] getAvailableTaskImplementations()
	{
		return TASK_IMPLEMENTATIONS;
	}
	
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfoFor(java.lang.String)
	 */
	public TaskMetaInfo getTaskMetaInfoFor(String implementationClass)
	{
		return (TaskMetaInfo)metaInfoMap.get(implementationClass);
	}

}
