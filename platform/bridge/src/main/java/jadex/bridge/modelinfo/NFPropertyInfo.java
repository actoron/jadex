package jadex.bridge.modelinfo;

import java.util.List;
import java.util.Map;

import jadex.bridge.ClassInfo;

/**
 *  Info struct for a nf property.
 */
public class NFPropertyInfo
{
	/** The property name. */
	protected String name;
	
	/** The property class. */
	protected ClassInfo clazz;

	/** The parameters (optional). */
	protected  List<UnparsedExpression> parameters;
	
	/**
	 *  Create a new property.
	 */
	public NFPropertyInfo()
	{
	}
	
	/**
	 *  Create a new property.
	 *  @param name The name.
	 *  @param clazz The clazz.
	 */
	public NFPropertyInfo(String name, ClassInfo clazz, List<UnparsedExpression> parameters)
	{
		this.name = name;
		this.clazz = clazz;
		this.parameters = parameters;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the parameters.
	 *  @return The parameters
	 */
	public List<UnparsedExpression> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Set the parameters.
	 *  @param parameters The parameters to set
	 */
	public void setParameters(List<UnparsedExpression> parameters)
	{
		this.parameters = parameters;
	}
}
