package jadex.bpmn.runtime.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentExecutionService;

/**
 *  Task for creating a component.
 */
public class CreateComponentTask extends AbstractTask
{
	static Set reserved;
	static
	{
		reserved = new HashSet();
		reserved.add("name");
		reserved.add("model");
		reserved.add("configuration");
		reserved.add("suspend");
		reserved.add("creator");
		reserved.add("arguments");
	}
	
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		String name = (String)context.getParameterValue("name");
		String model = (String)context.getParameterValue("model");
		String config = (String)context.getParameterValue("configuration");
		boolean suspend = context.getParameterValue("suspend")!=null? ((Boolean)context.getParameterValue("suspend")).booleanValue(): false;
		Object creator = context.getParameterValue("creator");
		
		Map args = (Map)context.getParameterValue("arguments");
		if(args==null)
		{
			args = new HashMap();
			Map params = context.getActivity().getParameters();
			if(params!=null)
			{
				for(Iterator it=params.values().iterator(); it.hasNext(); )
				{
					MParameter param = (MParameter)it.next();
					if(!reserved.contains(param.getName()))
						args.put(param.getName(), context.getParameterValue(param.getName()));
				}
			}
		}
//		System.out.println("args: "+args);

		IComponentExecutionService ces = (IComponentExecutionService)instance.getComponentAdapter().getServiceContainer().getService(IComponentExecutionService.class);
		ces.createComponent(name, model, config, args, suspend, null, creator);
	}
}
