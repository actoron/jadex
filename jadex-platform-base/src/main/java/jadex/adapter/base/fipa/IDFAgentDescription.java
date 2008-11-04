package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;

import java.util.Date;

/**
 *  Interface for df agent descriptions.
 */
public interface IDFAgentDescription
{
	/**
	 *  Get the languages of this AgentDescription.
	 *  @return languages
	 */
	public String[] getLanguages();

	/**
	 *  Get the agentidentifier of this AgentDescription.
	 * @return agentidentifier
	 */
	public IAgentIdentifier getName();

	/**
	 *  Get the ontologies of this AgentDescription.
	 * @return ontologies
	 */
	public String[] getOntologies();

	/**
	 *  Get the services of this AgentDescription.
	 * @return services
	 */
	public IDFServiceDescription[] getServices();

	/**
	 *  Get the lease-time of this AgentDescription.
	 * @return lease-time
	 */
	public Date getLeaseTime();

	/**
	 *  Get the protocols of this AgentDescription.
	 * @return protocols
	 */
	public String[] getProtocols();
}
