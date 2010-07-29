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

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getDirection()
	 */
	@Override
	public String getDirection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getClazz()
	 */
	@Override
	public Class<?> getClazz()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getName()
	 */
	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getInitialValue()
	 */
	@Override
	public String getInitialValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IParameterMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Call an unparameterized method on an object with reflection
	 * 
	 * @param source the source object to call the method
	 * @param methodName the method identifier
	 * @return the return value from called method, may be null
	 */
	private Object callUnparametrizedReflectionMethod(Object source, String methodName)
	{
		return null;
	}

}
