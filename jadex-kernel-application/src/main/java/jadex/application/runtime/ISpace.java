package jadex.application.runtime;

import jadex.application.model.MSpaceInstance;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.javaparser.IValueFetcher;


/**
 *  Interface for spaces.
 */
// Todo: Is only internal interface!?
public interface ISpace
{
	/**
	 *  Get the space name.
	 *  @return The name.
	 * /
	public String getName();*/
	
	/**
	 *  Initialize a space.
	 *  Called once, when the space is created.
	 */
//	public void	initSpace(IApplication context, MSpaceInstance config, IValueFetcher fetcher) throws Exception;
	public void	initSpace(IInternalAccess ia, MSpaceInstance config, IValueFetcher fetcher);

	/**
	 *  Get the context.
	 *  @return The context.
	 */
//	public IApplication getApplication();
	
	/**
	 *  Called from application component, when a component was added.
	 *  @param cid	The id of the added component.
	 *  @param type	The logical type name.
	 */
	public void	componentAdded(IComponentDescription desc);

	/**
	 *  Called from application component, when a component was removed.
	 *  @param cid	The id of the removed component.
	 */
	public void	componentRemoved(IComponentDescription desc);
	
	/**
	 *  Terminate the space.
	 *  Called, when the application component terminates.
	 */
	public void terminate();
}
