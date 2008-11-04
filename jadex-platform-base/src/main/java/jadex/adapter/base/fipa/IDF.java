package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

import java.util.Date;

/**
 *  Interface for the directory facilitator (DF). Provides services for registering,
 *  modifying, deregistering and searching of agent resp. service descriptions.
 */
public interface IDF
{
	/**
	 *  Register an agent description.
	 *  @throws RuntimeException when the agent is already registered.
	 */
	public void	register(IDFAgentDescription adesc, IResultListener listener);
	
	/**
	 *  Deregister an agent description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	deregister(IDFAgentDescription adesc, IResultListener listener);
	
	/**
	 *  Modify an agent description.
	 *  @throws RuntimeException when the agent is not registered.
	 */
	public void	modify(IDFAgentDescription adesc, IResultListener listener);
	
	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions. 
	 */
	public void search(IDFAgentDescription adesc, ISearchConstraints con, IResultListener listener);

	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership);
	
	/**
	 *  Create a df service description.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param ownership The ownership.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @param properties The properties.
	 *  @return The service description.
	 */
	public IDFServiceDescription createDFServiceDescription(String name, String type, String ownership,
		String[] languages, String[] ontologies, String[] protocols, IProperty[] properties);

	/**
	 *  Create a df agent description.
	 *  @param agent The agent.
	 *  @param service The service.
	 *  @return The df agent description.
	 */
	public IDFAgentDescription createDFAgentDescription(IAgentIdentifier agent, IDFServiceDescription service);

	/**
	 *  Create a new df agent description.
	 *  @param agent The agent id.
	 *  @param services The services.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @return The agent description.
	 */
	public IDFAgentDescription	createDFAgentDescription(IAgentIdentifier agent, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime);

	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);

	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new agent identifier.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local);
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @param resolvers The resolvers.
	 *  @return The new agent identifier.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local, String[] addresses);
}
