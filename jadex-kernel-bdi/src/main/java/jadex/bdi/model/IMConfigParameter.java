package jadex.bdi.model;

/**
 *  Interface for config parameter model.
 */
public interface IMConfigParameter extends IMParameter
{
	/**
	 *  Get the referenced element.
	 *  @return The referenced element name.
	 */
	public String getReference();
}
