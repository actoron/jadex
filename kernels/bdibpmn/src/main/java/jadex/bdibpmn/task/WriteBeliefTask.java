package jadex.bdibpmn.task;

import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;

/**
 *  Write a belief (set) value.
 *  The belief(set) is specified by the 'belief(set)name' belief.
 *  The value is specified by the 'value' belief.
 *  For belief sets a mode 'add', 'remove', or 'removeAll' can be specified to distinguish
 *  between value addition (default) and removal.
 */
public class WriteBeliefTask extends AbstractTask
{
	//-------- constants --------
	
	/** The 'add' mode (default). */
	public static final String	MODE_ADD	= "add";
	
	/** The 'remove' mode. */
	public static final String	MODE_REMOVE	= "remove";
	
	/** The 'removeAll' mode. */
	public static final String	MODE_REMOVE_ALL	= "removeAll";
	
	//-------- AbstractTask methods --------
	
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
		
		if(context.hasParameterValue("beliefname"))
		{
			String name = (String)context.getParameterValue("beliefname");
			Object value = context.getParameterValue("value");
			inst.getBeliefbase().getBelief(name).setFact(value);
		}
		else if(context.hasParameterValue("beliefsetname"))
		{
			String name = (String)context.getParameterValue("beliefsetname");
			if(!context.hasParameterValue("mode") || MODE_ADD.equals(context.getParameterValue("mode")))
			{
				Object value = context.getParameterValue("value");
				inst.getBeliefbase().getBeliefSet(name).addFact(value);
			}
			else if(MODE_REMOVE.equals(context.getParameterValue("mode")))
			{
				Object value = context.getParameterValue("value");
				inst.getBeliefbase().getBeliefSet(name).removeFact(value);
			}
			else if(MODE_REMOVE_ALL.equals(context.getParameterValue("mode")))
			{
				inst.getBeliefbase().getBeliefSet(name).removeFacts();
			}
			else
			{
				throw new RuntimeException("Unknown mode: "+context.getParameterValue("mode")+", "+context);
			}
		}
		else if (context.hasParameterValue("beliefname0"))
		{
			for(int i=0; ; i++)
			{
				if(context.hasParameterValue("beliefname"+i))
				{
					String name = (String)context.getParameterValue("beliefname"+i);
					Object value = context.getParameterValue("value"+i);
					inst.getBeliefbase().getBelief(name).setFact(value);
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			throw new RuntimeException("Belief(set)name no specified: "+context);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The write belief task can be used for setting a value to a belief or" +
			"for adding/removing a value to/from a beliefset.";
		
		ParameterMetaInfo belnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "beliefname", null, "The beliefname parameter identifies the belief.");
		ParameterMetaInfo belsetnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "beliefsetname", null, "The beliefsetname parameter identifies the beliefset.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value", null, "The value parameter identifies the value to set/add/remove.");
		ParameterMetaInfo modemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "mode", null, "The mode parameter identifies the beliefset mode (add, remove, or remove all).");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{belnamemi, belsetnamemi, valuemi, modemi}); 
	}
}
