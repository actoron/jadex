/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(taskMetaInfo,
				ITaskMetaInfo.METHOD_ITASKMETAINFO_GET_DESCRIPTION);
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IParameterMetaInfo[] getParameterMetaInfos()
	{
		try
		{
			Object returnValue = WorkspaceClassLoaderHelper
					.callUnparametrizedReflectionMethod(
							taskMetaInfo,
							ITaskMetaInfo.METHOD_ITASKMETAINFO_GET_PARAMETER_METAINFOS);
			
			// check the return value
			if (returnValue instanceof IParameterMetaInfo[])
			{
				return (IParameterMetaInfo[]) returnValue;
			}
			else if (returnValue != null && returnValue.getClass().isArray())
			{
				// create proxy objects
				Object[] objects = (Object[]) returnValue;
				IParameterMetaInfo[] params = new IParameterMetaInfo[objects.length];
				for (int i = 0; i < objects.length; i++)
				{
					params[i] = new ParameterMetaInfoProxy(objects[i]);
				}
				return params;
			}
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Problem during access on TaskmetaInfo in "+this.getClass().getSimpleName(), e, IStatus.ERROR);
		}
		
		// fall through
		return new IParameterMetaInfo[0];
	}

}
