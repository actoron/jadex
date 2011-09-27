/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

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
//			AnnotaionInvocationHandler	Proxy.getInvocationHandler(metainfo);
			return ""+metainfo;
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
		try
		{
			if(metainfo instanceof Annotation)
			{
//				AnnotaionInvocationHandler	Proxy.getInvocationHandler(metainfo);
				throw new RuntimeException("Annotation MetaInfo not yet supported.");
			}
			else
			{
				Object returnValue = WorkspaceClassLoaderHelper
						.callUnparametrizedReflectionMethod(
								metainfo,
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
		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Problem during access on TaskmetaInfo in "+this.getClass().getSimpleName(), e, IStatus.ERROR);
		}
		
		// fall through
		return new IEditorParameterMetaInfo[0];
	}

}
