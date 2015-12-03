package jadex.bridge.service.types.df;

import java.util.Date;

import jadex.bridge.IComponentIdentifier;

/**
 *  Interface for df component descriptions.
 */
public interface IDFComponentDescription
{
	/**
	 *  Get the languages of this ComponentDescription.
	 *  @return languages
	 */
	public String[] getLanguages();

	/**
	 *  Get the componentidentifier of this ComponentDescription.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getName();

	/**
	 *  Get the ontologies of this ComponentDescription.
	 * @return ontologies
	 */
	public String[] getOntologies();

	/**
	 *  Get the services of this ComponentDescription.
	 * @return services
	 */
	public IDFServiceDescription[] getServices();

	/**
	 *  Get the lease-time of this ComponentDescription.
	 * @return lease-time
	 */
	public Date getLeaseTime();

	/**
	 *  Get the protocols of this ComponentDescription.
	 * @return protocols
	 */
	public String[] getProtocols();
}
