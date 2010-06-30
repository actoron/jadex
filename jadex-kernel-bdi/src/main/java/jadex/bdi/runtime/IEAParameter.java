package jadex.bdi.runtime;

import jadex.commons.IFuture;


/**
 *  The interface for parameters.
 */
public interface IEAParameter extends IEAElement
{
	//-------- methods --------

	/**
	 *  Set a value of a parameter.
	 *  @param value The new value.
	 */
	public IFuture setValue(Object value);

	/**
	 *  Get the value of a parameter.
	 *  @return The value.
	 */
	public IFuture getValue();

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Get the value class.
	 *  @return The value class.
	 * /
	public Class	getClazz();*/
}

