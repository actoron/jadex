/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import java.lang.annotation.Annotation;

import jadex.editor.bpmn.editor.JadexBpmnEditor;

import org.eclipse.core.runtime.IStatus;

/**
 * @author claas
 *
 */
public class TaskMetaInfoProxy implements IEditorTaskMetaInfo
{

	Object metainfo;
	
	/**
	 * 
	 */
	public TaskMetaInfoProxy(Object taskMetaInfo)
	{
		super();
		this.metainfo = taskMetaInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		if(metainfo instanceof Annotation)
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo, "description");
		}
		else
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo,
				IEditorTaskMetaInfo.METHOD_ITASKMETAINFO_GET_DESCRIPTION);
		}
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IEditorParameterMetaInfo[] getParameterMetaInfos()
	{
		IEditorParameterMetaInfo[]	ret;
		try
		{
			Object	val;
			if(metainfo instanceof Annotation)
			{
				val = WorkspaceClassLoaderHelper
					.callUnparametrizedReflectionMethod(metainfo, "parameters");
			}
			else
			{
				val = WorkspaceClassLoaderHelper
					.callUnparametrizedReflectionMethod(metainfo,
						IEditorTaskMetaInfo.METHOD_ITASKMETAINFO_GET_PARAMETER_METAINFOS);
			}
				
			// check the return value
			if(val instanceof IEditorParameterMetaInfo[])
			{
				ret	= (IEditorParameterMetaInfo[])val;
			}
			else if(val!=null && val.getClass().isArray())
			{
				// create proxy objects
				Object[] objects = (Object[])val;
				IEditorParameterMetaInfo[] params = new IEditorParameterMetaInfo[objects.length];
				for(int i=0; i<objects.length; i++)
				{
					params[i]	= new ParameterMetaInfoProxy(objects[i]);
				}
				ret	= params;
			}
			else
			{
				JadexBpmnEditor.log("Problem during access on TaskmetaInfo in "+this.getClass().getSimpleName(), null, IStatus.ERROR);
				ret	= new IEditorParameterMetaInfo[0];				
			}
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Problem during access on TaskmetaInfo in "+this.getClass().getSimpleName(), e, IStatus.ERROR);
			ret	= new IEditorParameterMetaInfo[0];
		}
		
		return ret;
	}

}
