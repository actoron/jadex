/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

/**
 * @author claas
 * 
 */
public class ParameterMetaInfoProxy implements IEditorParameterMetaInfo
{

	Object parameterMetaInfo;

	/**
	 * 
	 */
	public ParameterMetaInfoProxy(Object parameterMetaInfo)
	{
		super();
		this.parameterMetaInfo = parameterMetaInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDirection()
	 */
	@Override
	public String getDirection()
	{
		return WorkspaceClassLoaderHelper
				.getStringFromMethod(parameterMetaInfo,
						METHOD_IJADEXPARAMETERMETAINFO_GET_DIRECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getClazz()
	 */
	@Override
	public Class<?> getClazz()
	{
		return WorkspaceClassLoaderHelper.getClassFromMethod(parameterMetaInfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_CLAZZ);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getName()
	 */
	@Override
	public String getName()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(
				parameterMetaInfo, METHOD_IJADEXPARAMETERMETAINFO_GET_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getInitialValue()
	 */
	@Override
	public String getInitialValue()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(
				parameterMetaInfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_INITIAL_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(
				parameterMetaInfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_DESCRIPTION);
	}

}
