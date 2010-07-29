package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;

public class TaskProviderProxy implements IJadexTaskProvider
{

	Object provider;
	
	/**
	 * Create a TaskProviderProxy for the given provider not 
	 * implementing the {@link IJadexTaskProvider} interface.
	 * 
	 * @param provider to proxy for
	 */
	protected TaskProviderProxy(Object provider)
	{
		super();
		this.provider = provider;
	}

	@Override
	public String[] getAvailableTaskImplementations()
	{
		String[] tasks;
		Object returnValue = null;
		
		// use reflection
		Method getTaskMetaInfoMethod;
		try
		{
			getTaskMetaInfoMethod = provider.getClass()
					.getMethod(IJadexTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_AVAILABLE_TASK_IMPLEMENTATIONS);
			returnValue = getTaskMetaInfoMethod.invoke(provider);
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log(e, IStatus.WARNING);
		}

		// check the return value
		if (returnValue != null && returnValue instanceof String[])
		{
			tasks = (String[]) returnValue;
		}
		else
		{
			tasks = new String[0];
		}
		
		return tasks;
	}

	@Override
	public ITaskMetaInfo getTaskMetaInfo(String fqClassName)
	{
		try
		{
			// use reflection
			Method getTaskMetaInfoMethod = provider.getClass()
					.getMethod(IJadexTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_TASK_META_INFO);
			Object returnValue = getTaskMetaInfoMethod.invoke(provider, new Object[]{fqClassName});
			
			// check the return value
			if (returnValue instanceof ITaskMetaInfo)
			{
				return (ITaskMetaInfo) returnValue;
			}
			else
			{
				// use reflection proxy
				return new TaskMetaInfoProxy(returnValue);
			}
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log(e, IStatus.ERROR);
		}
		
		// fall through
		return TaskProviderSupport.NO_TASK_META_INFO_PROVIDED;
	}

}
