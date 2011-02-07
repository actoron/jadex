package jadex.base.fipa;

import jadex.base.gui.componentviewer.dfservice.DFBrowserPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;
import jadex.commons.service.annotation.GuiClass;

import java.util.Date;

/**
 *  Interface for the directory facilitator (DF). Provides services for registering,
 *  modifying, deregistering and searching of component resp. service descriptions.
 */
@GuiClass(value=DFBrowserPanel.class)
public interface IDF	extends IService
{
	/**
	 *  Register an component description.
	 *  @throws RuntimeException when the component is already registered.
	 */
	public IFuture register(IDFComponentDescription adesc);
	
	/**
	 *  Deregister an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture deregister(IDFComponentDescription adesc);
	
	/**
	 *  Modify an component description.
	 *  @throws RuntimeException when the component is not registered.
	 */
	public IFuture modify(IDFComponentDescription adesc);
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture search(IDFComponentDescription adesc, ISearchConstraints con);
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions. 
	 */
	public IFuture search(IDFComponentDescription adesc, ISearchConstraints con, boolean remote);

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
