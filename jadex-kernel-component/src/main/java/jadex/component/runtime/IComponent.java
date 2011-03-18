package jadex.component.runtime;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.IFuture;
import jadex.component.model.MComponentType;

import java.util.Map;

/**
 *  Interface for applications.
 *  Defines methods of the application object,
 *  that may be used from the outside.
 */
public interface IComponent
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
	public IServiceProvider	getServiceProvider();

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
	// todo: remove?
	public String[] getAllImports();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	// todo: remove?
	public Map getArguments();

	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	// todo: remove?
	public Map getResults();
	
	/**
	 *  Get the application type.
	 */
	// todo: remove? replace with getModel()?
	public MComponentType	getApplicationType();
	
	/**
	 *  Schedule a step of the application component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step.
	 */
	// todo: belogs to interface?
	public IFuture scheduleStep(final IComponentStep step);
}
