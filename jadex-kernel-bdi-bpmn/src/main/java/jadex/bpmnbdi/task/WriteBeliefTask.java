package jadex.bpmnbdi.task;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmnbdi.BpmnPlanBodyInstance;

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
	public void doExecute(ITaskContext context, IProcessInstance instance)
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
		else
		{
			throw new RuntimeException("Belief(set)name no specified: "+context);
		}
	}
}
