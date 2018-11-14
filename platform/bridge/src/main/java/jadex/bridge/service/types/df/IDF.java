package jadex.bridge.service.types.df;

import java.util.Date;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Interface for the directory facilitator (DF). Provides services for registering,
 *  modifying, deregistering and searching of component resp. service descriptions.
 */
@GuiClassName("jadex.tools.dfbrowser.DFBrowserPanel")
@Service(system=true)
public interface IDF
{
	/**
	 *  Register an component description.
	 *  @throws RuntimeException when the component is already registered.
	 */
	public IFuture<IDFComponentDescription> register(IDFComponentDescription adesc);
	
	/**
	 *  Deregister an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture<Void> deregister(IDFComponentDescription adesc);
	
	/**
	 *  Modify an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture<IDFComponentDescription> modify(IDFComponentDescription adesc);
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture<IDFComponentDescription[]> search(IDFComponentDescription adesc, ISearchConstraints con);
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture<IDFComponentDescription[]> search(IDFComponentDescription adesc, ISearchConstraints con, boolean remote);

	// todo: remove following methods
	
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
	 *  Create a df component description.
	 *  @param component The component.
	 *  @param service The service.
	 *  @return The df component description.
	 */
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription service);

	/**
	 *  Create a df component description.
	 *  @param component The component.
	 *  @param service The service.
	 *  @return The df component description.
	 */
	public IDFComponentDescription createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription service, long leasetime);

	/**
	 *  Create a new df component description.
	 *  @param component The component id.
	 *  @param services The services.
	 *  @param languages The languages.
	 *  @param ontologies The ontologies.
	 *  @param protocols The protocols.
	 *  @return The component description.
	 */
	public IDFComponentDescription	createDFComponentDescription(IComponentIdentifier component, IDFServiceDescription[] services,
		String[] languages, String[] ontologies, String[] protocols, Date leasetime);

	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);

	/**
	 *  Create an component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new component identifier.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local);
*/	
	/**
	 *  Create an component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @param resolvers The resolvers.
	 *  @return The new component identifier.
	 * /
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses);
*/
}
