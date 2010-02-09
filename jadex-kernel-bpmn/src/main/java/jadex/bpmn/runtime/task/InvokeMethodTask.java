package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 *  Task for invoking a method with parameters and optionally 
 *  storing the result as parameter value in the context.
 */
public class InvokeMethodTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		Object object = context.hasParameterValue("object")? context.getParameterValue("object"): null;
		Class clazz = context.hasParameterValue("clazz")? (Class)context.getParameterValue("clazz"): object.getClass();
		String methodname = (String)context.getParameterValue("methodname");
		String returnname = context.hasParameterValue("returnname")? (String)context.getParameterValue("returnname"): null;
		
		// Collect parameters.
		List params = new ArrayList();
		List paramclasses = new ArrayList();
		if(context.hasParameterValue("param"))
		{
			params.add(context.getParameterValue("param"));
			paramclasses.add(((MParameter)context.getModelElement().getParameters().get("param")).getClazz());
		}
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("param"+i))
			{
				params.add(context.getParameterValue("param"+i));
			}
			else
			{
				break;
			}
		}
		
		try
		{
			Method method = clazz.getMethod(methodname, (Class[])paramclasses.toArray(new Class[paramclasses.size()]));
			Object ret = method.invoke(object, params.toArray());
			if(returnname!=null)
			{
				context.setParameterValue(returnname, ret);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
