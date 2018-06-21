package jadex.bridge.service.types.df;


/**
 *  Interface for df service descriptions.
 */
public interface IDFServiceDescription
{
	/**
	 *  Get the name of this ServiceDescription.
	 *  @return name The name.
	 */
	public String getName();
	
	/**
	 *  Get the type of this ServiceDescription.
	 *  @return type The type.
	 */
	public String getType();

	/**
	 *  Get the ownership of this ServiceDescription.
	 *  @return ownership The ownership.
	 */
	public String getOwnership();
	
	/**
	 *  Get the properties of this ServiceDescription.
	 *  @return properties The properties
	 */
	public IProperty[] getProperties();
	
	/**
	 *  Get the languages of this ServiceDescription.
	 *  @return languages The languages.
	 */
	public String[] getLanguages();
	
	/**
	 *  Get the ontologies of this ServiceDescription.
	 *  @return ontologies The ontologies.
	 */
	public String[] getOntologies();
	
	/**
	 *  Get the protocols of this ServiceDescription.
	 *  @return protocols The protocols.
	 */
	public String[] getProtocols();
}
