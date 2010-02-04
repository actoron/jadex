package jadex.application.runtime;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceContainer;

import java.util.Map;

/**
 *  Interface for applications.
 *  Defines methods of the application object,
 *  that may be used from the outside.
 */
public interface IApplication
{
	/**
	 *  Get the component identifier.
	 *  @return The component id of the application.
	 */
	public IComponentIdentifier	getComponentIdentifier();

	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer	getServiceContainer();

	/**
	 *  Get the logical component type for a given component id.
	 *  @param cid	The component id.
	 *  @return The logical type name of the component
	 *    as defined in the application descriptor.
	 */
	public String getComponentType(IComponentIdentifier cid);

	/**
	 *  Get the file name for a logical type name of a
	 *  subcomponent of this application.
	 *  @param type	The logical type name of the component
	 *    as defined in the application descriptor.
	 *  @return The file name.
	 */
	public String	getComponentFilename(String type);

	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	// Todo: remove?
	public String[] getAllImports();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 *  @deprecated
	 */
	public Map getArguments();

	/**
	 *  Get the name.
	 *  @deprecated Use getComponentIdentifier().getLocalName() instead.
	 */
	// todo: remove.
	public String	getName();
}
