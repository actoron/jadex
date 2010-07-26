package jadex.bdi.model;

/**
 *  Interface for configuration model element.
 */
public interface IMConfigElement extends IMConfigParameterElement
{
	/**
	 *  Get the referenced element.
	 *  @return The referenced element name.
	 */
	public String getReference();
}
