package jadex.bdi.model;

/**
 *  Interface for property base model.
 */
public interface IMPropertybase extends IMElement
{
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public IMExpression[] getProperties();
}
