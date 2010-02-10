package jadex.tools.bpmn.runtime.task;


import java.util.HashMap;


public class StaticJadexRuntimeTaskProvider implements IRuntimeTaskProvider
{
	// ---- static part ----
	
	public static final String[] TASK_IMPLEMENTATIONS = new String[] {
		"jadex.bpmnbdi.task.WriteBeliefTask.class",
		"jadex.bpmn.runtime.task.PrintTask.class",
		"jadex.bpmn.runtime.task.InvokeMethodTask.class",
		"jadex.bpmn.runtime.task.CreateComponentTask.class",
		"jadex.bpmn.runtime.task.DestroyComponentTask.class",
		"jadex.wfms.client.task.WorkitemTask.class"
	};
	
	private static HashMap<String, TaskMetaInfo> createMetaInfos()
	{
		HashMap<String, TaskMetaInfo> map = new HashMap<String, TaskMetaInfo>();
		
		String desc = "The print task can be used for printing out a text on the console.";
		ParameterMetaInfo textmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "text", null, "The text parameter should contain the text to be printed.");
		map.put("jadex.bpmn.runtime.task.PrintTask.class", new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi})); 
		
		return map;
	}
	
	// ---- attributes ----
	
	protected HashMap<String, TaskMetaInfo> metaInfoMap;
	
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
		return metaInfoMap.get(implementationClass);
	}

}
