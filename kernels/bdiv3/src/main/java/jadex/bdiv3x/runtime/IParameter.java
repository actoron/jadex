package jadex.bdiv3x.runtime;


/**
 *  The interface for parameters.
 */
public interface IParameter extends IElement
{
	//-------- methods --------

	/**
	 *  Set a value of a parameter.
	 *  @param value The new value.
	 */
	public void setValue(Object value);

	/**
	 *  Get the value of a parameter.
	 *  @return The value.
	 */
	public Object	getValue();

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
