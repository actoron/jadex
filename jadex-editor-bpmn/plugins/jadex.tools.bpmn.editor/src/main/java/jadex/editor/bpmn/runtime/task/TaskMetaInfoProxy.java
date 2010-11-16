/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.JadexBpmnEditor;

import org.eclipse.core.runtime.IStatus;

/**
 * @author claas
 *
 */
public class TaskMetaInfoProxy implements IEditorTaskMetaInfo
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
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(taskMetaInfo,
				IEditorTaskMetaInfo.METHOD_ITASKMETAINFO_GET_DESCRIPTION);
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IEditorParameterMetaInfo[] getParameterMetaInfos()
	{
		try
		{
			Object returnValue = WorkspaceClassLoaderHelper
					.callUnparametrizedReflectionMethod(
							taskMetaInfo,
							IEditorTaskMetaInfo.METHOD_ITASKMETAINFO_GET_PARAMETER_METAINFOS);
			
			// check the return value
			if (returnValue instanceof IEditorParameterMetaInfo[])
			{
				return (IEditorParameterMetaInfo[]) returnValue;
			}
			else if (returnValue != null && returnValue.getClass().isArray())
			{
				// create proxy objects
				Object[] objects = (Object[]) returnValue;
				IEditorParameterMetaInfo[] params = new IEditorParameterMetaInfo[objects.length];
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
		return new IEditorParameterMetaInfo[0];
	}

}
