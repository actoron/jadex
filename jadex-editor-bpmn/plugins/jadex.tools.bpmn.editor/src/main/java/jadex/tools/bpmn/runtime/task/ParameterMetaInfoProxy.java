/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

/**
 * @author claas
 * 
 */
public class ParameterMetaInfoProxy implements IParameterMetaInfo
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
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getDirection()
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
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getClazz()
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
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getName()
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
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getInitialValue()
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
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return WorkspaceClassLoaderHelper.getStringFromMethod(
				parameterMetaInfo,
				METHOD_IJADEXPARAMETERMETAINFO_GET_DESCRIPTION);
	}

}
