/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;

/**
 * @author claas
 *
 */
public class TaskMetaInfoProxy implements ITaskMetaInfo
{

	Object taskMetaInfo;
	
	/**
	 * 
	 */
	public TaskMetaInfoProxy(Object taskMetaInfo)
	{
		super();
		this.taskMetaInfo = taskMetaInfo;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		try
		{
			// use reflection
			Method getTaskMetaInfoMethod = taskMetaInfo.getClass()
					.getMethod(ITaskMetaInfo.METHOD_ITASKMETAINFO_GET_DESCRIPTION);
			Object returnValue = getTaskMetaInfoMethod.invoke(taskMetaInfo);
			
			// check the return value
			if (returnValue instanceof String)
			{
				return (String) returnValue;
			}
			else
			{
				JadexBpmnEditor.log(new UnsupportedOperationException(
						"No String for Description in TaskMetaInfoProxy for: "
								+ taskMetaInfo), IStatus.WARNING);
				return "";
			}
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log(e, IStatus.ERROR);
		}
		
		// fall through
		return "";
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IParameterMetaInfo[] getParameterMetaInfos()
	{
		try
		{
			// use reflection
			Method getTaskMetaInfoMethod = taskMetaInfo.getClass()
					.getMethod(ITaskMetaInfo.METHOD_ITASKMETAINFO_GET_PARAMETER_METAINFOS);
			Object returnValue = getTaskMetaInfoMethod.invoke(taskMetaInfo);
			
			// check the return value
			if (returnValue instanceof IParameterMetaInfo[])
			{
				return ( IParameterMetaInfo[]) returnValue;
			}
			else if (returnValue.getClass().isArray())
			{
				Object[] objects = (Object[]) returnValue;
				IParameterMetaInfo[] params = new IParameterMetaInfo[objects.length];
				for (int i = 0; i < objects.length; i++)
				{
					params[i] = new ParameterMetaInfoProxy(objects[i]);
				}
			}
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log(e, IStatus.ERROR);
		}
		
		// fall through
		return new IParameterMetaInfo[0];
	}

}
