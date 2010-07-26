package jadex.bdi.model;

/**
 *  Interface for element reference model.
 */
public interface IMElementReference extends IMReferenceableElement
{
	/**
	 *  Get concrete element name.
	 *  @return The concrete element name. 
	 */
	public String getConcrete();
}
