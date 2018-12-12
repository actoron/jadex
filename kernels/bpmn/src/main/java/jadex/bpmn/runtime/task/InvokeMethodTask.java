package jadex.bpmn.runtime.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;


/**
 *  Task for invoking a method with parameters and optionally 
 *  storing the result as parameter value in the context.
 */
public class InvokeMethodTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		Object object = context.hasParameterValue("object")? context.getParameterValue("object"): null;
		Class clazz = context.hasParameterValue("class")? (Class)context.getParameterValue("class"): object.getClass();
		String methodname = (String)context.getParameterValue("methodname");
		String returnname = context.hasParameterValue("returnname")? (String)context.getParameterValue("returnname"): null;

//		System.out.println("invoke method task: "+instance.getComponentIdentifier().getLocalName()+" "+methodname);
		
		// Collect parameters.
		List params = new ArrayList();
		List<Class> paramclasses = new ArrayList();
		if(context.hasParameterValue("param"))
		{
			params.add(context.getParameterValue("param"));
			paramclasses.add(((MParameter)context.getModelElement().getParameters().get("param")).getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports()));
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
			Object val = method.invoke(object, params.toArray());
			if(returnname!=null)
			{
				context.setOrCreateParameterValue(returnname, val);
			}
		}
		catch(InvocationTargetException e)
		{
			throw SUtil.throwUnchecked(e);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The invoke method task can be used to invoke a mathod on an object or a" +
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

		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{objectmi, classmi, methodnamemi, parammi, paramsmi, retmi}); 
	}
}
