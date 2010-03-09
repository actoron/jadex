package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  Writes values to context variables.
 */
public class WriteContextTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance) throws Exception
	{
		boolean incontext = context.hasParameterValue("context")? ((Boolean)context.getParameterValue("context")).booleanValue(): false;
		
		if(context.hasParameterValue("variablename"))
		{
			String name = (String)context.getParameterValue("variablename");
			Object val = context.getParameterValue("value");
			Object key = context.getParameterValue("key");
			if(incontext)
				instance.setContextVariable(name, key, val);
			else
				setVariableValue(name, key, val, instance);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("variablename"+i))
			{
				String name = (String)context.getParameterValue("variablename"+i);
				Object val = context.getParameterValue("value"+i);
				Object key = context.getParameterValue("key"+i);
				if(incontext)
					instance.setContextVariable(name, key, val);
				else
					setVariableValue(name, key, val, instance);
			}
			else
			{
				break;
			}
		}
	}
	
	/**
	 *  Set a variable value using the next fitting scope (from inner to outer).
	 *  @param name The variable name.
	 *  @param key The key (e.g. index of map key).
	 *  @param value The value.
	 *  @param instance The bpmn interpreter.
	 */
	protected void setVariableValue(String name, Object key, Object value, BpmnInterpreter instance)
	{
		boolean found = false;
		for(ProcessThread t=instance.getThreadContext().getInitiator(); t!=null && !found; t=t.getThreadContext().getInitiator() )
		{
			if(t.getActivity().hasParameter(name))
			{
				t.setParameterValue(name, key, value);
				found	= true;
			}
		}
		if(!found)
			throw new RuntimeException("Unknown variable: "+name+" "+key+" "+value);
	}
	
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The write context task can be used to write values to context variables.";
		ParameterMetaInfo vnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "variablename", null, "The name of the context variable that is the target of the write operation.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				Object.class, "value", null, "The value that is written to the context variable.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{vnamemi, valuemi}); 
	}
}
