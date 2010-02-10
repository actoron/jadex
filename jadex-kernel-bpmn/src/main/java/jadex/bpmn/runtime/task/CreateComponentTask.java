package jadex.bpmn.runtime.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentExecutionService;
import jadex.commons.concurrent.IResultListener;

/**
 *  Task for creating a component.
 */
public class CreateComponentTask implements ITask
{
	static Set reserved;
	static
	{
		reserved = new HashSet();
		reserved.add("name");
		reserved.add("model");
		reserved.add("configuration");
		reserved.add("suspend");
		reserved.add("subcomponent");
		reserved.add("resultlistener");
		reserved.add("resultmapping");
		reserved.add("wait");
		reserved.add("arguments");
	}
	
	/**
	 *  Execute the task.
	 */
	public void execute(final ITaskContext context, BpmnInterpreter instance, final IResultListener listener)
	{
		String name = (String)context.getParameterValue("name");
		String model = (String)context.getParameterValue("model");
		String config = (String)context.getParameterValue("configuration");
		boolean suspend = context.getParameterValue("suspend")!=null? ((Boolean)context.getParameterValue("suspend")).booleanValue(): false;
		boolean sub = context.getParameterValue("subcomponent")!=null? ((Boolean)context.getParameterValue("subcomponent")).booleanValue(): false;
		IResultListener resultlistener = (IResultListener)context.getParameterValue("resultlistener");
		final String[] resultmapping = (String[])context.getParameterValue("resultmapping");
		boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): resultlistener==null && resultmapping!=null;
		boolean master = context.getParameterValue("master")!=null? ((Boolean)context.getParameterValue("master")).booleanValue(): false;
		
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
				
		if(resultlistener==null && resultmapping!=null)
		{
			resultlistener = new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(result!=null)
					{
						Map results = (Map)result;
						for(int i=0; i<resultmapping.length/2; i++)
						{
							Object value = results.get(resultmapping[i]);
							context.setParameterValue(resultmapping[i+1], value);
							
//							System.out.println("Mapped result value: "+value+" "+resultmapping[i]+" "+resultmapping[i+1]);
						}
					}
					listener.resultAvailable(CreateComponentTask.this, null);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					listener.exceptionOccurred(CreateComponentTask.this, exception);
				}
			};
		}
		
		ces.createComponent(name, model, config, args, suspend, null, sub ? instance.getComponentAdapter().getComponentIdentifier() : null, resultlistener, master);

		if(!wait)
			listener.resultAvailable(this, null);
	}
}
