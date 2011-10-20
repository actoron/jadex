package jadex.bridge.service.types.df;

public interface IProperty
{
	/**
	 *  Get the value of this Property.
	 * @return value
	 */
	public Object getValue();

	/**
	 *  Set the value of this Property.
	 * @param value the value to be set
	 */
	public void setValue(Object value);

	/**
	 *  Get the name of this Property.
	 * @return name
	 */
	public String getName();

	/**
	 *  Set the name of this Property.
	 * @param name the value to be set
	 */
	public void setName(String name);
}
