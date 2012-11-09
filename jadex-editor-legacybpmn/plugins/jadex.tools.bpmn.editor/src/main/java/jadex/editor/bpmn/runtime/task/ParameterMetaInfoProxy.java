/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import java.lang.annotation.Annotation;

/**
 * @author claas
 * 
 */
public class ParameterMetaInfoProxy implements IEditorParameterMetaInfo
{

	Object metainfo;

	/**
	 * 
	 */
	public ParameterMetaInfoProxy(Object parameterMetaInfo)
	{
		super();
		this.metainfo = parameterMetaInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDirection()
	 */
	@Override
	public String getDirection()
	{
		if(metainfo instanceof Annotation)
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo, "direction");
		}
		else
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_DIRECTION);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getClazz()
	 */
	@Override
	public Class<?> getClazz()
	{
		if(metainfo instanceof Annotation)
		{
			return WorkspaceClassLoaderHelper.getClassFromMethod(metainfo, "clazz");
		}
		else
		{
			return WorkspaceClassLoaderHelper.getClassFromMethod(metainfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_CLAZZ);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getName()
	 */
	@Override
	public String getName()
	{
		if(metainfo instanceof Annotation)
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo, "name");
		}
		else
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_NAME);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getInitialValue()
	 */
	@Override
	public String getInitialValue()
	{
		if(metainfo instanceof Annotation)
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo, "initialvalue");
		}
		else
		{
			return WorkspaceClassLoaderHelper.getStringFromMethod(metainfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_INITIAL_VALUE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDescription()
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
				METHOD_IJADEXPARAMETERMETAINFO_GET_DESCRIPTION);
		}
	}

}
